package engine.entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.shapes.ShapeQuadTexture;
import engine.util.MathUtil;

public class ParticleSystem implements Displayable{

	public ArrayList<Particle> list;
	private GameWolfen game;
	private Vector3f position;
	private ShapeQuadTexture blood1Shape;
	private ShapeQuadTexture blood2Shape;
	private int life;
	
	private int newParticlesPerFrame;
	private int maxParticles;

	public ParticleSystem(GameWolfen game, Vector3f position, int life) {
		list = new ArrayList<Particle>();

		this.game = game;
		this.position = position;
		this.life = life;

		blood1Shape = new ShapeQuadTexture(game.shaderProgramTexBill, "blood");
		blood2Shape = new ShapeQuadTexture(game.shaderProgramTexBill, "blood2");
		
		newParticlesPerFrame = 10;
		maxParticles = 100;
	}

	@Override
	public boolean update(float dt) {

		if(life > 0)
		{
			life--;
			
			if(list.size() < maxParticles)
			{
				int amountToAdd = Math.min(maxParticles - list.size(), newParticlesPerFrame);
				
				for(int i = 0; i < amountToAdd; i++)
				{
					newParticle(400);
				}
			}
		}
		
		if(life == -1 || !list.isEmpty()){

			ArrayList<Particle> destroyList = new ArrayList<Particle>();

			for(Particle p : list){
				// 	if(p.life < 200)
				//		p.actor.shape = blood2Shape;

				boolean b = p.update(dt);
				
				if(!b){
					destroyList.add(p);
				}
			}

			for(Particle p : destroyList){
				list.remove(p);
			}
		}
		else{
			return false;
		}
		
		return true;
	}

	private void newParticle(int maxLife){
		int r = (int) MathUtil.random(0, 2);

		switch(r){
		case 0:
			list.add(new Particle(game, blood1Shape, (int) MathUtil.random(50, maxLife / 2), position));
			break;
		case 1:
			list.add(new Particle(game, blood2Shape, (int) MathUtil.random(100, maxLife), position));
			break;
		}

	}

	@Override
	public void render(Camera camera) {
		for(Particle p : list){
			p.render(camera);
		}
	}

	@Override
	public void delete() {
		life = 0;
	}
	
	public int size()
	{
		return list.size();
	}

}
