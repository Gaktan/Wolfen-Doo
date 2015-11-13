package engine.game;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import engine.BitMapFont;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.animations.AnimatedActor;
import engine.animations.CustomAnimatedActorExample;
import engine.entities.Camera;
import engine.entities.ParticleSystem;
import engine.generator.MapReader;
import engine.generator.MazeGenerator;
import engine.shapes.*;

@SuppressWarnings("unused")
public class GameWolfen extends Game {

	private static final float MAX_DELTA = 20.f;
	
	private static final float Z_NEAR = 0.2f;
	private static final float Z_FAR = 100.0f;

	public ShaderProgram shaderProgramTex;
	public ShaderProgram shaderProgramColor;
	public ShaderProgram shaderProgramTexBill;
	public ShaderProgram shaderProgramTexCamera;

	public Camera camera;

	public ShapeQuadTexture shapeAnimatedSmurf;
	public ShapeQuadTexture shapeImpact;

	public DisplayableList ac;
	public Map map;

	/* TEMP STUFF */

	public BitMapFont bmf;
	public DisplayableText textPos;
	public DisplayableText textFps;
	public DisplayableText textEntities;

	public AnimatedActor animatedActorTest;
	public long elapsedTime;
	public Fps fps;
	public long l_fps;

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

		shaderProgramTex = new ShaderProgram("texture");
		shaderProgramColor = new ShaderProgram("color");
		shaderProgramTexBill = new ShaderProgram("texture_billboard", "texture");
		shaderProgramTexCamera = new ShaderProgram("texture_camera", "texture");

		camera = new Camera(45.0f, (float) getWidth() / (float) getHeight(), Z_NEAR, Z_FAR);
		camera.setPosition(new Vector3f(2, 0, 2));
		
		setZfar(camera.getzFar());

		shapeAnimatedSmurf = new ShapeQuadTexture(shaderProgramTexBill, "mul_test");

		shapeImpact = new ShapeQuadTexture(shaderProgramTex, "bullet_impact");

		MapReader mr = new MapReader(this, "01");
		map = mr.createMap();
		//map = new MazeGenerator(this, 20, 50).generate();

		ac = new DisplayableList();

		ac.add(map);

		animatedActorTest = new CustomAnimatedActorExample(shapeAnimatedSmurf, "test", "a_running_front");
		animatedActorTest.position.x = 3;
		animatedActorTest.position.z = 5;
		ac.add(animatedActorTest);


		bmf = new BitMapFont(this, "char", 256, 16);

		textPos = bmf.createString(new Vector3f(-.95f, .95f, 0), "", false);
		textFps = bmf.createString(new Vector3f(-.95f, .85f, 0), "", false);
		textEntities = bmf.createString(new Vector3f(-.95f, .75f, 0), "", false);

		fps = new Fps();

		ParticleSystem ps = new ParticleSystem(this, new Vector3f(4, 0, 4), 16000);
		ac.add(ps);
	}

	@Override
	public void update(float elapsedTime) {

		if (elapsedTime > MAX_DELTA) {
			elapsedTime = MAX_DELTA;
		}

		Controls.update(camera, elapsedTime);

		camera.update(elapsedTime);
		ac.update(elapsedTime);
		
		textPos.setText(Math.round(camera.position.x) + ", " + Math.round(camera.position.z));
		textFps.setText("fps : " + l_fps);
		textEntities.setText("Entities : " + ac.size());
		
		textPos.update(elapsedTime);
		textFps.update(elapsedTime);
		textEntities.update(elapsedTime);

		l_fps = fps.update();
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		//GL11.glClearColor(0, 0, 0, 1);

		camera.apply();
		ac.render(camera);
		
		textPos.render(camera);
		textFps.render(camera);
		textEntities.render(camera);
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
		shaderProgramTex.dispose();
		shaderProgramColor.dispose();
		shaderProgramTexBill.dispose();
		shaderProgramTexCamera.dispose();
	}
	
	protected void setZfar(float zFar) {
		camera.setzFar(zFar);
		
		shaderProgramTex.bind();
		shaderProgramTex.setUniform("u_zfar", zFar);
		shaderProgramColor.bind();
		shaderProgramColor.setUniform("u_zfar", zFar);
		shaderProgramTexBill.bind();
		shaderProgramTexBill.setUniform("u_zfar", zFar);
		shaderProgramTexCamera.bind();
		shaderProgramTexCamera.setUniform("u_zfar", zFar);
		
		ShaderProgram.unbind();
	}
}
