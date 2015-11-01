package engine.game;

import static org.lwjgl.input.Keyboard.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.EntityDoor;
import engine.entities.EntityProjctile;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.MatrixUtil;

/**
 * Dirty class used to handle controls <br>
 * To be redone at some point
 * @author Gaktan
 */
public class Controls {

	private static float camSpeed = 0.4f;
	private static float upDownSpeed = 0.01f;
	private static float sensitivity = 0.2f;
	private static int lastX;
	private static int lastY;

	private static boolean lockMouse = true;
	private static boolean actionPress = false;

	public static void update(Camera camera, float dt){

		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			
			if (Keyboard.getEventKeyState())
				handlePress(key, camera, dt);
			
			else
				handleRelease(key, camera, dt);
		}

		if (isKeyDown(KEY_SPACE)) {
			Vector3f pos = camera.getPosition();
			pos.y += upDownSpeed * dt;
			camera.setPosition(pos);
		}

		if (isKeyDown(KEY_LCONTROL)) {
			Vector3f pos = camera.getPosition();
			pos.y += -upDownSpeed * dt;
			camera.setPosition(pos);
		}

		if (isKeyDown(KEY_ADD))
			camera.setFov(camera.getFov() + 1);
		
		if (isKeyDown(KEY_SUBTRACT))
			camera.setFov(camera.getFov() - 1);

		if (isKeyDown(KEY_ESCAPE))
			Game.end();
		
		handleMouse(camera);  	
	}

	private static void handleMouse(Camera camera) {
		int mouseMovedX = Mouse.getX() - lastX;
		int mouseMovedY = Mouse.getY() - lastY;

		camera.viewAngle.pitch -= mouseMovedY * sensitivity;
		camera.viewAngle.yaw += mouseMovedX * sensitivity;

		camera.viewAngle.normalize();

		if (Mouse.isButtonDown(2))
			lockMouse = !lockMouse;
		
		if (actionPress) {
			actionPress = false;
			Vector3f lineVector = new Vector3f();

			EAngle angle = new EAngle(camera.viewAngle);
			angle.yaw -= 45;
			angle.pitch = -angle.pitch;

			Vector3f forward = angle.toVector();

			forward.normalise();

			Vector3f right = new Vector3f();
			Vector3f.cross(MatrixUtil.Y_AXIS, forward, right);

			Vector3f.add(forward, right, lineVector);

			lineVector.normalise();
			
			Entity e = ((GameWolfen) GameWolfen.getInstance()).map.rayCast(new Vector3f(camera.position), lineVector, 1.5f);
			
			if (e instanceof EntityDoor) {
				((EntityDoor) e).toggle();
			}
		}

		if (Mouse.isButtonDown(0)) {
			Vector3f linePosition = new Vector3f(camera.getPosition());
			Vector3f lineVector = new Vector3f();

			float diff = 0.1f;

			linePosition.x += MathUtil.random(-diff, diff);
			linePosition.y += MathUtil.random(-diff, diff);
			linePosition.z += MathUtil.random(-diff, diff);

			EAngle angle = new EAngle(camera.viewAngle);
			angle.yaw -= 45;
			angle.pitch = -angle.pitch;

			Vector3f forward = angle.toVector();

			forward.normalise();

			Vector3f right = new Vector3f();
			Vector3f.cross(MatrixUtil.Y_AXIS, forward, right);

			Vector3f.add(forward, right, lineVector);

			lineVector.normalise();

			((GameWolfen) GameWolfen.getInstance()).ac.add(new EntityProjctile(linePosition, lineVector, 
					((GameWolfen) GameWolfen.getInstance()).map));
		}

		if (lockMouse)
			Mouse.setCursorPosition(Game.getInstance().getWidth() / 2, Game.getInstance().getHeight() / 2);

		lastX = Mouse.getX();
		lastY = Mouse.getY(); 
	}

	private static void handleRelease(int key, Camera camera, float dt) {
		if (key == KEY_Z)
			camera.movementGoal.z = 0;

		if (key == KEY_S)
			camera.movementGoal.z = 0;

		if (key == KEY_Q)
			camera.movementGoal.x = 0;

		if (key == KEY_D)
			camera.movementGoal.x = 0;
		
		if(key == KEY_E)
			actionPress = false;
	}

	private static void handlePress(int key, Camera camera, float dt) {
		if (key == KEY_Z)
			camera.movementGoal.z = camSpeed;

		if (key == KEY_S)
			camera.movementGoal.z = -camSpeed;

		if (key == KEY_Q)
			camera.movementGoal.x = -camSpeed;

		if (key == KEY_D)
			camera.movementGoal.x = camSpeed;
		
		if(key == KEY_E)
			actionPress = true;

		if (key == KEY_UP)
			((GameWolfen)GameWolfen.getInstance()).animatedActorTest.setAnimation("a_running_back");

		if (key == KEY_DOWN){
			((GameWolfen)GameWolfen.getInstance()).animatedActorTest.setAnimation("a_running_front");
		}

		if (key == KEY_LEFT){
			((GameWolfen)GameWolfen.getInstance()).animatedActorTest.setAnimation("a_running_left");
		}

		if (key == KEY_RIGHT){
			((GameWolfen)GameWolfen.getInstance()).animatedActorTest.setAnimation("a_running_right");
		}
	}
}