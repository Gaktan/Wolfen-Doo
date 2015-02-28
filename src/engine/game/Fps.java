package engine.game;

import java.util.LinkedList;

public class Fps {

	private static long ONE_SECOND = 1000000L * 1000L;

	LinkedList<Long> frames;

	public long calcFPS(){
		long time = System.nanoTime();
		frames.add(time);
		while(true){
			long f = frames.getFirst();
			if(time - f > ONE_SECOND){
				frames.remove();
			} 
			else 
				break;
		}
		return frames.size();
	}

	public Fps() {
		frames = new LinkedList<>();
	}
}
