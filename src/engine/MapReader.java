package engine;

import java.util.HashMap;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;
import engine.game.GameWolfen;
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
	public static final String COMMAND_MAP = "map";

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
		String data = FileUtil.readFromFile(path);

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

		else if(command.equals(COMMAND_ACTOR))
			createShape(value);

		else if(command.equals(COMMAND_MAP))
			mapData = value.replace("\n", "");

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

					if(animated)
						map.newAnimatedActor(i, j, shape, solid);

					else if(wall)
						map.newWall(i, j, (ShapeCubeTexture) shape, solid);
					else
						map.newActor(i, j, shape, solid);
				}
			}
		}
	}

	public void createShape(String value){
		String[] values = value.split(", ");

		char ch = values[0].charAt(0);
		String s_program = values[1];
		String s_shape = values[2];
		String s_image = values[3];
		String s_wall = values[4];
		String s_solid = values[5];
		String s_animated = values[6];

		ShaderProgram program = getProgram(s_program);
		Shape shape = createShape(s_shape, program, s_image);

		shapeMap.put(ch, shape);

		infoMap.put(ch, new Vector3f(Integer.parseInt(s_wall), Integer.parseInt(s_solid), Integer.parseInt(s_animated)));
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
			return game.shaderAnimatedBillboard;

		return null;
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
