package engine.animations;

import static engine.util.JSONUtil.getArray;
import static engine.util.JSONUtil.getString;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Class used to read an animation file
 *
 * @author Gaktan
 */
public class AnimationReader {

	protected static final String KEY_ANIMATIONS = "animations";
	protected static final String KEY_ANIMATION_NAME = "name";
	protected static final String KEY_ANIMATION_FRAMES = "frames";
	protected static final String KEY_ANIMATION_DELAYS = "delays";

	/**
	 * Reads a specific file
	 *
	 * @param path
	 *            Filename (no extension)
	 * @return HashMap containing all the animations of given file
	 */
	public static HashMap<String, Animation> readFromFile(String path) {
		HashMap<String, Animation> animations = new HashMap<String, Animation>();

		try {
			Object obj = JSONValue.parseWithException(new FileReader("res/animations/" + path));
			JSONObject root = (JSONObject) obj;

			JSONArray array = getArray(root, KEY_ANIMATIONS, false);

			for (Object o : array) {
				if (!(o instanceof JSONObject)) {
					System.err.println("Error in animations. Element is not a JSONObject");
					continue;
				}

				JSONObject anim = (JSONObject) o;

				String name = getString(anim, KEY_ANIMATION_NAME, true);

				Object o_frames = getArray(anim, KEY_ANIMATION_FRAMES, true);
				if (!(o_frames instanceof JSONArray)) {
					System.err.println("Error in animations. \"frames\" is not an JSONArray");
					continue;
				}
				JSONArray j_frames = (JSONArray) o_frames;
				int[] frames = new int[j_frames.size()];
				for (int i = 0; i < j_frames.size(); i++) {
					Object element = j_frames.get(i);
					if (!(element instanceof Number)) {
						System.err.println("Error in animations. \"frames\" element " + i + " is not a Number");
						continue;
					}
					Number number = (Number) element;
					int frame = number.intValue();
					frames[i] = frame;
				}

				Object o_delays = getArray(anim, KEY_ANIMATION_DELAYS, true);
				JSONArray j_delays = (JSONArray) o_delays;
				float[] delays = new float[j_delays.size()];
				for (int i = 0; i < j_delays.size(); i++) {
					Object element = j_delays.get(i);
					if (!(element instanceof Number)) {
						System.err.println("Error in animations. \"delays\" element " + i + " is not a Number");
						continue;
					}
					Number number = (Number) element;
					float delay = number.floatValue();
					delays[i] = delay;
				}

				if (delays.length != 1 && (delays.length != frames.length)) {
					System.err.println("Error in animation \"" + name
							+ "\". The amount of delays doesn't match the amount of frames");
					continue;
				}

				Animation a = new Animation();
				a.setFrames(frames);
				if (delays.length == 1) {
					a.setDelay(delays[0], frames.length);
				}
				else {
					a.setDelays(delays);
				}

				animations.put(name, a);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return animations;
	}

}
