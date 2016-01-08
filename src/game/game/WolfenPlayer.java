package game.game;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.EntityDoor;
import engine.game.Controls;
import engine.game.Player;
import engine.game.states.GameStateManager;
import engine.generator.Map;
import engine.util.Vector3;
import engine.weapons.Weapon;
import game.game.states.MainMenuState;
import game.weapons.WeaponFist;
import game.weapons.WeaponRevolver;
import game.weapons.WeaponShotgun;

public class WolfenPlayer extends Player {

	protected static float mouseSensitivity = 0.2f;

	public static final int WEAPON_FIST = 0;
	public static final int WEAPON_REVOLVER = 1;
	public static final int WEAPON_SHOTGUN = 2;

	// Speed in every directions
	protected static final float FORWARD_SPEED = 0.4f;
	protected static final float SIDEWAYS_SPEED = 0.3f;
	protected static final float UPDOWN_SPEED = 0.6f;

	protected int movingKeyPressed;

	// Weapon
	protected int weaponIndex;
	protected ArrayList<Weapon> weaponList;

	// Controls
	protected static int k_forward = Keyboard.KEY_W;
	protected static int k_left = Keyboard.KEY_A;
	protected static int k_back = Keyboard.KEY_S;
	protected static int k_right = Keyboard.KEY_D;

	protected Map map;

	public WolfenPlayer(Camera camera) {
		this(camera, null);
	}

	public WolfenPlayer(Camera camera, Map map) {
		super(camera);

		this.map = map;
		if (map != null) {
			position.set(map.getStartingPoint());
		}

		weaponList = new ArrayList<Weapon>();
		weaponList.add(new WeaponFist(this));
		weaponList.add(new WeaponRevolver(this));
		weaponList.add(new WeaponShotgun(this));

		weaponIndex = 0;

		if (Controls.getLocale().startsWith("fr")) {
			k_forward = Keyboard.KEY_Z;
			k_left = Keyboard.KEY_Q;
		}
	}

	@Override
	public void render() {
		super.render();
		weaponList.get(weaponIndex).render();
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);

		Weapon currentWeapon = weaponList.get(weaponIndex);

		currentWeapon.setMoving(isMoving());
		currentWeapon.update(dt);

		// Collision on map
		Vector3 resolution = map.testCollision(collisionSphere);
		position.add(resolution);

		return result;
	}

	public Weapon getWeapon(int weaponNumber) {
		if (weaponNumber >= weaponList.size() || weaponNumber < 0) {
			return null;
		}
		return weaponList.get(weaponNumber);
	}

	public boolean isMoving() {
		return movingKeyPressed != 0;
	}

	// -- Listeners --
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
		else if (key == Keyboard.KEY_SPACE) {
			upGoal = UPDOWN_SPEED;
		}
		else if (key == Keyboard.KEY_LCONTROL) {
			downGoal = -UPDOWN_SPEED;
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
		else if (key == Keyboard.KEY_SPACE) {
			upGoal = 0f;
		}
		else if (key == Keyboard.KEY_LCONTROL) {
			downGoal = 0f;
		}
		else if (key == Keyboard.KEY_R) {
			weaponList.get(weaponIndex).forceReload();
		}
		else if (key == Keyboard.KEY_E) {
			Entity e = map.rayCast(position, getViewAngle().toVector(), 1f);
			if (e == null) {
				return;
			}

			if (e instanceof EntityDoor) {
				EntityDoor door = (EntityDoor) e;
				door.toggle();
			}
		}
		else if (key == Keyboard.KEY_ESCAPE) {
			if (GameWolfen.SKIP_MENU) {
				GameWolfen.end();
			}
			else {
				GameStateManager.changeGameState(new MainMenuState());
			}
		}
		else if (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_0) {

			int weaponIndex = key - 2;

			if (weaponIndex != this.weaponIndex && weaponIndex < weaponList.size()) {
				this.weaponIndex = weaponIndex;
				weaponList.get(this.weaponIndex).setFiring(false);
			}
		}
	}

	@Override
	public void onMouseMoved(int x, int y) {
		camera.getViewAngle().pitch -= y * mouseSensitivity;
		camera.getViewAngle().yaw += x * mouseSensitivity;

		camera.getViewAngle().normalize();
	}

	@Override
	public void onMousePress(int button) {
		if (button == 0) {
			weaponList.get(weaponIndex).setFiring(true);
		}
	}

	@Override
	public void onMouseRelease(int button) {
		if (button == 0) {
			weaponList.get(weaponIndex).setFiring(false);
		}
	}
}
