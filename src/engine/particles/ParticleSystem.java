package engine.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import engine.entities.Displayable;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

/**
 * Object used to generate particles
 *
 * @author Gaktan
 */
public abstract class ParticleSystem implements Displayable {

	protected ArrayList<Particle> list;
	protected Vector3 position;
	protected float life;

	protected int newParticlesPerFrame;
	protected int maxParticles;
	protected float particlesLife;

	protected ShapeInstancedQuadTexture particleShape;

	public ParticleSystem(Vector3 position, float life) {
		list = new ArrayList<Particle>();

		this.position = position;
		this.life = life;

		newParticlesPerFrame = 1;
		maxParticles = 1;
		particlesLife = 1.f;
	}

	@Override
	public void delete() {
		life = 0;
	}

	@Override
	public void dispose() {
		particleShape.dispose();
	}

	@Override
	public void render() {
		particleShape.preRender();
		particleShape.render(list.size());
		particleShape.postRender();
	}

	public int size() {
		return list.size();
	}

	@Override
	public boolean update(float dt) {
		if (life > 0) {
			life -= dt;

			if (list.size() < maxParticles) {
				int amountToAdd = MathUtil.min(maxParticles - list.size(), newParticlesPerFrame);

				for (int i = 0; i < amountToAdd; i++) {
					Particle p = newParticle(particlesLife);
					list.add(p);
				}
			}
		}

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

		if (list.isEmpty()) {
			return false;
		}

		setBufferData();

		return true;
	}

	protected abstract Particle newParticle(float particleLife);

	protected void setBufferData() {
		FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 16 + 1));

		for (Particle p : list) {
			p.setBufferData(fb1);
		}
		fb1.flip();
		particleShape.setData(fb1);
	}
}
