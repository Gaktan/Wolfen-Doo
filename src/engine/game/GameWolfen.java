package engine.game;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import engine.BitMapFont;
import engine.DisplayableList;
import engine.DisplayableText;
import engine.entities.AnimatedActor;
import engine.entities.Camera;
import engine.entities.ParticleSystem;
import engine.generator.MapReader;
import engine.generator.MazeGenerator;
import engine.shapes.*;

public class GameWolfen extends Game{

	public ShaderProgram shaderProgramTex;
	public ShaderProgram shaderProgramSky;
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
	public float total;

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
		shaderProgramSky = new ShaderProgram("sky_color");
		shaderProgramTexBill = new ShaderProgram("texture_billboard");
		shaderProgramTexCamera = new ShaderProgram("texture_camera");

		camera = new Camera(45, (float) getWidth() / (float) getHeight(), 0.2f, 100f);
		camera.setPosition(new Vector3f(2, 0, 2));

		shapeAnimatedSmurf = new ShapeQuadTexture(shaderProgramTexBill, "mul_test");
		
		shapeImpact = new ShapeQuadTexture(shaderProgramTex, "bullet_impact");

		MapReader mr = new MapReader(this, "01");
		map = mr.createMap();
		//map = new MazeGenerator(this, 20, 50).generate();

		ac = new DisplayableList();

		ac.add(map);

		animatedActorTest = new AnimatedActor(shapeAnimatedSmurf, 512, 128);
		animatedActorTest.position = new Vector3f(5, 0, 10);
		ac.add(animatedActorTest);


		bmf = new BitMapFont(this, "char", 256, 16);
		
		textPos = bmf.createString(new Vector3f(-.95f, .95f, 0), "", false);
		textFps = bmf.createString(new Vector3f(-.95f, .85f, 0), "", false);
		textEntities = bmf.createString(new Vector3f(-.95f, .75f, 0), "", false);
		
		ac.add(textPos);
		ac.add(textFps);
		ac.add(textEntities);

		fps = new Fps();

		ParticleSystem ps = new ParticleSystem(this, new Vector3f(4, 0, 4), 800);
		ac.add(ps);
	}

	@Override
	public void update(float elapsedTime) {
		Controls.update(camera, elapsedTime);

		total += elapsedTime;
		/*
		if(total > elapsedTime * 100){
			ac.remove(map);
			map = new MazeGenerator(this, 51, 31).generate();
			ac.add(map);

			total = 0;
		}
		 */
		camera.update(elapsedTime);
		ac.update(elapsedTime);
		

		l_fps = fps.calcFPS();
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL11.glClearColor(0, 0, 0, 1);

		camera.apply();
		ac.render(camera);
		
		textPos.setText(Math.round(camera.position.x) + ", " + Math.round(camera.position.z));
		textFps.setText("fps : " + l_fps);
		textEntities.setText("Entities : " + ac.size());
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
