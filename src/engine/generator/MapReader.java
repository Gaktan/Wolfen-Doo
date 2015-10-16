package engine.generator;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.EntityActor;
import engine.game.GameWolfen;
import engine.game.Map;
import engine.game.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInsideOutCubeColor;
import engine.shapes.ShapeQuadTexture;
import engine.util.FileUtil;

public class MapReader {

	public static final String COMMAND_NAME = "name";
	public static final String COMMAND_WIDTH = "width";
	public static final String COMMAND_HEIGHT = "height";
	public static final String COMMAND_ACTOR = "actor";
	public static final String COMMAND_WALL = "wall";
	public static final String COMMAND_ANIMATION = "animation";
	public static final String COMMAND_MAP = "map";
	public static final String COMMAND_SKY = "sky";

	public static final String PROGRAM_TEXTURE = "texture";
	public static final String PROGRAM_BILLBOARD_ANIMATED = "billboard_animated";
	public static final String PROGRAM_SKY = "sky";
	public static final String PROGRAM_BILLBOARD_TEXTURE = "billboard_texture";

	public static final String SHAPE_CUBE_TEXTURE = "cube_texture";
	public static final String SHAPE_INSIDE_OUT_CUBE_COLOR = "inside_out_cube_color";
	public static final String SHAPE_QUAD_TEXTURE = "quad_texture";

	private String path;
	private String mapData;

	@SuppressWarnings("unused")
	private String name;
	private int width;
	private int height;
	private Map map;

	private HashMap<Character, Shape> shapeMap;
	private HashMap<Character, Vector3f> infoMap;

	private GameWolfen game;


	public MapReader(GameWolfen game, String path) {
		this.game = game;
		this.path = path;

		shapeMap = new HashMap<Character, Shape>();
		infoMap = new HashMap<Character, Vector3f>();
	}

	public Map createMap(){
		map = new Map(game);

		readFile(path);

		map.setSize(width, height);
		map.setSky();
		readMap(mapData);

		map.setOrientation();
	
		System.out.println(map.list);

		return map;
	}

	public void readFile(String path){
		String data = FileUtil.readFromFile("res/maps/" + path + ".map");

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
	}

	public void performCommand(String command, String value){

		if(command.equals(COMMAND_WIDTH))
			setWidth(value);

		else if(command.equals(COMMAND_HEIGHT))
			setHeight(value);

		else if(command.equals(COMMAND_NAME))
			name = value;

		else if(command.equals(COMMAND_ACTOR) || command.equals(COMMAND_WALL)
				|| command.equals(COMMAND_ANIMATION))

			createShape(value, command);


		else if(command.equals(COMMAND_MAP))
			mapData = value.replace("\n", "");

		else if(command.equals(COMMAND_SKY))
			setSky(value);

	}

	private void readMap(String value) {

		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				char c = value.charAt((i * width) + j);

				Shape shape = shapeMap.get(c);

				if(shape != null){
					Vector3f wallSolid = infoMap.get(c);
					boolean wall = (wallSolid.x == 1);
					boolean solid = (wallSolid.y == 1);
					boolean animated = (wallSolid.z == 1);

					if(animated){
						map.newAnimatedActor(i, j, shape, solid);
					}

					else if(wall){
						map.newWall(i, j, (ShapeCubeTexture) shape, solid);
					}

					else{
						map.newActor(i, j, shape, solid);
					}
				}
			}
		}
	}

	public void createShape(String value, String command){
		String[] values = value.split(", ");

		ShaderProgram program = null;
		Shape shape = null;

		char ch = values[0].charAt(0);

		String s_image = values[1];

		int wall = 0;
		int solid = 0;
		int animated = 0;

		if(command.equals(COMMAND_ANIMATION)){
			program = getProgram(PROGRAM_BILLBOARD_ANIMATED);
			shape = createShape(SHAPE_QUAD_TEXTURE, program, s_image);
			animated = 1;
		}

		if(command.equals(COMMAND_ACTOR)){
			program = getProgram(PROGRAM_BILLBOARD_TEXTURE);
			shape = createShape(SHAPE_QUAD_TEXTURE, program, s_image);
		}

		if(command.equals(COMMAND_WALL)){
			program = getProgram(PROGRAM_TEXTURE);
			shape = createShape(SHAPE_CUBE_TEXTURE, program, s_image);
			wall = 1;
		}

		String s_solid = values[2];

		try{
			solid = Integer.parseInt(s_solid);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}

		shapeMap.put(ch, shape);

		infoMap.put(ch, new Vector3f(wall, solid, animated));
	}

	public Shape createShape(String shape, ShaderProgram program, String texture){

		if(shape.equals(SHAPE_CUBE_TEXTURE))
			return new ShapeCubeTexture(program, texture);

		if(shape.equals(SHAPE_INSIDE_OUT_CUBE_COLOR))
			return new ShapeInsideOutCubeColor(program);

		if(shape.equals(SHAPE_QUAD_TEXTURE))
			return new ShapeQuadTexture(program, texture);

		return null;
	}

	public ShaderProgram getProgram(String shaderProgram){
		if(shaderProgram.equals(PROGRAM_TEXTURE))
			return game.shaderProgramTex;

		if(shaderProgram.equals(PROGRAM_SKY))
			return game.shaderProgramSky;

		if(shaderProgram.equals(PROGRAM_BILLBOARD_TEXTURE))
			return game.shaderProgramTexBill;

		if(shaderProgram.equals(PROGRAM_BILLBOARD_ANIMATED))
			return game.shaderProgramTexBill;

		return null;
	}

	private void setSky(String value) {

		String[] values = value.split(", ");
		Vector3f downColor = readColor(values[0]);
		Vector3f upColor = readColor(values[1]);

		ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(game.shaderProgramSky);

		skyShape.downColor = downColor;
		skyShape.upColor = upColor;

		map.sky = new EntityActor(skyShape);
	}

	public Vector3f readColor(String value){
		String[] v = value.split(" ");

		try{
			float x = (float) Integer.parseInt(v[0]) / 256;
			float y = (float) Integer.parseInt(v[1]) / 256;
			float z = (float) Integer.parseInt(v[2]) / 256;
			
			return new Vector3f(x, y, z);
		}
		catch(Exception e){
			System.err.println("Exception in color : " + value);
			e.printStackTrace();
		}
		
		return new Vector3f();
	}

	public void setWidth(String value){
		try{
			width = Integer.parseInt(value);
		} catch(NumberFormatException e){
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
	}

	public void setHeight(String value){
		try{
			height = Integer.parseInt(value);
		} catch(NumberFormatException e){
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
	}
}
