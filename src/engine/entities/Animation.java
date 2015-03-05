package engine.entities;

import java.util.Arrays;

import org.lwjgl.util.vector.Vector3f;

public class Animation {

	private float imageFactor;
	private int imagesPerLine;

	private int startFrame;
	private int endFrame;

	private float[] delay;

	private int currentFrame;
	private float currentDelay;

	private boolean pause;

	public Animation(int start, int end, int image_size, int frame_size) {
		int totalFrames = end - start + 1;

		startFrame = start;
		endFrame = end;

		imagesPerLine = image_size / frame_size;
		imageFactor = (float) frame_size / image_size;

		delay = new float[totalFrames];
		setDelay();

		currentDelay = 0;
		currentFrame = 0;
	}

	public void setDelay(int index, float delay){
		if(index < 0 || index > this.delay.length + 1)
			return;

		this.delay[index] = delay;
	}

	public void setDelay(float delay){
		Arrays.fill(this.delay, delay);
	}

	public void setDelay(){
		setDelay(1000.0f);
	}

	public void updateFrame(float dt){
		if(pause)
			return;

		currentDelay += dt;
		
		if(currentDelay >= delay[currentFrame]){
			nextFrame();
		}
	}

	public void nextFrame(){
		currentFrame++;
		currentDelay = 0;

		if(currentFrame > endFrame - startFrame){
			currentFrame = 0;
		}
	}

	public Vector3f getCurrentUV(){
		int actualFrame = startFrame + currentFrame;

		float y = actualFrame / imagesPerLine;
		float x = actualFrame % imagesPerLine;

		Vector3f vec = new Vector3f(x, y, imageFactor);
		return vec;
	}

	public void pause(){
		pause = true;
	}

	public void resume(){
		pause = false;
	}

	public void stop(){
		pause();
		currentDelay = 0;
		currentFrame = 0;
	}
}
