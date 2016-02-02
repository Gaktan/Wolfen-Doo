package game.game.states;

import org.newdawn.slick.Color;

import engine.animations.AnimatedActor;
import engine.entities.BitMapFont;
import engine.entities.Camera;
import engine.entities.Displayable;
import engine.entities.DisplayableInstancedList;
import engine.entities.DisplayableList;
import engine.entities.DisplayableText;
import engine.entities.DisplayableText.TextPosition;
import engine.entities.EntityActor;
import engine.entities.EntityLine;
import engine.game.Controls;
import engine.game.Fps;
import engine.game.FrameBuffer;
import engine.game.FrameBuffer.RenderLast;
import engine.game.Game;
import engine.game.Player;
import engine.game.states.GameState;
import engine.particles.ParticleSystem;
import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.animations.CustomAnimatedActorExample;
import game.entities.Item;
import game.entities.ItemList;
import game.entities.RotatingText;
import game.game.WolfenPlayer;
import game.generator.DungeonGenerator;
import game.generator.Generator;
import game.generator.Map;
import game.generator.MapReader;
import game.particles.ParticleSystemExplosion;

public class WolfenGameState extends GameState {

	protected static final float Z_NEAR = 0.1f;
	protected static final float Z_FAR = 100.0f;

	protected WolfenPlayer player;

	protected DisplayableList<Displayable> displayableList;
	protected Map map;
	protected DisplayableInstancedList<EntityActor> bulletHoles;

	protected ItemList itemList;

	/* Shader programs */
	protected ShaderProgram programTexture;
	protected ShaderProgram programColor;
	protected ShaderProgram programFrameBuffer;
	protected ShaderProgram programTexBill;
	protected ShaderProgram programTexCamera;
	protected ShaderProgram programTexCameraInstanced;
	protected ShaderProgram programTexBillInstanced;
	protected ShaderProgram programTexInstanced;

	/* Shapes */
	protected ShapeInstancedSprite itemShape;
	protected ShapeInstancedQuadTexture bulletShape;

	/* TEMP STUFF */

	protected BitMapFont bmf;
	protected DisplayableText textPos;
	protected DisplayableText textFps;
	protected DisplayableText textMemory;

	protected Fps fps;
	protected long l_fps;

	protected long seed;
	protected String mapName;

	public FrameBuffer frameBuffer;

	public WolfenGameState(long seed) {
		this.seed = seed;
		mapName = null;
	}

	public WolfenGameState(String mapName) {
		this.mapName = mapName;
	}

	public void add(Displayable d) {
		displayableList.add(d);
	}

	public void addBulletHole(EntityActor d) {
		bulletHoles.add(d);
	}

	@Override
	public void dispose() {
		programTexture.dispose();
		programColor.dispose();
		programTexBill.dispose();
		programTexCamera.dispose();
		programTexCameraInstanced.dispose();
		programTexBillInstanced.dispose();
		programTexInstanced.dispose();

		itemShape.dispose();
		bulletShape.dispose();

		frameBuffer.dispose();

		Controls.removeControlsListener(player);
		Controls.removeMouseListener(player);

		displayableList.dispose();
		bmf.dispose();
		textPos.dispose();
		textFps.dispose();
		textMemory.dispose();
	}

	public Map getMap() {
		return map;
	}

