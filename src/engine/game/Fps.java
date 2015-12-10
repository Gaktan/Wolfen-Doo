package engine.game;

import java.util.LinkedList;

/**
 * Class used to compute FPS
 * 
 * @author Gaktan
 */
public class Fps {

	private static long ONE_SECOND = 1000000L * 1000L;

	private LinkedList<Long> frames;

	public Fps() {
		frames = new LinkedList<>();
	}

	public long update() {
		long time = System.nanoTime();
		frames.add(time);

		while (true) {
			long f = frames.getFirst();
			if (time - f > ONE_SECOND) {
				frames.remove();
			}
			else
				break;
		}
		return frames.size();
	}
}
