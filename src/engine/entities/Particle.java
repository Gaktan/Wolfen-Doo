package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.shapes.ShapeQuadTexture;
import engine.util.MathUtil;

public class Particle implements Displayable{
	
	public int life;
	private EntityActor actor;
	private static final float factor = 1f;
	private boolean paused;
	private static final Vector3f GRAVITY = new Vector3f(0, -0.04f, 0);
	
	public Particle(GameWolfen game, String texture, int life, Vector3f position){
		ShapeQuadTexture shape = new ShapeQuadTexture(game.shaderProgramTexBill, texture);
		
		actor = new EntityActor(shape);
		actor.position = new Vector3f(position);
		
		System.out.println(MathUtil.randomNegPos(-factor, factor));
		
		actor.velocity = new Vector3f(MathUtil.randomNegPos(-factor, factor), 0.5f, MathUtil.randomNegPos(-factor, factor));
		actor.scale = 0.05f;
		
		this.life = life;
	}
	
	public Particle(int life){
		this.life = life;
		paused = true;
	}

	@Override
	public void update(float dt) {
		
		life--;
		
		if(paused)
			return;
		
		Vector3f.add(actor.velocity, GRAVITY, actor.velocity);
		
		//position of the floor - size of Particle
		if(actor.position.y <= -0.45f){
			actor.position.y = -0.45f;
			paused = true;
		}
		
		else
			actor.update(dt);
	}

	@Override
	public void render(Camera camera) {
		if(actor != null)
			actor.render(camera);
	}
	

}
