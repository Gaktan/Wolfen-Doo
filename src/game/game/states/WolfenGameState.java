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
import engine.entities.Item;
import engine.game.Controls;
import engine.game.Fps;
import engine.game.FrameBuffer;
import engine.game.Game;
import engine.game.Player;
import engine.game.states.GameState;
import engine.generator.DungeonGenerator;
import engine.generator.Map;
import engine.particles.ParticleSystem;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeQuadTexture;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.animations.CustomAnimatedActorExample;
import game.entities.RotatingText;
import game.particles.AnimatedParticleSystemExplosion;

public class WolfenGameState extends GameState {

	protected static final float Z_NEAR = 0.1f;
	protected static final float Z_FAR = 100.0f;

	protected Player player;

	protected DisplayableList displayableList;
	protected Map map;
	protected DisplayableInstancedList bulletHoles;

	/* TEMP STUFF */

	protected BitMapFont bmf;
	protected DisplayableText textPos;
	protected DisplayableText textFps;
	protected DisplayableText textMemory;

	protected Fps fps;
	protected long l_fps;

	protected DungeonGenerator generator;

	protected FrameBuffer frameBuffer;

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

		Controls.removeControlsListener(Player.getInstance());
		Controls.removeMouseListener(Player.getInstance());
	}

	public Map getMap() {
		return map;
	}

	@Override
	public void init() {
		ShaderProgram shaderProgramTex = new ShaderProgram("texture");
		new ShaderProgram("color");
		ShaderProgram shaderProgramTexBill = new ShaderProgram("texture_billboard", "texture", "texture_billboard");
		new ShaderProgram("texture_camera", "texture", "texture_camera");
		ShaderProgram shaderProgramTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture",
				"texture_camera_instanced");
		ShaderProgram shaderProgramTexBillInstanced = new ShaderProgram("texture_billboard_instanced", "texture",
				"texture_billboard_instanced");
		ShaderProgram shaderProgramTexInstanced = new ShaderProgram("texture_instanced", "texture", "texture_instanced");
		new ShaderProgram("texture_camera", "screen", "screen");

		frameBuffer = new FrameBuffer();
		frameBuffer.init("screen");

		current_camera = new Camera(45.0f, (float) Game.getInstance().getWidth()
				/ (float) Game.getInstance().getHeight(), Z_NEAR, Z_FAR);

		setZfar(current_camera.getzFar());

		player = new Player(current_camera);

		displayableList = new DisplayableList();

		ShapeSprite shapeAnimatedSmurf = new ShapeSprite(shaderProgramTexBill, "mul_test.png", 256, 256, 64, 64);
		ShapeInstancedSprite shapeExplosion = new ShapeInstancedSprite(shaderProgramTexBillInstanced, "exp2.png", 256,
				256, 64, 64);

		generator = new DungeonGenerator(30, 4, (long) MathUtil.random(0, 10000), 4, false);

		map = generator.generate();
		player.position.set(map.getStartingPoint());

		// MapReader mr = new MapReader();
		// map = mr.createMap("01.map");
		add(map);

		AnimatedActor animatedActorTest = new CustomAnimatedActorExample(shapeAnimatedSmurf, "test", "a_running_front");
		animatedActorTest.position.set(3, 0, 5);
		add(animatedActorTest);

		bmf = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexCameraInstanced, "char.png", 256, 256, 16, 16));

		textPos = bmf.createString(new Vector3(-1f, 1f, 0), "", 0.85f);
		textFps = bmf.createString(new Vector3(-1f, .9f, 0), "", 0.85f);
		textMemory = bmf.createString(new Vector3(-1f, 0.7f, 0), "", 0.6f, new Color(0f, 0f, 0f));

		BitMapFont worldFont = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexInstanced, "char.png", 256, 256,
				16, 16));

		String welcomeText = "Hello and welcome to Wolfen-doo. You can't do much right now,\n"
				+ "but it will come, don't worry.\n" + "Use WASD to move around, mouse to look and shoot,\n"
				+ "'E' to open doors, 'R' to reload.";

		String rotatedText = "Woah ! You can even rotate text !";

		Vector3 worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, 0f));

		DisplayableText worldText = worldFont.createString(worldTextPos, rotatedText, 0.45f, new Color(0f, 0f, 0f),
				true);
		worldText.setRotation(90f);
		worldText.updateText();
		add(worldText);

		worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, -1.5f));

		worldText = worldFont.createString(worldTextPos, welcomeText, 0.45f, new Color(0f, 0f, 0f), true);
		add(worldText);

		RotatingText rotatingText = new RotatingText(new Vector3(9, 0.25f, 3), "WELCOME !", worldFont, 1f, new Color(
				1f, 0f, 1f), TextPosition.CENTER, true);
		add(rotatingText);

		Item item = new Item(new ShapeQuadTexture(shaderProgramTex, "wall.png"));
		item.position.set(9, 0, 3);
		add(item);

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
		frameBuffer.unbind();
	}

	@Override
	public void update(float dt) {
		current_camera.update(dt);
		player.update(dt);

		displayableList.update(dt);

		textPos.setText(Math.round(player.position.getX()) + ", " + Math.round(player.position.getZ()));
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

	protected void setZfar(float zFar) {
		current_camera.setzFar(zFar);

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_zfar", zFar);
		}

		ShaderProgram.unbind();
	}
}
