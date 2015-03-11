package engine.entities;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.shapes.Shape;
import engine.util.MathUtil;

public class Particle implements Displayable{
	
	public int life;
	EntityActor actor;
	private static final float factor = 1f;
	private boolean paused;
	private static final Vector3f GRAVITY = new Vector3f(0, -0.04f, 0);
	
	public Particle(GameWolfen game, Shape shape, int life, Vector3f position){
		
		
		actor = new EntityActor(shape);
		actor.position = new Vector3f(position);
		
		actor.velocity = new Vector3f(MathUtil.randomNegPos(-factor, factor), 0.5f, MathUtil.randomNegPos(-factor, factor));
		actor.scale = MathUtil.random(0.05f, 0.1f);
		
		//Random r = new Random();
		
		//actor.color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
		
		this.life = life;
	}
	
	public Particle(int life){
		this.life = life;
		paused = true;
	}

	@Override
	public boolean update(float dt) {
		
		life--;
		
		if(life <= 0)
			return false;
		
		if(paused)
			return true;
		
		Vector3f.add(actor.velocity, GRAVITY, actor.velocity);
		
		//position of the floor - size of Particle
		if(actor.position.y <= -0.45f){
			actor.position.y = -0.45f;
			paused = true;
		}
		
		else
			actor.update(dt);
		
		return true;
	}

	@Override
	public void render(Camera camera) {
		if(actor != null)
			actor.render(camera);
	}

	@Override
	public void delete() {
		life = 0;
	}
	
}