	@Override
	public void init() {
		displayableList = new DisplayableList<Displayable>();

		fps = new Fps();

		programTexture = new ShaderProgram("texture");
		programColor = new ShaderProgram("color");
		programFrameBuffer = new ShaderProgram("frameBuffer", "texture", "frameBuffer");
		programTexBill = new ShaderProgram("texture_billboard", "texture", "texture_billboard");
		programTexCamera = new ShaderProgram("texture_camera", "texture", "texture_camera");
		programTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture", "texture_camera_instanced");
		programTexBillInstanced = new ShaderProgram("texture_billboard_instanced", "texture",
				"texture_billboard_instanced");
		programTexInstanced = new ShaderProgram("texture_instanced", "texture", "texture_instanced");

		// FrameBuffer
		frameBuffer = new FrameBuffer();
		frameBuffer.init(programFrameBuffer);

		// Camera
		current_camera = new Camera(45.0f, (float) Game.getInstance().getWidth()
				/ (float) Game.getInstance().getHeight(), Z_NEAR, Z_FAR);
		setZfar(current_camera.getzFar());

		// Map
		if (mapName == null) {
			Generator generator = new DungeonGenerator().setSizeX(30).setSizeY(4).setRoomSize(3).setSeed(seed)
					.setIntersections(true);
			map = generator.generate();
		}
		else {
			map = MapReader.createMap(mapName);
		}
		add(map);

		// Player
		player = new WolfenPlayer(current_camera, map);
		Controls.addControlsListener(player);
		Controls.addMouseListener(player);

		// Items
		itemShape = new ShapeInstancedSprite(programTexBillInstanced, "items.png", 128, 64, 32, 32);
		itemList = new ItemList(itemShape, player);
		addItem(map.getStartingPoint().getAdd(-1f, 0f, -1f), 0, 100);
		addItem(map.getStartingPoint().getAdd(0f, 0f, -1f), 1, 100);
		addItem(map.getStartingPoint().getAdd(1f, 0f, -1f), 2, 100);
		addItem(map.getStartingPoint().getAdd(2f, 0f, -1f), 3, 100);
		add(itemList);

		// Screen Text
		bmf = new BitMapFont(new ShapeInstancedSprite(programTexCameraInstanced, "scumm_font.png", 128, 256, 8, 11));
		textPos = bmf.createString(new Vector3(-1f, 1f, 0), "", 0.85f);
		textFps = bmf.createString(new Vector3(-1f, .9f, 0), "", 0.85f);
		textMemory = bmf.createString(new Vector3(-1f, 0.7f, 0), "", 0.6f);

		// Bullets
		bulletShape = new ShapeInstancedQuadTexture(programTexInstanced, "bullet_impact.png");
		bulletHoles = new DisplayableInstancedList<EntityActor>(bulletShape, false);
		add(bulletHoles);

		// -- Test area --

		// animation
		ShapeSprite shapeGuybrush = new ShapeSprite(programTexBill, "mul_test.png", 512, 512, 32, 48);
		AnimatedActor animatedActorTest = new CustomAnimatedActorExample(shapeGuybrush, "guybrush.animation",
				"a_walking_front", map);
		map.addActor(animatedActorTest);

		// explosion
		ShapeInstancedSprite shapeExplosion = new ShapeInstancedSprite(programTexBillInstanced, "exp2.png", 256, 256,
				64, 64);
		ParticleSystem psTest = new ParticleSystemExplosion(new Vector3(4, 0, 4), 16000, shapeExplosion);
		add(psTest);

		// text in world
		BitMapFont worldFont = new BitMapFont(new ShapeInstancedSprite(programTexInstanced, "scumm_font.png", 128, 256,
				8, 11));

		String welcomeText = "Hello and welcome to Wolfen-doo. You can't do much right now,\n"
				+ "but it will come, don't worry.\n" + "Use WASD to move around, mouse to look and shoot,\n"
				+ "'E' to open doors, 'R' to reload. '1-3' to change weapon.";

		String rotatedText = "Woah! You can even rotate text!";

		Vector3 worldTextPos = map.getStartingPoint().getAdd(new Vector3(0f, 0.4f, 0.5f));
		DisplayableText worldText = worldFont.createString(worldTextPos, rotatedText, 0.45f, true);
		worldText.setRotation(180f);
		worldText.updateText();
		add(worldText);

		worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, .5f));
		worldText = worldFont.createString(worldTextPos, welcomeText, 0.45f, true);
		worldText.setRotation(90f);
		worldText.updateText();
		add(worldText);

		RotatingText rotatingText = new RotatingText(new Vector3(13f, 0.25f, 3f), "WELCOME!", worldFont, 1f, new Color(
				1f, 0f, 1f), TextPosition.CENTER, true);
		add(rotatingText);

		// Origin
		EntityLine line_x = new EntityLine(new Vector3(), new Vector3(100f, 0f, 0f), new Vector3(1f, 0f, 0f),
				programColor);
		EntityLine line_y = new EntityLine(new Vector3(), new Vector3(0f, 100f, 0f), new Vector3(0f, 1f, 0f),
				programColor);
		EntityLine line_z = new EntityLine(new Vector3(), new Vector3(0f, 0f, 100f), new Vector3(0f, 0f, 1f),
				programColor);
		add(line_x);
		add(line_y);
		add(line_z);

		/*
		EntityActor gui = new EntityActor(new ShapeQuadTexture(shaderProgramTexCamera, "gui"));
		gui.position.x = 0f;
		gui.position.y = -0.9f;
		gui.scale.x = 20f;
		gui.scale.y = 2f;
		ac.add(gui);
		*/
	}

	protected void setProgramUniform(ShaderProgram program) {
		program.bind();
		program.setUniform(Uniform.projection, current_camera.getProjection());
		program.setUniform(Uniform.view, current_camera.getMatrixView());
		program.setUniform(Uniform.zfar, current_camera.getzFar());
	}

	protected void setProgramsUniform() {
		setProgramUniform(programTexture);
		setProgramUniform(programColor);
		setProgramUniform(programTexBill);
		setProgramUniform(programTexCamera);
		setProgramUniform(programTexCameraInstanced);
		setProgramUniform(programTexBillInstanced);
		setProgramUniform(programTexInstanced);

		ShaderProgram.unbind();
	}

	@Override
	public void render() {
		frameBuffer.bind();
		frameBuffer.clear();

		current_camera.apply();

		setProgramsUniform();

		displayableList.render();

		frameBuffer.addRenderLast(new RenderLast() {
			@Override
			public void renderLast() {
				player.render();
				textPos.render();
				textFps.render();
				textMemory.render();
			}
		});

		frameBuffer.render();

		FrameBuffer.unbind();
	}

	@Override
	public void update(float dt) {
		// dt *= 0.1f;
		current_camera.update(dt);
		player.update(dt);

		displayableList.update(dt);

		textPos.setText(MathUtil.round(player.position.getX()) + ", " + MathUtil.round(player.position.getZ()));
		textFps.setText("fps : " + l_fps);

		int mb = 1024 * 1024;

		textMemory.setText("Total Memory: " + (Runtime.getRuntime().totalMemory() / mb) + "MB\nFree Memory: "
				+ (Runtime.getRuntime().freeMemory() / mb) + "MB\nUsed Memory: "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb) + "MB");

		textPos.update(dt);
		textFps.update(dt);
		textMemory.update(dt);

		l_fps = fps.update();
	}

	public void addItem(Vector3 position, int itemNumber, int value) {
		Item item = new Item(position, itemNumber, value);
		itemList.add(item);
	}

	protected void setZfar(float zFar) {
		current_camera.setzFar(zFar);
	}

	public Player getPlayer() {
		return player;
	}
}
