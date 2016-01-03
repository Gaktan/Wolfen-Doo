package game.game.states;

import java.util.Map.Entry;

import org.newdawn.slick.Color;

import engine.BitMapFont;
import engine.Displayable;
import engine.DisplayableInstancedList;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.game.Controls;
import engine.game.Fps;
import engine.game.FrameBuffer;
import engine.game.Game;
import engine.game.Player;
import engine.game.states.GameState;
import engine.generator.Generator;
import engine.generator.Map;
import engine.generator.MapReader;
import engine.particles.ParticleSystem;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.animations.CustomAnimatedActorExample;
import game.entities.Item;
import game.entities.ItemList;
import game.entities.RotatingText;
import game.generator.DungeonGenerator;
import game.particles.AnimatedParticleSystemExplosion;

public class WolfenGameState extends GameState {

	protected static final float Z_NEAR = 0.1f;
	protected static final float Z_FAR = 100.0f;

	protected Player player;

	protected DisplayableList displayableList;
	protected Map map;
	protected DisplayableInstancedList bulletHoles;

	protected ShapeSprite itemShape;
	protected DisplayableList itemList;

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

	public void addBulletHole(Displayable d) {
		bulletHoles.add(d);
	}

	@Override
	public void dispose() {
		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.dispose();
		}

		frameBuffer.dispose();

