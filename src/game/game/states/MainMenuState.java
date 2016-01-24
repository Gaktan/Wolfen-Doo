package game.game.states;

import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.game.Controls.ControlsCharListener;
import engine.game.states.GameStateManager;
import engine.util.Vector3;
import game.game.GameWolfen;
import game.menu.MenuButton;
import game.menu.MenuButton.ButtonRelease;
import game.menu.MenuTextField;

public class MainMenuState extends MenuState {

	protected DisplayableText gameTitle;
	protected DisplayableText seedLabel;

	@Override
	public void dispose() {
		gameTitle.dispose();
		seedLabel.dispose();
		super.dispose();
	}

	@Override
	public void init() {
		super.init();

		gameTitle = font.createString(new Vector3(0f, 0.75f, 0f), "Wolfen-Doo", 2f, TextPosition.CENTER, true);
		seedLabel = font.createString(new Vector3(-0.25f, 0.35f, 0f), "Seed:", 1f, TextPosition.CENTER, true);

		final MenuTextField seedField = new MenuTextField(buttonShape, "", new Vector3(0.25f, 0.35f, 0f), 1f, font);
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

				else if (seedField.getText().getText().length() == 0 && key == '-') {
					seedField.setText("" + key);
				}
			}
		});
		buttons.add(seedField);

		final MenuButton playButton = new MenuButton(buttonShape, "Generate map", new Vector3(0f, 0.05f, 0f), 1.f, font);
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

		final MenuButton openButton = new MenuButton(buttonShape, "Open map...", new Vector3(0f, -0.3f, 0f), 1f, font);
		openButton.setOnButtonRelease(new ButtonRelease() {

			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					GameStateManager.changeGameState(new MapLoadingState());
				}
			}
		});
		buttons.add(openButton);

		final MenuButton quitButton = new MenuButton(buttonShape, "Quit", new Vector3(0f, -0.75f, 0f), 1f, font);
		quitButton.setOnButtonRelease(new ButtonRelease() {

			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					GameWolfen.end();
				}
			}
		});
		buttons.add(quitButton);
	}

	@Override
	public void render() {
		gameTitle.render();
		seedLabel.render();

		super.render();
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		gameTitle.update(dt);
		seedLabel.update(dt);
	}
}
