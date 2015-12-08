package game.particles;

import engine.game.ShaderProgram;
import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

public class ParticleSystemBlood extends ParticleSystem {

	public ParticleSystemBlood(Vector3 position, int life) {
		super(position, life);

		particleShape = new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_billboard_instanced"), "blood.png");

		newParticlesPerFrame = 100;
		maxParticles = 10000;
		particlesLife = 4000.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		float scale = MathUtil.random(0.05f, 0.1f);

		Particle p;
		p = new Particle(MathUtil.random(500, maxLife / 2), new Vector3(position), scale);
		p.velocity = new Vector3(MathUtil.randomNegative(-0.5f, 0.5f), MathUtil.random(-0.1f, 0.1f), MathUtil.randomNegative(-0.5f, 0.5f));
		p.velocity.normalize();
		//p.velocity.scale(0.25f);

		return p;
	}
}