		Controls.removeControlsListener(player);
		Controls.removeMouseListener(player);
	}

	public Map getMap() {
		return map;
	}

	@Override
	public void init() {
		new ShaderProgram("texture");
		new ShaderProgram("color");
		ShaderProgram shaderProgramTexBill = new ShaderProgram("texture_billboard", "texture", "texture_billboard");
		new ShaderProgram("texture_camera", "texture", "texture_camera");
		ShaderProgram shaderProgramTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture",
				"texture_camera_instanced");
		ShaderProgram shaderProgramTexBillInstanced = new ShaderProgram("texture_billboard_instanced", "texture",
				"texture_billboard_instanced");
		ShaderProgram shaderProgramTexInstanced = new ShaderProgram("texture_instanced", "texture", "texture_instanced");
		new ShaderProgram("texture_camera", "texture", "screen");

		frameBuffer = new FrameBuffer();
		frameBuffer.init("screen");

		current_camera = new Camera(45.0f, (float) Game.getInstance().getWidth()
				/ (float) Game.getInstance().getHeight(), Z_NEAR, Z_FAR);

		setZfar(current_camera.getzFar());

		player = new Player(current_camera);
		Controls.addControlsListener(player);
		Controls.addMouseListener(player);

		displayableList = new DisplayableList();

		ShapeSprite shapeAnimatedSmurf = new ShapeSprite(shaderProgramTexBill, "mul_test.png", 512, 512, 32, 48);
		ShapeInstancedSprite shapeExplosion = new ShapeInstancedSprite(shaderProgramTexBillInstanced, "exp2.png", 256,
				256, 64, 64);

		// Items
		itemShape = new ShapeSprite(shaderProgramTexBill, "items.png", 128, 64, 32, 32);
		itemList = new ItemList(player);

		addItem(new Vector3(9, 0, 3), 0, 100);
		addItem(new Vector3(10, 0, 3), 1, 100);
		addItem(new Vector3(11, 0, 3), 2, 100);
		addItem(new Vector3(12, 0, 3), 3, 100);

		add(itemList);

		if (mapName == null) {
			Generator generator = new DungeonGenerator().setSizeX(30).setSizeY(4).setRoomSize(3).setSeed(seed)
					.setIntersections(true);

			map = generator.generate();
		}
		else {
			map = new MapReader().createMap(mapName);
		}

		player.position.set(map.getStartingPoint());

		add(map);

		AnimatedActor animatedActorTest = new CustomAnimatedActorExample(shapeAnimatedSmurf, "guybrush.animation",
				"a_running_front");
		animatedActorTest.position.set(3, 0, 5);
		add(animatedActorTest);

		bmf = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexCameraInstanced, "scumm_font.png", 128, 256, 8,
				11));

		textPos = bmf.createString(new Vector3(-1f, 1f, 0), "", 0.85f);
		textFps = bmf.createString(new Vector3(-1f, .9f, 0), "", 0.85f);
		textMemory = bmf.createString(new Vector3(-1f, 0.7f, 0), "", 0.6f);

		BitMapFont worldFont = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexInstanced, "scumm_font.png",
				128, 256, 8, 11));

		String welcomeText = "Hello and welcome to Wolfen-doo. You can't do much right now,\n"
				+ "but it will come, don't worry.\n" + "Use WASD to move around, mouse to look and shoot,\n"
				+ "'E' to open doors, 'R' to reload. '1-3' to change weapon.";

		String rotatedText = "Woah! You can even rotate text!";

		Vector3 worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, 0f));

		DisplayableText worldText = worldFont.createString(worldTextPos, rotatedText, 0.45f, true);
		worldText.setRotation(90f);
		worldText.updateText();
		add(worldText);

		worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, -1.5f));

		worldText = worldFont.createString(worldTextPos, welcomeText, 0.45f, true);
		add(worldText);

		RotatingText rotatingText = new RotatingText(new Vector3(9, 0.25f, 3), "WELCOME!", worldFont, 1f, new Color(1f,
				0f, 1f), TextPosition.CENTER, true);
		add(rotatingText);

		/*
		EntityActor gui = new EntityActor(new ShapeQuadTexture(shaderProgramTexCamera, "gui"));
		gui.position.x = 0f;
		gui.position.y = -0.9f;
		gui.scale.x = 20f;
		gui.scale.y = 2f;
		ac.add(gui);
		*/

		fps = new Fps();

		// ParticleSystem ps = new ParticleSystemBlood(new Vector3(4, 0, 4),
		// 16000);
		// ac.add(ps);

		ParticleSystem psTest = new AnimatedParticleSystemExplosion(new Vector3(4, 0, 4), 16000, shapeExplosion);
		add(psTest);

		bulletHoles = new DisplayableInstancedList(new ShapeInstancedQuadTexture(shaderProgramTexInstanced,
				"bullet_impact.png"), false);

		add(bulletHoles);
	}

	@Override
	public void render() {
		frameBuffer.bind();
		frameBuffer.clear();

		current_camera.apply();

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_projection", current_camera.getProjection());
			program.setUniform("u_view", current_camera.getMatrixView());
		}
		ShaderProgram.unbind();

		displayableList.render();

		textPos.render();
		textFps.render();
		textMemory.render();

		player.render();

		frameBuffer.render();

		FrameBuffer.unbind();
	}

	@Override
	public void update(float dt) {

		current_camera.update(dt);
		player.update(dt);

		displayableList.update(dt);

		textPos.setText(MathUtil.round(player.position.getX()) + ", " + MathUtil.round(player.position.getZ()));
		textFps.setText("fps : " + l_fps);

		int mb = 1024 * 1024;

		textMemory.setText("Total Memory: " + (Runtime.getRuntime().totalMemory() / mb) + "MB\nFree Memory: "
				+ (Runtime.getRuntime().freeMemory() / mb) + "MB\nUsed Memory: "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb) + "MB\nMax Memory: "
				+ (Runtime.getRuntime().maxMemory() / mb) + "MB");

		textPos.update(dt);
		textFps.update(dt);
		textMemory.update(dt);

		l_fps = fps.update();
	}

	public void addItem(Vector3 position, int itemNumber, int value) {
		Item item = new Item(itemShape, position, itemNumber, value);
		itemList.add(item);
	}

	protected void setZfar(float zFar) {
		current_camera.setzFar(zFar);

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_zfar", zFar);
		}

		ShaderProgram.unbind();
	}

	public Player getPlayer() {
		return player;
	}
}
