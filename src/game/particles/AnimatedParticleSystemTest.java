package game.particles;

import engine.particles.AnimatedParticle;
import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class AnimatedParticleSystemTest extends ParticleSystem {

	public AnimatedParticleSystemTest(Vector3 position, int life, ShapeInstancedSprite shape) {
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
		p = new AnimatedParticle(MathUtil.random(500, maxLife / 2), new Vector3(position), scale);
		p.velocity = new Vector3(MathUtil.random(-1f, 1f), MathUtil.random(-0.3f, 0.3f), MathUtil.random(-1f, 1f));

		//p.position.x += p.velocity.x;
		//p.position.y += p.velocity.y;
		//p.position.z += p.velocity.z;
		p.position.add(p.velocity);

		p.setPaused(true);
		p.setAnimation("test", "a_explosion");

		return p;
	}
}
