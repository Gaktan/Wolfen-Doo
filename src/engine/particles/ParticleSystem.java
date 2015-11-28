package engine.particles;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MatrixUtil;

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

	protected ShapeInstancedQuadTexture particleShape;

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

		FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 16 + 1));

		for (Particle p : list) {
			float[] array = new float[3];
			array[0] = p.color.r;
			array[1] = p.color.g;
			array[2] = p.color.b;
			fb1.put(array);

			Matrix4f model = MatrixUtil.createIdentityMatrix();
			model.m30 = p.position.x;
			model.m31 = p.position.y;
			model.m32 = p.position.z;
			model = model.scale(new Vector3f(p.scale, p.scale, p.scale));
			model.store(fb1);
			
			fb1.put(-1f);
		}

		fb1.flip();

		particleShape.setData(fb1);

		return true;
	}

	@Override
	public void render() {
		particleShape.preRender();
		particleShape.render(list.size());
		particleShape.postRender();
	}

	protected abstract Particle newParticle(float particleLife);

	@Override
	public void delete() {
		life = 0;
	}

	public int size() {
		return list.size();
	}
}
