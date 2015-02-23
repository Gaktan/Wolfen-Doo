package engine.game;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.Camera;
import engine.Controls;
import engine.DisplayableList;
import engine.Map;
import engine.MapReader;
import engine.animation.AnimatedActor;
import engine.shapes.*;
import shaders.ShaderProgram;

public class GameWolfen extends Game{

	public ShaderProgram shaderProgramTex;
	public ShaderProgram shaderAnimatedBillboard;
	public ShaderProgram shaderProgramSky;
	public ShaderProgram shaderProgramTexBill;

	public Camera camera;

	public ShapeQuadTexture shapeAnimatedSmurf;

	public DisplayableList ac;
	public Map map;

	/* TEMP STUFF */

	public AnimatedActor animatedActorTest;

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

		shaderProgramTex = new ShaderProgram("shaders/texture");
		shaderAnimatedBillboard = new ShaderProgram("shaders/animated_billboard");
		shaderProgramSky = new ShaderProgram("shaders/sky_color");
		shaderProgramTexBill = new ShaderProgram("shaders/texture_billboard");

		camera = new Camera(45, (float) getWidth() / (float) getHeight(), 0.1f, 100f);
		camera.setPosition(new Vector3f(15, 0, 3));

		shapeAnimatedSmurf = new ShapeQuadTexture(shaderAnimatedBillboard, "mul_test.png");

		MapReader mr = new MapReader(this, "maps/01.map");
		map = mr.createMap();

		ac = new DisplayableList();

		ac.add(map);

		animatedActorTest = new AnimatedActor(shapeAnimatedSmurf, 512, 128);
		animatedActorTest.position = new Vector3f(5, 0, 10);
		ac.add(animatedActorTest);
	}

	@Override
	public void update(long elapsedTime) {
		Controls.update(camera, elapsedTime);

		ac.update(elapsedTime);
		camera.update(elapsedTime);
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glClearColor(1, 1, 1, 1);

		camera.apply();
		ac.render(camera);
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

	}
}
