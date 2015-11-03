package engine.animations;

import java.io.File;
import java.util.HashMap;

/**
 * Manager used to hold all the existing animations from the "animations" folder
 * @author Gaktan
 */
public class AnimationManager {

	/**
	 * Holds all the animations for a specific file
	 * @author Gaktan
	 */
	public class AnimationHolder {
		private HashMap<String, Animation> animations;

		public AnimationHolder(HashMap<String, Animation> animations) {
			this.animations = animations;
		}

		public Animation get(String s) {
			return animations.get(s);
		}
	}

	private HashMap<String, AnimationHolder> holders;
	private static final AnimationManager instance;
	
	static {
		instance = new AnimationManager();
	}

	private AnimationManager() {
		holders = new HashMap<String, AnimationHolder>();

		readAnimations();
	}

	public static AnimationManager getInstance() {
		return instance;
	}

	/**
	 * Reads all the animations from the "res/animations/" folder
	 */
	private void readAnimations() {
		File folder = new File("res/animations/");

		for (File f : folder.listFiles()) {

			if (!f.getName().endsWith(".animation"))
				continue;

			String path = f.getName().replaceAll(".animation", "");

			HashMap<String, Animation> animations = new AnimationReader().readFromFile(path);

			holders.put(path, new AnimationHolder(animations));
		}
	}

	/**
	 * Gets any animation from any file
	 * @param file Animation file (without the extension)
	 * @param animation Animation name
	 * @return null if nothing was found
	 */
	public Animation getAnimation(String file, String animation) {
		AnimationHolder holder = holders.get(file);
		if (holder != null)
			return holder.get(animation);

		return null;
	}
}
