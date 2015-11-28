package engine.game;

import java.util.Map.Entry;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.BitMapFont;
import engine.DisplayableInstancedList;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.generator.Map;
import engine.generator.OldMap;
import engine.generator.OldMapReader;
import engine.generator.MazeGenerator;
import engine.generator.MapReader;
import engine.particles.ParticleSystem;
import engine.shapes.*;
import engine.weapons.Weapon;
import game.animations.CustomAnimatedActorExample;
import game.particles.ParticleSystemBlood;
import game.weapons.WeaponRevolver;

@SuppressWarnings("unused")
public class GameWolfen extends Game {

	private static final float MAX_DELTA = 20.f;

	private static final float Z_NEAR = 0.1f;
	private static final float Z_FAR = 100.0f;

	public Camera camera;

	public ShapeQuadTexture shapeAnimatedSmurf;

	public DisplayableList ac;
	public Map map;
	public DisplayableInstancedList bulletHoles;

	/* TEMP STUFF */

	public BitMapFont bmf;
	public DisplayableText textPos;
	public DisplayableText textFps;
	public DisplayableText textEntities;

	public AnimatedActor animatedActorTest;
	public long elapsedTime;
	public Fps fps;
	public long l_fps;

	public Weapon currentWeapon;

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
		ShaderProgram shaderProgramTexCameraInstanced = new ShaderProgram("texture_camera_instanced", "texture", "texture_camera_instanced");
		ShaderProgram shaderProgramTexBillInstanced = new ShaderProgram("texture_billboard_instanced", "texture", 
				"texture_billboard_instanced");
		ShaderProgram shaderProgramTexInstanced = new ShaderProgram("texture_instanced", "texture", 
				"texture_instanced");

		camera = new Camera(45.0f, (float) getWidth() / (float) getHeight(), Z_NEAR, Z_FAR);
		camera.setPosition(new Vector3f(2, 0, 2));

		setZfar(camera.getzFar());

		ac = new DisplayableList();

		shapeAnimatedSmurf = new ShapeQuadTexture(shaderProgramTexBill, "mul_test.png");

		//MapReader mr = new MapReader(this, "01.map");
		//map = mr.createMap();
		//map = new MazeGenerator(this, 75, 75).generate();

		//ac.add(map);

		MapReader mr = new MapReader();
		map = mr.createMap("01.map");
		ac.add(map);

		animatedActorTest = new CustomAnimatedActorExample(shapeAnimatedSmurf, "test", "a_running_front");
		animatedActorTest.position.set(3, 0, 5);
		ac.add(animatedActorTest);

		bmf = new BitMapFont(new ShapeInstancedQuadTexture(shaderProgramTexCameraInstanced, "char.png"), 256, 256, 16, 16);

		textPos = bmf.createString(new Vector3f(-1f, 1f, 0), "", 0.85f);
		textFps = bmf.createString(new Vector3f(-1f, .9f, 0), "", 0.85f);
		textEntities = bmf.createString(new Vector3f(-1f, .8f, 0), "", 0.85f);

		BitMapFont worldFont = new BitMapFont(new ShapeInstancedQuadTexture(shaderProgramTexInstanced, "char.png"),
				256, 256, 16, 16);

		String welcomeText = "Hello and welcome to Wolfen-doo. You can't to much right now,\n"
				+ "but it will come, don't worry.\n"
				+ "Use ZQSD to move around, mouse to look and shoot,\n"
				+ "'E' to open doors, 'R' to reload.";

		DisplayableText text = worldFont.createString(new Vector3f(0.5f, 0.4f, 0.5f), welcomeText, 0.45f, 
				new Color(0f, 0f, 0f), true);
		ac.add(text);

		/*
		EntityActor gui = new EntityActor(new ShapeQuadTexture(shaderProgramTexCamera, "gui"));
		gui.position.x = 0f;
		gui.position.y = -0.9f;
		gui.scale.x = 20f;
		gui.scale.y = 2f;
		ac.add(gui);
		 */

		fps = new Fps();

		ParticleSystem ps = new ParticleSystemBlood(new Vector3f(4, 0, 4), 16000);
		ac.add(ps);

		currentWeapon = new WeaponRevolver(camera);

		bulletHoles = new DisplayableInstancedList(new ShapeInstancedQuadTexture(shaderProgramTexInstanced, 
				"bullet_impact.png"), false);

		ac.add(bulletHoles);
	}

	@Override
	public void update(float elapsedTime) {

		if (elapsedTime > MAX_DELTA) {
			elapsedTime = MAX_DELTA;
		}

		// Timescale!
		//elapsedTime *= 0.1f;

		Controls.update(camera, elapsedTime);

		camera.update(elapsedTime);

		ac.update(elapsedTime);

		textPos.setText(Math.round(camera.position.x) + ", " + Math.round(camera.position.z));
		textFps.setText("fps : " + l_fps);
		textEntities.setText("Entities : " + ac.size());

		textPos.update(elapsedTime);
		textFps.update(elapsedTime);
		textEntities.update(elapsedTime);

		currentWeapon.update(elapsedTime);

		l_fps = fps.update();
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		//GL11.glClearColor(0, 0, 0, 1);

		camera.apply();

		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.bind();
			program.setUniform("u_projection", camera.projection);
			program.setUniform("u_view", camera.getMatrixView());
		}

		ShaderProgram.unbind();

		ac.render();

		textPos.render();
		textFps.render();
		textEntities.render();

		currentWeapon.render();
	}

	@Override
	public void resized() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if(camera != null)
			camera.setAspect(getWidth() / getHeight());
	}

	@Override
	public void dispose() {
		for (Entry<String, ShaderProgram> entry : ShaderProgram.getAllPrograms()) {
			ShaderProgram program = entry.getValue();

			program.dispose();
		}
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

	public static GameWolfen getInstance() {
		return (GameWolfen) instance;
	}
}
