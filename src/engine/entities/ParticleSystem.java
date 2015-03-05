package engine.entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.util.MathUtil;

public class ParticleSystem implements Displayable{
	
	public ArrayList<Particle> list;
	private GameWolfen game;
	private Vector3f position;
	
	public ParticleSystem(GameWolfen game, Vector3f position) {
		list = new ArrayList<Particle>();
		
		this.game = game;
		this.position = position;
		
		for(int i = 0; i < 200; i++){
			newGhostParticle();
		}
	}

	@Override
	public void update(float dt) {
		
		ArrayList<Particle> destroyList = new ArrayList<Particle>();
		
		for(Particle p : list){
			if(p.life < 0){
				destroyList.add(p);
				continue;
			}
			
			p.update(dt);
		}
		
		for(Particle p : destroyList){
			list.remove(p);
			newParticle(400);
		}
	}
	
	private void newParticle(int maxLife){
		list.add(new Particle(game, "pillar.png", (int) MathUtil.random(100, maxLife), position));
	}
	
	private void newGhostParticle(){
		list.add(new Particle((int) MathUtil.random(0, 50)));
	}

	@Override
	public void render(Camera camera) {
		for(Particle p : list){
			p.render(camera);
		}
	}

}
