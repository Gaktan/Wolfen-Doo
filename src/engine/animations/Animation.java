package engine.animations;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

/**
 * Single animation
 * @author Gaktan
 */
public class Animation {

	private float imageFactor;
	private int imagesPerLine;

	private int[] frames;

	private float[] delays;

	private int currentIndex;
	private float currentDelay;

	private boolean pause;
	
	public Animation() {}

	public Animation(int image_size, int frame_size) {
		imagesPerLine = image_size / frame_size;
		imageFactor = (float) frame_size / image_size;

		currentDelay = 0;
		currentIndex = 0;
	}

	public Animation(Animation animation) {
		imagesPerLine = animation.imagesPerLine;
		imageFactor = animation.imageFactor;
		
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

	public Vector3f getCurrentUV() {
		int actualFrame = frames[currentIndex];

		float y = actualFrame / imagesPerLine;
		float x = actualFrame % imagesPerLine;

		Vector3f vec = new Vector3f(x, y, imageFactor);
		return vec;
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
