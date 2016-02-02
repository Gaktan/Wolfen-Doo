package game.generator;

import static engine.util.JSONUtil.getArray;
import static engine.util.JSONUtil.getBoolean;
import static engine.util.JSONUtil.getChar;
import static engine.util.JSONUtil.getFloat;
import static engine.util.JSONUtil.getInt;
import static engine.util.JSONUtil.getObject;
import static engine.util.JSONUtil.getString;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import engine.util.Vector3;

/**
 * MapReader class is used to read a map from a .map file
 *
 * For an example, see res/maps/map.example
 *
 * @author Gaktan
 */
public class MapReader {

	// Commands
	protected static final String KEY_NAME = "name";
	protected static final String KEY_WIDTH = "width";
	protected static final String KEY_HEIGHT = "height";
	protected static final String KEY_BILLBOARDS = "billboards";
	protected static final String KEY_WALLS = "walls";
	protected static final String KEY_DOORS = "doors";
	protected static final String KEY_MAP = "map";
	protected static final String KEY_SKY = "sky";
	protected static final String KEY_START = "start";
	protected static final String KEY_X = "x";
	protected static final String KEY_Y = "y";
	protected static final String KEY_Z = "z";
	protected static final String KEY_R = "r";
	protected static final String KEY_G = "g";
	protected static final String KEY_B = "b";

	/**
	 * Starts the process of reading the map
	 *
	 * @return Map created from designated file
	 */
	public static Map createMap(String path) {
		try {
			Object obj = JSONValue.parseWithException(new FileReader("res/maps/" + path));
			JSONObject root = (JSONObject) obj;

			Map map = new Map();

			getString(root, KEY_NAME, false);
			int width = getInt(root, KEY_WIDTH, true);
			int height = getInt(root, KEY_HEIGHT, true);

			map.setSize(width, height);

			// -- Start
			{
				JSONObject start = getObject(root, KEY_START, false);
				float x = getFloat(start, KEY_X, false);
				float y = getFloat(start, KEY_Y, false);

				map.setStartingPoint(new Vector3(x, 0f, y));
			}

			// -- Sky
			{
				JSONObject sky = getObject(root, KEY_SKY, false);
				JSONObject sky_down = getObject(sky, "down", false);
				JSONObject sky_up = getObject(sky, "up", false);

				int r = getInt(sky_down, KEY_R, false);
				int g = getInt(sky_down, KEY_G, false);
				int b = getInt(sky_down, KEY_B, false);

				// 0,00390625 = 1 / 255
				float colorScale = 0.00390625f;

				Vector3 down = new Vector3(r, g, b);
				down.scale(colorScale);

				r = getInt(sky_up, KEY_R, false);
				g = getInt(sky_up, KEY_G, false);
				b = getInt(sky_up, KEY_B, false);

				Vector3 up = new Vector3(r, g, b);
				up.scale(colorScale);

				map.setSky(up, down);
			}

			// -- Walls
			{
				JSONArray walls = getArray(root, KEY_WALLS, true);

				for (Object o : walls) {
					if (!(o instanceof JSONObject)) {
						System.err.println("Error in walls. Element is not a JSONObject");
						continue;
					}
					JSONObject wall = (JSONObject) o;
					boolean solid = getBoolean(wall, "solid", false);
					char c = getChar(wall, "char", true);
					String texture = getString(wall, "texture", true);

					map.newWall(c, texture, solid);
				}
			}

			// -- Billboards
			{
				JSONArray billboards = getArray(root, KEY_BILLBOARDS, true);

				for (Object o : billboards) {
					if (!(o instanceof JSONObject)) {
						System.err.println("Error in walls. Element is not a JSONObject");
						continue;
					}
					JSONObject billboard = (JSONObject) o;
					boolean solid = getBoolean(billboard, "solid", false);
					char c = getChar(billboard, "char", true);
					String texture = getString(billboard, "texture", true);

					map.newBillboard(c, texture, solid);
				}
			}

			// -- Doors
			{
				JSONArray doors = getArray(root, KEY_DOORS, true);

				for (Object o : doors) {
					if (!(o instanceof JSONObject)) {
						System.err.println("Error in walls. Element is not a JSONObject");
						continue;
					}
					JSONObject door = (JSONObject) o;
					char c = getChar(door, "char", true);
					float time = getFloat(door, "time", true);
					String texture = getString(door, "texture", true);
					String texture_side = getString(door, "texture_side", true);

					JSONObject openDirection = getObject(door, "open_direction", true);
					float x = getFloat(openDirection, KEY_X, false);
					float y = getFloat(openDirection, KEY_Y, false);
					float z = getFloat(openDirection, KEY_Z, false);
					Vector3 openingPosition = new Vector3(x, y, z);

					JSONObject scale = getObject(door, "scale", true);
					x = getFloat(scale, KEY_X, false);
					x = (x == 0) ? 1f : x;
					y = getFloat(scale, KEY_Y, false);
					y = (y == 0) ? 1f : y;
					z = getFloat(scale, KEY_Z, false);
					z = (z == 0) ? 1f : z;
					Vector3 scaleVector = new Vector3(x, y, z);

					map.newDoor(c, texture, texture_side, openingPosition, scaleVector, time);
				}
			}

			// -- Map
			{
				String mapData = getString(root, KEY_MAP, true);
				mapData = mapData.replaceAll("(\n|\r\n|\t)", "");
				map.buildMapFromString(mapData);
			}

			return map;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
