package engine.animations;

import java.io.File;
import java.util.HashMap;

public class AnimationManager {

	private HashMap<String, AnimationHolder> holders;
	private static final AnimationManager instance = new AnimationManager();
	
	private AnimationManager() {
		holders = new HashMap<String, AnimationHolder>();
		
		readAnimations();
	}
	
	public static AnimationManager getInstance()
	{
		return instance;
	}
	
	private void readAnimations()
	{
		File folder = new File("res/animations/");
		
		for(File f : folder.listFiles())
		{
			if(!f.getName().endsWith(".animation"))
				continue;
			
			String path = f.getName().replaceAll(".animation", "");
			
			HashMap<String, Animation> animations = new AnimationReader().readFromFile(path);
			
			holders.put(path, new AnimationHolder(animations));
		}
	}
	
	public Animation getAnimation(String file, String animation)
	{
		AnimationHolder holder = holders.get(file);
		if(holder != null)
			return holder.get(animation);
		
		return null;
	}
	
	public class AnimationHolder
	{
		private HashMap<String, Animation> animations;
		
		public AnimationHolder(HashMap<String, Animation> animations) {
			this.animations = animations;
		}
		
		public Animation get(String s)
		{
			return animations.get(s);
		}
	}
}
