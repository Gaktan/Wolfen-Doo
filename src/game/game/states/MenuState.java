package game.game.states;

import org.lwjgl.opengl.GL11;

import engine.BitMapFont;
import engine.Displayable;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.entities.AABBRectangle;
import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.game.Controls;
import engine.game.Controls.ControlsCharListener;
import engine.game.Controls.MouseListener;
import engine.game.Game;
import engine.game.states.GameState;
import engine.game.states.GameStateManager;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeQuadTexture;
import engine.util.Vector3;
import game.menu.MenuButton;
import game.menu.MenuButton.ButtonRelease;
import game.menu.MenuTextField;

public class MenuState extends GameState implements MouseListener {

	protected EntityActor mouseCursor;
	protected DisplayableText gameTitle;
	protected DisplayableText seedLabel;

	protected DisplayableList buttons;
	protected MenuButton selectedButton;

	protected int lastX;
	protected int lastY;

	@Override
	public void dispose() {
		Controls.removeMouseListener(this);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void init() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		Controls.addMouseListener(this);

		current_camera = new Camera(45.0f, (float) Game.getInstance().getWidth()
				/ (float) Game.getInstance().getHeight(), 0f, 100f);

		ShaderProgram screenProgram = new ShaderProgram("texture_camera", "texture", "screen");
		ShapeQuadTexture mouseShape = new ShapeQuadTexture(screenProgram, "menu/cursor.png");
		mouseCursor = new EntityActor(mouseShape);
		mouseCursor.scale.set(0.075f);

		ShaderProgram shaderProgramTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture",
				"texture_camera_instanced");

		BitMapFont bmf = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexCameraInstanced, "scumm_font.png",
				128, 256, 8, 11));

		gameTitle = bmf.createString(new Vector3(0f, 0.75f, 0f), "Wolfen-Doo", 2f, TextPosition.CENTER, true);
		seedLabel = bmf.createString(new Vector3(-0.25f, 0.2f, 0f), "Seed:", 1f, TextPosition.CENTER, true);

		// BUTTONS --

		buttons = new DisplayableList();

		ShapeQuadTexture buttonShape = new ShapeQuadTexture(screenProgram, "menu/button.png");

		final MenuTextField seedField = new MenuTextField(buttonShape, "", new Vector3(0.25f, 0.2f, 0f), 1f, bmf);
		seedField.scale.setX(0.5f);
		seedField.setOnButtonRelease(new ButtonRelease() {

			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					seedField.setEditing(true);
				}
			}
		});
		seedField.setControlsCharListener(new ControlsCharListener() {

			@Override
			public void onKeyPress(char key) {
				// NULL
				if (key == 0) {
					return;
				}

				// ENTER
				if (key == 13) {
					seedField.setEditing(false);
					return;
				}

				String text = seedField.getText().getText();

				// DELETE
				if (key == 8) {
					if (text.length() > 0) {
						seedField.setText(text.substring(0, text.length() - 1));
					}
					return;
				}

				if (key >= '0' && key <= '9') {
					seedField.setText(text + key);
				}

				if (seedField.getText().getText().length() == 0 && key == '-') {
					seedField.setText("" + key);
				}
			}
		});
		buttons.add(seedField);

		final MenuButton playButton = new MenuButton(buttonShape, "Generate map", new Vector3(0f, -0.1f, 0f), 1.f, bmf);
		playButton.setOnButtonRelease(new ButtonRelease() {

			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					String s_seed = seedField.getText().getText().replace(MenuTextField.CURSOR_CHAR, "");
					long seed = 0;

					if (s_seed.length() > 0) {
						seed = Long.parseLong(s_seed);
					}

					GameStateManager.changeGameState(new WolfenGameState(seed));
				}
			}
		});
		buttons.add(playButton);

		final MenuButton quitButton = new MenuButton(buttonShape, "Quit", new Vector3(0f, -0.6f, 0f), 1f, bmf);
		quitButton.setOnButtonRelease(new ButtonRelease() {

			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					Game.end();
				}
			}
		});
		buttons.add(quitButton);
	}

	@Override
	public void onMouseMoved(int x, int y) {

		float newX = x - lastX;
		float newY = y - lastY;

		newX *= 2f / Game.getInstance().getWidth();
		newY *= 2f / Game.getInstance().getHeight();

		mouseCursor.position.add(newX, newY, 0f);

		lastX = x;
		lastY = y;
	}

	@Override
	public void onMousePress(int button) {
		if (button == 0) {

			AABBRectangle cursorRect = getMouseRectangle();

			for (Displayable d : buttons) {
				MenuButton buttonActor = (MenuButton) d;
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
		gameTitle.render();
		seedLabel.render();

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

		gameTitle.update(dt);
		seedLabel.update(dt);

		buttons.update(dt);
	}

	protected AABBRectangle getMouseRectangle() {
		Vector3 cursorScale = new Vector3(0f);
		Vector3 cursorPos = new Vector3(mouseCursor.position);
		cursorPos.addX(mouseCursor.scale.getScale(0.5f).getNegate().getX());
		cursorPos.addY(mouseCursor.scale.getScale(0.5f).getY());

		return new AABBRectangle(cursorPos, cursorScale);
	}

}
