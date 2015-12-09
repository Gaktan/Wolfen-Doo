package engine.game;

import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import engine.BitMapFont;
import engine.DisplayableInstancedList;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.entities.Item;
import engine.generator.DungeonGenerator;
import engine.generator.Map;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.shapes.ShapeInstancedSprite;
import engine.shapes.ShapeQuadTexture;
import engine.shapes.ShapeSprite;
import engine.util.Vector3;
import engine.weapons.Weapon;
import game.animations.CustomAnimatedActorExample;
import game.entities.RotatingText;
import game.particles.AnimatedParticleSystemTest;
import game.weapons.WeaponRevolver;

@SuppressWarnings("unused")
public class GameWolfen extends Game {

	public Camera					camera;

	public DisplayableList			ac;
	public Map						map;

	public DisplayableInstancedList	bulletHoles;

	public BitMapFont				bmf;
	public DisplayableText			textPos;
	public DisplayableText			textFps;

	/* TEMP STUFF */

	public DisplayableText			textEntities;
	public DisplayableText			textMemory;
	public long						elapsedTime;
	public Fps						fps;
	public long						l_fps;

	public Weapon					currentWeapon;

	@Override
	public void dispose() {
		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.dispose();
		}
	}

	@Override
	public void init() {

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// Enable Depth Testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		ShaderProgram shaderProgramTex = new ShaderProgram("texture");
		ShaderProgram shaderProgramColor = new ShaderProgram("color");
		ShaderProgram shaderProgramTexBill = new ShaderProgram("texture_billboard", "texture", "texture_billboard");
		ShaderProgram shaderProgramTexCamera = new ShaderProgram("texture_camera", "texture", "texture_camera");
		ShaderProgram shaderProgramTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture",
				"texture_camera_instanced");
		ShaderProgram shaderProgramTexBillInstanced = new ShaderProgram("texture_billboard_instanced", "texture",
				"texture_billboard_instanced");
		ShaderProgram shaderProgramTexInstanced = new ShaderProgram("texture_instanced", "texture", "texture_instanced");
		ShaderProgram shaderProgramScreen = new ShaderProgram("texture_camera", "screen", "screen");

		FrameBuffer.getInstance().init();

		camera = new Player(45.0f, (float) getWidth() / (float) getHeight(), Z_NEAR, Z_FAR);
		// camera.setPosition(new Vector3(2, 0, 2));
		setZfar(camera.getzFar());

		ac = new DisplayableList();

		ShapeSprite shapeAnimatedSmurf = new ShapeSprite(shaderProgramTexBill, "mul_test.png", 256, 256, 64, 64);
		ShapeInstancedSprite shapeExplosion = new ShapeInstancedSprite(shaderProgramTexBillInstanced, "exp2.png", 256,
				256, 64, 64);

		map = new DungeonGenerator(30, 4, 8, 4, false).generate();
		camera.setPosition(map.getStartingPoint());

		// MapReader mr = new MapReader();
		// map = mr.createMap("01.map");
		ac.add(map);

		AnimatedActor animatedActorTest = new CustomAnimatedActorExample(shapeAnimatedSmurf, "test", "a_running_front");
		animatedActorTest.position.set(3, 0, 5);
		ac.add(animatedActorTest);

		bmf = new BitMapFont(new ShapeInstancedSprite(shaderProgramTexCameraInstanced, "char.png", 256, 256, 16, 16));

		textPos = bmf.createString(new Vector3(-1f, 1f, 0), "", 0.85f);
		textFps = bmf.createString(new Vector3(-1f, .9f, 0), "", 0.85f);
		textEntities = bmf.createString(new Vector3(-1f, .8f, 0), "", 0.85f);
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
		ac.add(worldText);

		worldTextPos = map.getStartingPoint().getAdd(new Vector3(-1.5f, 0.4f, -1.5f));

		worldText = worldFont.createString(worldTextPos, welcomeText, 0.45f, new Color(0f, 0f, 0f), true);
		ac.add(worldText);

		RotatingText rotatingText = new RotatingText(new Vector3(9, 0.25f, 3), "WELCOME !", worldFont, 1f, new Color(
				1f, 0f, 1f), TextPosition.CENTER, true);
		ac.add(rotatingText);

		Item item = new Item(new ShapeQuadTexture(shaderProgramTex, "wall.png"));
		item.position.set(9, 0, 3);
		ac.add(item);

		/*
		 * EntityActor gui = new EntityActor(new
		 * ShapeQuadTexture(shaderProgramTexCamera, "gui")); gui.position.x =
		 * 0f; gui.position.y = -0.9f; gui.scale.x = 20f; gui.scale.y = 2f;
		 * ac.add(gui);
		 */

		fps = new Fps();

		// ParticleSystem ps = new ParticleSystemBlood(new Vector3(4, 0, 4),
		// 16000);
		// ac.add(ps);

		ParticleSystem psTest = new AnimatedParticleSystemTest(new Vector3(4, 0, 4), 16000, shapeExplosion);
		ac.add(psTest);

		currentWeapon = new WeaponRevolver(camera);

		bulletHoles = new DisplayableInstancedList(new ShapeInstancedQuadTexture(shaderProgramTexInstanced,
				"bullet_impact.png"), false);

		ac.add(bulletHoles);
	}

	@Override
	public void render() {
		FrameBuffer.getInstance().bind();

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// GL11.glClearColor(0, 0, 0, 1);

		camera.apply();

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_projection", camera.getProjection());
			program.setUniform("u_view", camera.getMatrixView());
		}
		ShaderProgram.unbind();

		ac.render();

		textPos.render();
		textFps.render();
		textEntities.render();
		textMemory.render();

		currentWeapon.render();

		FrameBuffer.getInstance().render();
		FrameBuffer.getInstance().unbind();
	}

	@Override
	public void resized() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if (camera != null)
			camera.setAspect(getWidth() / getHeight());
	}

	@Override
	public void update(float elapsedTime) {

		if (elapsedTime > MAX_DELTA) {
			elapsedTime = MAX_DELTA;
		}

		// Timescale!
		// elapsedTime *= 0.1f;

		Controls.update(camera, elapsedTime);

		camera.update(elapsedTime);

		ac.update(elapsedTime);

		textPos.setText(Math.round(camera.position.getX()) + ", " + Math.round(camera.position.getZ()));
		textFps.setText("fps : " + l_fps);
		textEntities.setText("Displaying : " + ac.size());

		int mb = 1024 * 1024;

		textMemory.setText("Total Memory: " + (Runtime.getRuntime().totalMemory() / mb) + "MB\nFree Memory: "
				+ (Runtime.getRuntime().freeMemory() / mb) + "MB\nUsed Memory: "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb) + "MB\nMax Memory: "
				+ (Runtime.getRuntime().maxMemory() / mb) + "MB");

		textPos.update(elapsedTime);
		textFps.update(elapsedTime);
		textEntities.update(elapsedTime);
		textMemory.update(elapsedTime);

		currentWeapon.update(elapsedTime);

		l_fps = fps.update();
	}

	protected void setZfar(float zFar) {
		camera.setzFar(zFar);

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_zfar", zFar);
		}

		ShaderProgram.unbind();
	}

	private static final float	MAX_DELTA	= 20.f;

	private static final float	Z_NEAR		= 0.1f;

	private static final float	Z_FAR		= 100.0f;

	public static GameWolfen getInstance() {
		return (GameWolfen) instance;
	}
}
