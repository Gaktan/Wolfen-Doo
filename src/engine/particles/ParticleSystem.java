package engine.particles;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.entities.Camera;

/**
 * Object used to generate particles
 * @author Gaktan
 */
public abstract class ParticleSystem implements Displayable {

	protected ArrayList<Particle> list;
	protected Vector3f position;
	protected float life;

	protected int newParticlesPerFrame;
	protected int maxParticles;
	protected float particlesLife;

	public ParticleSystem(Vector3f position, int life) {
		list = new ArrayList<Particle>();

		this.position = position;
		this.life = life;

		newParticlesPerFrame = 1;
		maxParticles = 1;
		particlesLife = 1.f;
	}

	@Override
	public boolean update(float dt) {

		if (life > 0) {
			life -= dt;

			if (list.size() < maxParticles) {
				int amountToAdd = Math.min(maxParticles - list.size(), newParticlesPerFrame);

				for (int i = 0; i < amountToAdd; i++) {
					list.add(newParticle(particlesLife));
				}
			}
		}

		if (life == -1 || !list.isEmpty()) {

			ArrayList<Particle> destroyList = new ArrayList<Particle>();

			for (Particle p : list) {
				boolean b = p.update(dt);

				if (!b) {
					destroyList.add(p);
				}
			}

			for (Particle p : destroyList) {
				list.remove(p);
			}
		}
		else {
			return false;
		}

		return true;
	}

	protected abstract Particle newParticle(float particleLife);

	@Override
	public void render(Camera camera) {
		for (Particle p : list) {
			p.render(camera);
		}
	}

	@Override
	public void delete() {
		life = 0;
	}

	public int size() {
		return list.size();
	}
}
