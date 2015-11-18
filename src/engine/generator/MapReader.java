package engine.generator;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.EntityActor;
import engine.game.GameWolfen;
import engine.game.Map;
import engine.game.ShaderProgram;
import engine.shapes.Orientation;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInsideOutCubeColor;
import engine.shapes.ShapeQuadTexture;
import engine.util.FileUtil;
import engine.util.MathUtil;

/**
 * MapReader class is used to read a map from a .map file
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

	protected HashMap<Character, MapInfo> infoMap;

	protected GameWolfen game;

	/**
	 * Used by MapInfo to get the type of the entity
	 */
	protected enum MapInfoType {
		WALL,
		BILLBOARD,
		ANIMATED_BILLBOARD,
		DOOR
	}

	/**
	 * Used to store informations about entity creation commands
	 */
	protected class MapInfo {
		protected Shape shape;
		protected boolean solid;

		protected MapInfoType type;
	}

	/**
	 * MapInfo specifically made for doors (as they hold much more information)
	 */
	protected class MapInfoDoor extends MapInfo {
		protected Vector3f openingPosition;
		protected int orientation;
		protected float speed;

		public MapInfoDoor() {
			type = MapInfoType.DOOR;
		}
	}

	public MapReader(GameWolfen game, String path) {
		this.game = game;
		this.path = path;

		infoMap = new HashMap<Character, MapInfo>();
	}

	/**
	 * Starts the process of reading the map
	 * @return Map created from designated file
	 */
	public Map createMap() {
		map = new Map(game);

		readFile(path);

		map.setSize(width, height);
		map.setSky();
		readMap(mapData);

		map.setOrientation();

		System.out.println(map.list);

		return map;
	}

	protected void readFile(String path) {
		String data = FileUtil.readFromFile("res/maps/" + path + ".map");

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

	protected void readMap(String value) {
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				char c = value.charAt((i * width) + j);

				MapInfo mapInfo = infoMap.get(c);

				if (mapInfo != null) {
					Shape shape = mapInfo.shape;

					boolean solid = mapInfo.solid;

					if (mapInfo.type == MapInfoType.ANIMATED_BILLBOARD) {
						map.newAnimatedActor(i, j, shape, solid);
					}

					else if (mapInfo.type == MapInfoType.WALL) {
						map.newWall(i, j, (ShapeCubeTexture) shape, solid);
					}

					else if (mapInfo.type == MapInfoType.BILLBOARD) {
						map.newActor(i, j, shape, solid);
					}

					else if (mapInfo.type == MapInfoType.DOOR) {
						MapInfoDoor mapInfoDoor = (MapInfoDoor) mapInfo;
						map.newDoor(i, j, (ShapeCubeTexture) shape, new Vector3f(mapInfoDoor.openingPosition),
								mapInfoDoor.orientation, mapInfoDoor.speed);
					}
				}
			} // for j
		} // for i
	}

	protected void createShape(String command, String value) {
		String[] values = value.split(", ");

		ShaderProgram program = null;
		Shape shape = null;

		char ch = values[0].charAt(0);
		String s_image = values[1];

		MapInfo mapInfo = new MapInfo();

		if (command.equals(COMMAND_ANIMATION)) {
			program = getProgram(PROGRAM_BILLBOARD_ANIMATED);
			shape = createShape(SHAPE_QUAD_TEXTURE, program, s_image);
			mapInfo.type = MapInfoType.ANIMATED_BILLBOARD;
		}

		else if (command.equals(COMMAND_BILLBOARD)) {
			program = getProgram(PROGRAM_BILLBOARD_TEXTURE);
			shape = createShape(SHAPE_QUAD_TEXTURE, program, s_image);
			mapInfo.type = MapInfoType.BILLBOARD;
		}

		else if (command.equals(COMMAND_WALL)) {
			program = getProgram(PROGRAM_TEXTURE);
			shape = createShape(SHAPE_CUBE_TEXTURE, program, s_image);
			mapInfo.type = MapInfoType.WALL;
		}

		else if (command.equals(COMMAND_DOOR)) {
			program = getProgram(PROGRAM_TEXTURE);
			shape = createShape(SHAPE_CUBE_TEXTURE, program, s_image);
			MapInfoDoor mapInfoDoor = new MapInfoDoor();

			mapInfoDoor.openingPosition = readVector3f(values[2]);
			mapInfoDoor.orientation = ((MathUtil.parseInt(values[3]) == 1) ? Orientation.NORTH : Orientation.WEST);
			mapInfoDoor.speed = MathUtil.parseFloat(values[4]);

			mapInfoDoor.shape = shape;
			mapInfoDoor.solid = true;

			infoMap.put(ch, mapInfoDoor);
			return;
		}

		int solid = 0;
		String s_solid = values[2];

		try {
			solid = Integer.parseInt(s_solid);
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}

		mapInfo.shape = shape;
		mapInfo.solid = solid == 1f;

		infoMap.put(ch, mapInfo);
	}

	protected Shape createShape(String shape, ShaderProgram program, String texture) {

		if (shape.equals(SHAPE_CUBE_TEXTURE))
			return new ShapeCubeTexture(program, texture);

		if (shape.equals(SHAPE_QUAD_TEXTURE))
			return new ShapeQuadTexture(program, texture);

		return null;
	}

	private ShaderProgram getProgram(String shaderProgram) {
		if (shaderProgram.equals(PROGRAM_TEXTURE))
			return ShaderProgram.getProgram("texture");

		if (shaderProgram.equals(PROGRAM_BILLBOARD_TEXTURE))
			return ShaderProgram.getProgram("texture_billboard");

		if (shaderProgram.equals(PROGRAM_BILLBOARD_ANIMATED))
			return ShaderProgram.getProgram("texture_billboard");

		return null;
	}

	protected void setSky(String value) {

		String[] values = value.split(", ");
		Vector3f downColor = readVector3f(values[0]);
		Vector3f upColor = readVector3f(values[1]);

		downColor.scale(1.0f / 256);
		upColor.scale(1.0f / 256);

		ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(ShaderProgram.getProgram("color"), upColor, downColor);

		map.sky = new EntityActor(skyShape);
	}

	protected Vector3f readVector3f(String value) {
		String[] v = value.split(" ");

		float x = MathUtil.parseFloat(v[0]);
		float y = MathUtil.parseFloat(v[1]);
		float z = MathUtil.parseFloat(v[2]);

		return new Vector3f(x, y, z);
	}
}
