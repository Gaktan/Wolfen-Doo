package engine.game;

import static org.lwjgl.input.Keyboard.KEY_1;
import static org.lwjgl.input.Keyboard.KEY_2;
import static org.lwjgl.input.Keyboard.KEY_A;
import static org.lwjgl.input.Keyboard.KEY_ADD;
import static org.lwjgl.input.Keyboard.KEY_D;
import static org.lwjgl.input.Keyboard.KEY_ESCAPE;
import static org.lwjgl.input.Keyboard.KEY_LCONTROL;
import static org.lwjgl.input.Keyboard.KEY_Q;
import static org.lwjgl.input.Keyboard.KEY_R;
import static org.lwjgl.input.Keyboard.KEY_S;
import static org.lwjgl.input.Keyboard.KEY_SPACE;
import static org.lwjgl.input.Keyboard.KEY_SUBTRACT;
import static org.lwjgl.input.Keyboard.KEY_W;
import static org.lwjgl.input.Keyboard.KEY_Z;

import org.lwjgl.input.Mouse;

import engine.entities.Camera;

public class Player extends Camera implements ControlsListener, MouseListener {

	// Mouse
	protected int	lastMouseX, lastMouseY;
	protected int	movingKeyPressed;
	protected float	leftGoal;

	protected float	rightGoal;
	protected float	forwardGoal;
	protected float	backwardGoal;

	public Player(float fov, float aspect, float zNear, float zFar) {
		super(fov, aspect, zNear, zFar);

		scale.set(.5f, .5f, .5f);

		Controls.addControlsListener(this);
		Controls.addMouseListener(this);

		if (Controls.getLocale().startsWith("fr")) {
			k_forward = KEY_Z;
			k_left = KEY_Q;
		}
	}

	@Override
	public void onKeyPress(int key) {

		if (key == k_forward) {
			forwardGoal = FORWARD_SPEED;
			movingKeyPressed++;
		} else if (key == k_back) {
			backwardGoal = -FORWARD_SPEED;
			movingKeyPressed++;
		} else if (key == k_left) {
			leftGoal = -SIDEWAYS_SPEED;
			movingKeyPressed++;
		} else if (key == k_right) {
			rightGoal = SIDEWAYS_SPEED;
			movingKeyPressed++;
		} else if (key == KEY_SPACE) {
			velocity.setY(UPDOWN_SPEED);
		} else if (key == KEY_LCONTROL) {
			velocity.setY(-UPDOWN_SPEED);
		}
	}

	@Override
	public void onKeyRelease(int key) {

		if (key == k_forward) {
			forwardGoal = 0;
			movingKeyPressed--;
		} else if (key == k_back) {
			backwardGoal = 0;
			movingKeyPressed--;
		} else if (key == k_left) {
			leftGoal = 0;
			movingKeyPressed--;
		} else if (key == k_right) {
			rightGoal = 0;
			movingKeyPressed--;
		} else if (key == KEY_SPACE) {
			velocity.setY(0);
		} else if (key == KEY_LCONTROL) {
			velocity.setY(0);
		} else if (key == KEY_R) {
			GameWolfen.getInstance().currentWeapon.forceReload();
		} else if (key == KEY_ESCAPE) {
			GameWolfen.end();
		} else if (key == KEY_ADD) {
			fov++;
		} else if (key == KEY_SUBTRACT) {
			fov--;
		} else if (key == KEY_1) {
			GameWolfen.getInstance().setZfar(zFar - 0.1f);
		} else if (key == KEY_2) {
			GameWolfen.getInstance().setZfar(zFar + 0.1f);
		}
	}

	@Override
	public void onMouseMoved(int x, int y) {
		int mouseMovedX = Mouse.getX() - lastMouseX;
		int mouseMovedY = Mouse.getY() - lastMouseY;

		viewAngle.pitch -= mouseMovedY * mouseSensitivity;
		viewAngle.yaw += mouseMovedX * mouseSensitivity;

		viewAngle.normalize();

		if (Mouse.isButtonDown(2))
			lockMouse = !lockMouse;

		if (Mouse.isButtonDown(0)) {
			GameWolfen.getInstance().currentWeapon.fire();
		}

		if (lockMouse)
			Mouse.setCursorPosition(Game.getInstance().getWidth() / 2, Game.getInstance().getHeight() / 2);

		lastMouseX = Mouse.getX();
		lastMouseY = Mouse.getY();
	}

	@Override
	public boolean update(float elapsedTime) {
		GameWolfen.getInstance().currentWeapon.setMoving(movingKeyPressed != 0);

		movementGoal.set(leftGoal + rightGoal, 0, forwardGoal + backwardGoal);

		return super.update(elapsedTime);
	}

	protected static float			mouseSensitivity	= 0.2f;
	protected static boolean		lockMouse			= true;
	// Controls
	protected static final float	FORWARD_SPEED		= 0.4f;
	protected static final float	SIDEWAYS_SPEED		= 0.3f;

	protected static float			UPDOWN_SPEED		= 0.6f;

	protected static int			k_forward			= KEY_W;

	protected static int			k_left				= KEY_A;

	protected static int			k_back				= KEY_S;

	protected static int			k_right				= KEY_D;
}
