package game.game.states;

import java.io.File;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import engine.game.states.GameStateManager;
import engine.shapes.ShapeQuadTexture;
import engine.util.Vector3;
import game.menu.MenuButton.ButtonRelease;
import game.menu.MenuTextField;

public class MapLoadingState extends MenuState {

	@Override
	public void init() {
		super.init();

		buttonShape = new ShapeQuadTexture(programScreen, "menu/button.png");

		File mapsFolder = new File("./res/maps");

		final MenuTextField backButton = new MenuTextField(buttonShape, "", new Vector3(-.8f, .85f, 0f), 1f, font);
		backButton.scale.setX(0.35f);
		backButton.setText("Back");

		backButton.setOnButtonRelease(new ButtonRelease() {
			@Override
			public void onButtonRelease(boolean mouseInside) {
				if (mouseInside) {
					GameStateManager.changeGameState(new MainMenuState());
				}
			}
		});
		buttons.add(backButton);

		String[] files = mapsFolder.list();

		Arrays.sort(files);

		int index = -1;
		for (String s : files) {
			if (!s.endsWith(".map")) {
				continue;
			}

			index++;

			Vector3 position = new Vector3(-0.5f, 0.5f, 0f);

			if (index % 2 == 1) {
				position.setX(0.5f);
			}

			int posY = index / 2;

			position.addY(-posY * 0.25f);

			final MenuTextField mapButton = new MenuTextField(buttonShape, s, position, 0.75f, font);

			mapButton.setOnButtonRelease(new ButtonRelease() {

				@Override
				public void onButtonRelease(boolean mouseInside) {
					if (mouseInside) {
						GameStateManager.changeGameState(new WolfenGameState(mapButton.getText().getText()));
					}
				}
			});
			buttons.add(mapButton);
		} // for String s
	}

	@Override
	public void onKeyRelease(int key) {
		if (key == Keyboard.KEY_ESCAPE) {
			GameStateManager.changeGameState(new MainMenuState());
		}
	}
}
