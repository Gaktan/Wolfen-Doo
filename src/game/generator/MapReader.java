package game.generator;

import engine.shapes.Orientation;
import engine.util.FileUtil;
import engine.util.MathUtil;
import engine.util.Vector3;

/**
 * NewMapReader class is used to read a map from a .map file
 *
 * For an example, see res/maps/map.example
 *
 * @author Gaktan
 */
public class MapReader {

	// Commands
	protected static final String COMMAND_NAME = "name";
	protected static final String COMMAND_WIDTH = "width";
	protected static final String COMMAND_HEIGHT = "height";
	protected static final String COMMAND_BILLBOARD = "billboard";
	protected static final String COMMAND_WALL = "wall";
	protected static final String COMMAND_ANIMATION = "animation";
	protected static final String COMMAND_DOOR = "door";
	protected static final String COMMAND_MAP = "map";
	protected static final String COMMAND_SKY = "sky";
	protected static final String COMMAND_START = "start";

	protected String mapData;
	protected String name;

	protected int width;
	protected int height;

	protected Vector3 startingPoint;

	protected Map map;
	protected Vector3 upColor;
	protected Vector3 downColor;

	public MapReader() {
		startingPoint = new Vector3();
		upColor = new Vector3();
		downColor = new Vector3();
	}

	/**
	 * Starts the process of reading the map
	 *
	 * @return Map created from designated file
	 */
	public Map createMap(String path) {
		map = new Map();

		startingPoint = new Vector3();

		readFile(path);

		map.setSize(width, height);
		map.buildMapFromString(mapData);
		map.setStartingPoint(startingPoint);
		map.setSky(upColor, downColor);

		for (int i = 0; i < height; i++) {
			System.out.println(mapData.substring(i * height, (i + 1) * height));
		}

		return map;
	}

	protected void createShape(String command, String value) {
		String[] values = value.split(", ");

		char ch = values[0].charAt(0);
		String s_image = values[1];

		if (command.equals(COMMAND_ANIMATION)) {
			// TODO
			return;
		}

		else if (command.equals(COMMAND_BILLBOARD)) {
			if (values.length != 3) {
				System.err.println("Error with command \"" + command + "\". Usage : " + command
						+ "{char, texture, solidity (0 for not solid, 1 for solid)}");
				System.err.println("Values given : {" + value + "}.");
				return;
			}
			String s_solid = values[2];
			boolean solid = (MathUtil.parseInt(s_solid) == 1);
			map.newBillboard(ch, s_image, solid);
		}

		else if (command.equals(COMMAND_WALL)) {
			if (values.length != 3) {
				System.err.println("Error with command \"" + command + "\". Usage : " + command
						+ "{char, texture, solidity (0 for not solid, 1 for solid)}");
				System.err.println("Values given : {" + value + "}.");
				return;
			}
			String s_solid = values[2];
			boolean solid = (MathUtil.parseInt(s_solid) == 1);
			map.newWall(ch, s_image, solid);
		}

		else if (command.equals(COMMAND_DOOR)) {

			if (values.length != 6) {
				System.err.println("Error with command \"" + command + "\". Usage : " + command
						+ "{char, texture, sideTexture, opening direction (x y z), "
						+ " orientation (0 for vertical, 1 for horizontal), opening time}");
				System.err.println("Values given : {" + value + "}.");
				return;
			}

			String side_image = values[2];
			Vector3 openingPosition = readVector3(values[3]);
			int orientation = ((MathUtil.parseInt(values[4]) == 1) ? Orientation.NORTH : Orientation.EAST);
			float openingTime = MathUtil.parseFloat(values[5]);

			map.newDoor(ch, s_image, side_image, openingPosition, orientation, openingTime);
		}
	}

	protected void performCommand(String command, String value) {

		if (command.equals(COMMAND_WIDTH))
			width = MathUtil.parseInt(value);

		else if (command.equals(COMMAND_HEIGHT))
			height = MathUtil.parseInt(value);

		else if (command.equals(COMMAND_NAME))
			name = value;

		else if (command.equals(COMMAND_BILLBOARD) || command.equals(COMMAND_WALL) || command.equals(COMMAND_ANIMATION)
				|| command.equals(COMMAND_DOOR))
			createShape(command, value);

		else if (command.equals(COMMAND_MAP))
			mapData = value.replace("\n", "");

		else if (command.equals(COMMAND_SKY))
			setSky(value);

		else if (command.equals(COMMAND_START))
			setStartingPosition(value);

	}

	protected void readFile(String path) {
		String data = FileUtil.readFromFile("res/maps/" + path);

		int start = 0;
		int end = 0;

		String command = null;

		for (char ch : data.toCharArray()) {
			if (ch == '{') {
				command = data.substring(start, end).trim().toLowerCase();

				start = end + 1;
			}
			else if (ch == '}') {
				String value;

				if (!command.equals(COMMAND_MAP))
					value = data.substring(start, end).trim();
				else
					value = data.substring(start, end);

				start = end + 1;

				performCommand(command, value);
				command = null;
			}
			end++;
		}
	}

	protected Vector3 readVector3(String value) {
		String[] v = value.split(" ");

		if (v.length != 3) {
			System.err.println("Error with value \"" + value + "\". Usage : \"x y z\".");
			return new Vector3();
		}

		float x = MathUtil.parseFloat(v[0]);
		float y = MathUtil.parseFloat(v[1]);
		float z = MathUtil.parseFloat(v[2]);

		return new Vector3(x, y, z);
	}

	protected void setStartingPosition(String value) {
		String[] v = value.split(",");

		if (v.length != 2) {
			System.err.println("Error with command \"" + COMMAND_START + "\". Usage : " + COMMAND_START + "{x, y}");
			return;
		}

		float x = MathUtil.parseFloat(v[0]);
		float y = MathUtil.parseFloat(v[1]);

		startingPoint.set(x, 0f, y);
	}

	protected void setSky(String value) {
		String[] values = value.split(",");

		if (values.length != 2) {
			System.err.println("Error with command \"" + COMMAND_START + "\". Usage : " + COMMAND_START
					+ "{r1 g1 b1, r2 g2 b2}");
			return;
		}

		downColor = readVector3(values[0].trim());
		upColor = readVector3(values[1].trim());

		// 0,00390625 = 1 / 255
		float colorScale = 0.00390625f;
		downColor.scale(colorScale);
		upColor.scale(colorScale);
	}
}