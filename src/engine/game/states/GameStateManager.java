package engine.game.states;

public class GameStateManager {

	private static GameState currentGameState;
	private static GameState nextGameState;

	public static GameState getCurrentGameState() {
		return currentGameState;
	}

	public static void changeGameState(GameState newGameState) {
		nextGameState = newGameState;
	}

	public static void updateState() {
		if (nextGameState != null) {

			if (currentGameState != null) {
				currentGameState.dispose();
			}
			currentGameState = nextGameState;
			nextGameState = null;
			currentGameState.init();
		}
	}
}
