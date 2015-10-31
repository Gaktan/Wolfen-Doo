package engine.animations;

import java.util.HashMap;

import engine.util.FileUtil;
import engine.util.MathUtil;

public class AnimationReader {
	
	private static final String COMMAND_IMAGE_SIZE = "imagesize";
	private static final String COMMAND_FRAME_SIZE = "framesize";

	private static final String COMMAND_ANIMATION = "animation";
	private static final String COMMAND_ANIMATION_NAME = "name";
	private static final String COMMAND_ANIMATION_FRAMES = "frames";
	private static final String COMMAND_ANIMATION_DELAYS = "delays";
	
	HashMap<String, Animation> animations;
	
	private Animation currentAnimation;
	private String currentAnimationName;
	
	private boolean framesFirst;
	
	private int imageSize;
	private int frameSize;
	
	public AnimationReader() {
		framesFirst = false;
		
		animations = new HashMap<String, Animation>();
	}
	
	public HashMap<String, Animation> readFromFile(String path)
	{
		String data = FileUtil.readFromFile("res/animations/" + path + ".animation");
		
		int start = 0;
		int end = 0;

		String command = null;

		for(char ch : data.toCharArray()){
			if(ch == '{'){
				command = data.substring(start, end).trim().toLowerCase();

				start = end+1;
			}

			else if(ch == '}'){
				String value = data.substring(start, end).trim().toLowerCase();

				start = end+1;

				performCommand(command, value);
				command = null;
			}

			end++;
		}
		
		return animations;
	}

	private void performCommand(String command, String value)
	{
		if (command.equals(COMMAND_IMAGE_SIZE))
		{
			imageSize = MathUtil.parseInt(value);
		}
		else if (command.equals(COMMAND_FRAME_SIZE))
		{
			frameSize = MathUtil.parseInt(value);
		}
		else if (command.equals(COMMAND_ANIMATION))
		{			
			int start = 0;
			int end = 0;
			
			String a_command = null;
			
			currentAnimation = new Animation(imageSize, frameSize);
			
			for(char ch : value.toCharArray()){
				if(ch == '['){
					a_command = value.substring(start, end).trim().toLowerCase();

					start = end+1;
				}

				else if(ch == ']'){
					String a_value = value.substring(start, end).trim().toLowerCase();

					start = end+1;

					performAnimationCommand(a_command, a_value);
					a_command = null;
				}

				end++;
			}
			
			animations.put(currentAnimationName, currentAnimation);
		}
	}
	
	public void performAnimationCommand(String command, String value)
	{
		if(command.equals(COMMAND_ANIMATION_NAME))
		{
			currentAnimationName = value;
		}
		else if(command.equals(COMMAND_ANIMATION_FRAMES))
		{
			framesFirst = true;
			
			String[] split = value.split(",");
			
			int frames[] = new int[split.length];
			
			for(int i = 0; i < split.length; i++)
			{
				frames[i] = MathUtil.parseInt(split[i].trim());
			}
			
			currentAnimation.setFrames(frames);
		}
		else if(command.equals(COMMAND_ANIMATION_DELAYS))
		{
			if(!framesFirst)
			{
				System.err.println("You must declare frames first, then delays");
				return;
			}
			
			framesFirst = false;
			
			String[] split = value.split(",");
			
			if(split.length == 1)
			{
				currentAnimation.setDelay(MathUtil.parseFloat(split[0].trim()));
				return;
			}
			
			float delays[] = new float[split.length];
			
			for(int i = 0; i < split.length; i++)
			{
				delays[i] = MathUtil.parseFloat(split[i].trim());
			}
			
			currentAnimation.setDelays(delays);
		}
	}
}
