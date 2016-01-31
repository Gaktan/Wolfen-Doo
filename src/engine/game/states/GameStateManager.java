package engine.game.states;

import engine.util.TextureUtil;

public class GameStateManager {

	private static GameState currentGameState;
	private static GameState nextGameState;

	public static void changeGameState(GameState newGameState) {
		nextGameState = newGameState;
	}

	public static GameState getCurrentGameState() {
		return currentGameState;
	}

	public static void updateState() {
		if (nextGameState != null) {
			if (currentGameState != null) {
				currentGameState.dispose();
				TextureUtil.deleteTextures();
			}
			currentGameState = nextGameState;
			nextGameState = null;
			currentGameState.init();
		}
	}
}
