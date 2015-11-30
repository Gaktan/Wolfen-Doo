package game.particles;

import org.lwjgl.util.vector.Vector3f;

import engine.particles.AnimatedParticle;
import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedSprite;
import engine.util.MathUtil;

public class AnimatedParticleSystemTest extends ParticleSystem {

	public AnimatedParticleSystemTest(Vector3f position, int life, ShapeInstancedSprite shape) {
		super(position, life);

		particleShape = shape;

		newParticlesPerFrame = 1;
		maxParticles = 100;
		particlesLife = 4000.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		float scale = MathUtil.random(0.2f, 0.4f);

		AnimatedParticle p;
		p = new AnimatedParticle(MathUtil.random(500, maxLife / 2), new Vector3f(position), scale);
		p.velocity = new Vector3f(MathUtil.random(-1f, 1f), MathUtil.random(-0.3f, 0.3f), MathUtil.random(-1f, 1f));
		p.position.x += p.velocity.x;
		p.position.y += p.velocity.y;
		p.position.z += p.velocity.z;
		p.setPaused(true);
		p.setAnimation("test", "a_explosion");

		return p;
	}
}
