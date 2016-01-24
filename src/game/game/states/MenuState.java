package game.game.states;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import engine.BitMapFont;
import engine.DisplayableList;
import engine.entities.AABBRectangle;
import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.game.Controls;
import engine.game.Controls.ControlsListener;
import engine.game.Controls.MouseListener;
import engine.game.Game;
import engine.game.states.GameState;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeQuadTexture;
import engine.util.Vector3;
import game.menu.MenuButton;
import game.menu.MenuTextField;

public abstract class MenuState extends GameState implements MouseListener, ControlsListener {

	protected ShaderProgram programScreen;
	protected ShaderProgram programTexCameraInstanced;

	protected ShapeQuadTexture mouseShape;
	protected EntityActor mouseCursor;

	protected BitMapFont font;

	protected ShapeQuadTexture buttonShape;
	protected DisplayableList<MenuButton> buttons;
	protected MenuButton selectedButton;

	@Override
	public void dispose() {
		Controls.removeMouseListener(this);
		Controls.removeControlsListener(this);

		for (MenuButton button : buttons) {
			button.dispose();
		}
		buttonShape.dispose();

		programScreen.dispose();
		programTexCameraInstanced.dispose();
		mouseShape.dispose();
		font.dispose();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void init() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Controls.addMouseListener(this);
		Controls.addControlsListener(this);

		current_camera = new Camera(45.0f, (float) Game.getInstance().getWidth()
				/ (float) Game.getInstance().getHeight(), 0f, 100f);

		programScreen = new ShaderProgram("texture_camera", "texture", "texture_camera");
		programTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture", "texture_camera_instanced");

		buttonShape = new ShapeQuadTexture(programScreen, "menu/button.png");
		mouseShape = new ShapeQuadTexture(programScreen, "menu/cursor.png");

		mouseCursor = new EntityActor(mouseShape);
		mouseCursor.scale.set(0.075f);

		font = new BitMapFont(new ShapeInstancedSprite(programTexCameraInstanced, "scumm_font.png", 128, 256, 8, 11));

		buttons = new DisplayableList<MenuButton>();
	}

	@Override
	public void onMouseMoved(int x, int y) {
		float f_x = x * 2f / Game.getInstance().getWidth();
		float f_y = y * 2f / Game.getInstance().getHeight();

		mouseCursor.position.add(f_x, f_y, 0f);
	}

	@Override
	public void onMousePress(int button) {
		if (button == 0) {
			AABBRectangle cursorRect = getMouseRectangle();

			for (MenuButton buttonActor : buttons) {
				AABBRectangle buttonRect = new AABBRectangle(buttonActor);
				if (cursorRect.collide(buttonRect)) {
					selectedButton = buttonActor;
					selectedButton.onButtonPress();
					break;
				}
			}
		} // button 0
	}

	@Override
	public void onMouseRelease(int button) {
		AABBRectangle cursorRect = getMouseRectangle();

		if (button == 0) {
			if (selectedButton != null) {
				AABBRectangle buttonRect = new AABBRectangle(selectedButton);

				boolean collide = cursorRect.collide(buttonRect);
				selectedButton.onButtonRelease(collide);

				if (!collide) {
					if (selectedButton instanceof MenuTextField) {
						MenuTextField textField = (MenuTextField) selectedButton;
						textField.setEditing(false);
					}
					selectedButton = null;
				}
			}
		} // button 0
	}

	@Override
	public void render() {
		current_camera.apply();
		buttons.render();
		mouseCursor.render();
	}

	@Override
	public void update(float dt) {
		current_camera.update(dt);

		mouseCursor.update(dt);
		if (mouseCursor.position.getX() < -1f) {
			mouseCursor.position.setX(-1f);
		}
		else if (mouseCursor.position.getX() > 1f) {
			mouseCursor.position.setX(1f);
		}
		if (mouseCursor.position.getY() < -1f) {
			mouseCursor.position.setY(-1f);
		}
		else if (mouseCursor.position.getY() > 1f) {
			mouseCursor.position.setY(1f);
		}

		buttons.update(dt);
	}

	protected AABBRectangle getMouseRectangle() {
		Vector3 cursorScale = new Vector3(0f);
		Vector3 cursorPos = new Vector3(mouseCursor.position);
		cursorPos.addX(mouseCursor.scale.getScale(0.5f).getNegate().getX());
		cursorPos.addY(mouseCursor.scale.getScale(0.5f).getY());

		return new AABBRectangle(cursorPos, cursorScale);
	}

	@Override
	public void onKeyPress(int key) {
	}

	@Override
	public void onKeyRelease(int key) {
		if (key == Keyboard.KEY_ESCAPE) {
			Game.end();
		}
	}
}
