package game.particles;

import engine.particles.AnimatedParticle;
import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class ParticleSystemExplosion extends ParticleSystem {

	public ParticleSystemExplosion(Vector3 position, int life, ShapeInstancedSprite shape) {
		super(position, life);

		particleShape = shape;

		newParticlesPerFrame = 1;
		maxParticles = 100;
		particlesLife = 600.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		float scale = MathUtil.random(0.2f, 0.4f);

		AnimatedParticle p;
		p = new AnimatedParticle(maxLife * MathUtil.random(0f, 1f), new Vector3(position), scale);
		p.velocity = new Vector3(MathUtil.random(-2f, 2f), MathUtil.random(-0.3f, 0.3f), MathUtil.random(-2f, 2f));

		p.velocity.normalize();
		p.velocity.scale(MathUtil.random(0.1f, 1f));

		p.position.add(p.velocity);

		p.setPaused(true);
		// TODO: change "guybrush"
		p.setAnimation("guybrush.animation", "a_explosion");

		return p;
	}
}
