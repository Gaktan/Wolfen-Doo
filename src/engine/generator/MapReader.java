package engine.generator;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.EntityActor;
import engine.game.ShaderProgram;
import engine.shapes.Orientation;
import engine.shapes.ShapeInsideOutCubeColor;
import engine.util.FileUtil;
import engine.util.MathUtil;

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

	// Shader Programs
	protected static final String PROGRAM_TEXTURE = "texture";
	protected static final String PROGRAM_BILLBOARD_ANIMATED = "billboard_animated";
	protected static final String PROGRAM_BILLBOARD_TEXTURE = "billboard_texture";

	// Shapes
	protected static final String SHAPE_CUBE_TEXTURE = "cube_texture";
	protected static final String SHAPE_QUAD_TEXTURE = "quad_texture";

	protected String path;
	protected String mapData;

	protected String name;
	protected int width;
	protected int height;
	protected Map map;


	public MapReader(String path) {
		this.path = path;
	}

	/**
	 * Starts the process of reading the map
	 * @return Map created from designated file
	 */
	public Map createMap() {

		map = new Map();
		readFile(path);
		map.setSize(width, height);
		map.buildMapFromString(mapData);

		for (int i = 0; i < height; i++) {
			System.out.println(mapData.substring(i * height, (i+1) * height));
		}

		return map;
	}

	protected void readFile(String path) {
		String data = FileUtil.readFromFile("res/maps/" + path);

		int start = 0;
		int end = 0;

		String command = null;

		for (char ch : data.toCharArray()) 
		{
			if (ch == '{') {
				command = data.substring(start, end).trim().toLowerCase();

				start = end+1;
			}

			else if (ch == '}') {
				String value = data.substring(start, end).trim().toLowerCase();

				start = end+1;

				performCommand(command, value);
				command = null;
			}

			end++;
		}
	}

	protected void performCommand(String command, String value) {

		if (command.equals(COMMAND_WIDTH))
			width = MathUtil.parseInt(value);

		else if (command.equals(COMMAND_HEIGHT))
			height = MathUtil.parseInt(value);

		else if (command.equals(COMMAND_NAME))
			name = value;

		else if (command.equals(COMMAND_BILLBOARD) || command.equals(COMMAND_WALL)
				|| command.equals(COMMAND_ANIMATION) || command.equals(COMMAND_DOOR))
			createShape(command, value);

		else if (command.equals(COMMAND_MAP))
			mapData = value.replace("\n", "");

		else if (command.equals(COMMAND_SKY))
			setSky(value);

	}

	protected void createShape(String command, String value) {
		String[] values = value.split(", ");

		char ch = values[0].charAt(0);
		String s_image = values[1];

		if (command.equals(COMMAND_ANIMATION)) {
			return;
		}

		else if (command.equals(COMMAND_BILLBOARD)) {
			String s_solid = values[2];

			boolean solid = (MathUtil.parseInt(s_solid) == 1);

			map.newBillboard(ch, s_image, solid);
		}

		else if (command.equals(COMMAND_WALL)) {
			String s_solid = values[2];

			boolean solid = (MathUtil.parseInt(s_solid) == 1);

			map.newWall(ch, s_image, solid);
		}

		else if (command.equals(COMMAND_DOOR)) {
			
			Vector3f openingPosition = readVector3f(values[2]);
			int orientation = ((MathUtil.parseInt(values[3]) == 1) ? Orientation.NORTH : Orientation.WEST);
			float openingTime = MathUtil.parseFloat(values[4]);
			
			map.newDoor(ch, s_image, openingPosition, orientation, openingTime);
		}
	}

	protected void setSky(String value) {
		String[] values = value.split(", ");
		Vector3f downColor = readVector3f(values[0]);
		Vector3f upColor = readVector3f(values[1]);

		downColor.scale(1.0f / 256);
		upColor.scale(1.0f / 256);

		ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(ShaderProgram.getProgram("color"), upColor, downColor);

		map.setSky(new EntityActor(skyShape));
	}

	protected Vector3f readVector3f(String value) {
		String[] v = value.split(" ");

		float x = MathUtil.parseFloat(v[0]);
		float y = MathUtil.parseFloat(v[1]);
		float z = MathUtil.parseFloat(v[2]);

		return new Vector3f(x, y, z);
	}
}
