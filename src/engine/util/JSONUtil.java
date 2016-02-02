package engine.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONUtil {

	public static JSONArray getArray(JSONObject json, String key, boolean displayError) {
		try {
			JSONArray array = (JSONArray) json.get(key);
			if (array == null) {
				System.err.println("Cannot find \"" + key + "\".");
				return new JSONArray();
			}
			return array;
		} catch (ClassCastException e) {
			System.err.println("Error with key \"" + key + "\" :");
			System.err.println(e.getMessage());
		}

		return new JSONArray();
	}

	public static JSONObject getObject(JSONObject json, String key, boolean displayError) {
		try {
			JSONObject obj = (JSONObject) json.get(key);
			if (obj == null) {
				if (displayError) {
					System.err.println("Cannot find \"" + key + "\".");
				}
				return new JSONObject();
			}
			return obj;
		} catch (ClassCastException e) {
			System.err.println("Error with key \"" + key + "\" :");
			System.err.println(e.getMessage());
		}

		return new JSONObject();
	}

	public static String getString(JSONObject json, String key, boolean displayError) {
		try {
			String s = (String) json.get(key);
			if (s == null) {
				if (displayError) {
					System.err.println("Cannot find \"" + key + "\".");
				}
				return "";
			}
			return s;
		} catch (ClassCastException e) {
			System.err.println("Error with key \"" + key + "\" :");
			System.err.println(e.getMessage());
		}

		return "";
	}

	public static char getChar(JSONObject json, String key, boolean displayError) {
		String s = getString(json, key, displayError);

		if (!s.isEmpty()) {
			return s.charAt(0);
		}

		return 0;
	}

	public static int getInt(JSONObject json, String key, boolean displayError) {
		return getNumber(json, key, displayError).intValue();
	}

	public static float getFloat(JSONObject json, String key, boolean displayError) {
		return getNumber(json, key, displayError).floatValue();
	}

	public static Number getNumber(JSONObject json, String key, boolean displayError) {
		try {
			Number i = (Number) json.get(key);
			if (i == null) {
				if (displayError) {
					System.err.println("Cannot find \"" + key + "\"." + json.toString());
				}
				return 0;
			}
			return i;
		} catch (ClassCastException e) {
			System.err.println("Error with key \"" + key + "\" :");
			System.err.println(e.getMessage());
		}

		return 0;
	}

	public static boolean getBoolean(JSONObject json, String key, boolean displayError) {
		try {
			Boolean b = (Boolean) json.get(key);
			if (b == null) {
				if (displayError) {
					System.err.println("Cannot find \"" + key + "\".");
				}
				return false;
			}
			return b.booleanValue();
		} catch (ClassCastException e) {
			System.err.println("Error with key \"" + key + "\" :");
			System.err.println(e.getMessage());
		}

		return false;
	}

}
