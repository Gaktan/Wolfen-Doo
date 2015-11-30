package engine.animations;

import java.util.Arrays;

/**
 * Single animation
 * @author Gaktan
 */
public class Animation {
	private int[] frames;

	private float[] delays;

	private int currentIndex;
	private float currentDelay;

	private boolean pause;

	public Animation() {
		currentDelay = 0;
		currentIndex = 0;
	}

	public Animation(Animation animation) {
		frames = animation.frames;
		delays = animation.delays;

		currentDelay = 0;
		currentIndex = 0;
	}

	public void setDelay(float delay){
		if (delays == null) {
			delays = new float[frames.length];
		}
		Arrays.fill(this.delays, delay);
	}

	public void setDelays(float[] delays){
		this.delays = delays;
	}

	public void updateFrame(float dt){
		if (pause)
			return;

		currentDelay += dt;

		if (currentDelay >= delays[currentIndex]) {
			nextFrame();
		}
	}

	/**
	 * Advance to the next frame
	 */
	public void nextFrame() {
		currentIndex++;
		currentDelay = 0;

		if (currentIndex >= frames.length) {
			currentIndex = 0;
		}
	}

	public int getCurrentFrame() {
		return frames[currentIndex];
	}

	public void pause() {
		pause = true;
	}

	public void resume() {
		pause = false;
	}

	public void stop() {
		pause();
		currentDelay = 0;
		currentIndex = 0;
	}

	public void setFrames(int[] frames) {
		this.frames = frames;
	}
}
