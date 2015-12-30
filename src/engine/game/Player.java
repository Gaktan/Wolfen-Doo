package engine.game;

import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;
import static org.lwjgl.input.Keyboard.KEY_LCONTROL;
import static org.lwjgl.input.Keyboard.KEY_Q;
import static org.lwjgl.input.Keyboard.KEY_R;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_SPACE;
import static org.lwjgl.input.Keyboard.KEY_W;
import static org.lwjgl.input.Keyboard.KEY_Z;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import engine.entities.AABBSphere;
import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.game.Controls.ControlsListener;
import engine.game.Controls.MouseListener;
import engine.game.states.GameStateManager;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;
import engine.weapons.Weapon;
import game.game.GameWolfen;
import game.game.states.MainMenuState;
import game.weapons.WeaponFist;
import game.weapons.WeaponRevolver;
import game.weapons.WeaponShotgun;

public class Player extends EntityActor implements ControlsListener, MouseListener {

	public static final int WEAPON_FIST = 0;
	public static final int WEAPON_REVOLVER = 1;
	public static final int WEAPON_SHOTGUN = 2;

	protected static final float SLIPPERY_LEVEL = 100.0f;
	protected static float mouseSensitivity = 0.2f;
	// Controls
	protected static final float FORWARD_SPEED = 0.4f;

	protected static final float SIDEWAYS_SPEED = 0.3f;
	protected static final float UPDOWN_SPEED = 0.6f;
	protected static int k_forward = KEY_W;
	protected static int k_left = KEY_A;
	protected static int k_back = KEY_S;
	protected static int k_right = KEY_D;

	protected Camera camera;
	protected Vector3 movement;
	protected Vector3 movementGoal;
	protected AABBSphere collisionSphere;

	// Mouse
	protected int lastMouseX, lastMouseY;
	protected int movingKeyPressed;

	protected float leftGoal;
	protected float rightGoal;
	protected float forwardGoal;
	protected float backwardGoal;

	protected Weapon currentWeapon;
	protected int weaponIndex;
	protected ArrayList<Weapon> weaponList;

	public Player(Camera camera) {
		super(new ShapeQuadTexture(ShaderProgram.getProgram("texture"), "wall.png"));

		weaponList = new ArrayList<Weapon>();

		movementGoal = new Vector3();
		movement = new Vector3();
		this.camera = camera;
		this.camera.position = position;

		rotation.setX((float) Math.toRadians(-90));

		scale.set(0.5f);

		collisionSphere = new AABBSphere(position, scale);

		if (Controls.getLocale().startsWith("fr")) {
			k_forward = KEY_Z;
			k_left = KEY_Q;
		}

		// TODO: Game/engine dependency
		weaponList.add(new WeaponFist(this));
		weaponList.add(new WeaponRevolver(this));
		weaponList.add(new WeaponShotgun(this));

		weaponIndex = 0;

		currentWeapon = weaponList.get(weaponIndex);
	}

	public EAngle getViewAngle() {
		return camera.getViewAngle();
	}

	@Override
	public void onKeyPress(int key) {

		if (key == k_forward) {
			forwardGoal = FORWARD_SPEED;
			movingKeyPressed++;
		}
		else if (key == k_back) {
			backwardGoal = -FORWARD_SPEED;
			movingKeyPressed++;
		}
		else if (key == k_left) {
			leftGoal = -SIDEWAYS_SPEED;
			movingKeyPressed++;
		}
		else if (key == k_right) {
			rightGoal = SIDEWAYS_SPEED;
			movingKeyPressed++;
		}
		else if (key == KEY_SPACE) {
			velocity.setY(UPDOWN_SPEED);
		}
		else if (key == KEY_LCONTROL) {
			velocity.setY(-UPDOWN_SPEED);
		}
	}

	@Override
	public void onKeyRelease(int key) {

		if (key == k_forward) {
			forwardGoal = 0;
			movingKeyPressed--;
		}
		else if (key == k_back) {
			backwardGoal = 0;
			movingKeyPressed--;
		}
		else if (key == k_left) {
			leftGoal = 0;
			movingKeyPressed--;
		}
		else if (key == k_right) {
			rightGoal = 0;
			movingKeyPressed--;
		}
		else if (key == KEY_SPACE) {
			velocity.setY(0);
		}
		else if (key == KEY_LCONTROL) {
			velocity.setY(0);
		}
		else if (key == KEY_R) {
			currentWeapon.forceReload();
		}
		else if (key == KEY_ESCAPE) {
			// TODO: Engine/Game dependency
			if (GameWolfen.SKIP_MENU) {
				Game.end();
			}
			else {
				GameStateManager.changeGameState(new MainMenuState());
			}
		}
		else if (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_0) {

			int weaponIndex = key - 2;

			if (weaponIndex != this.weaponIndex && weaponIndex < weaponList.size()) {
				this.weaponIndex = weaponIndex;
				currentWeapon.setFiring(false);
				currentWeapon = weaponList.get(this.weaponIndex);
			}
		}
	}

	@Override
	public void onMouseMoved(int x, int y) {
		int mouseMovedX = x - lastMouseX;
		int mouseMovedY = y - lastMouseY;

		camera.getViewAngle().pitch -= mouseMovedY * mouseSensitivity;
		camera.getViewAngle().yaw += mouseMovedX * mouseSensitivity;

		camera.getViewAngle().normalize();

		lastMouseX = x;
		lastMouseY = y;
	}

	@Override
	public void onMousePress(int button) {
		if (button == 0) {
			currentWeapon.setFiring(true);
		}
	}

	@Override
	public void onMouseRelease(int button) {
		if (button == 0) {
			currentWeapon.setFiring(false);
		}
	}

	@Override
	public void render() {
		super.render();
		currentWeapon.render();
	}

	@Override
	public boolean update(float elapsedTime) {
		currentWeapon.setMoving(movingKeyPressed != 0);
		currentWeapon.update(elapsedTime);

		movementGoal.set(leftGoal + rightGoal, 0, forwardGoal + backwardGoal);

		boolean result = super.update(elapsedTime);

		float dt = elapsedTime / SLIPPERY_LEVEL;

		movement = MathUtil.approach(movement, movementGoal, dt);

		Vector3 forward = camera.getViewAngle().toVector();

		forward.setY(0f);
		forward.normalize();

		Vector3 right = Matrix4.Y_AXIS.getCross(forward);

		forward.scale(movement.getX());
		right.scale(movement.getZ());

		float y = velocity.getY();
		velocity = forward.getAdd(right);
		velocity.setY(y);

		rotation.setZ((float) Math.toRadians(-camera.getViewAngle().yaw));

		return result;
	}

	public Weapon getWeapon(int weaponNumber) {
		if (weaponNumber >= weaponList.size() || weaponNumber < 0) {
			return null;
		}

		return weaponList.get(weaponNumber);
	}

	public AABBSphere getCollisionSphere() {
		return collisionSphere;
	}
}
