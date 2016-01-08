package game.particles;

import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

public class ParticleSystemBlood extends ParticleSystem {

	public ParticleSystemBlood(Vector3 position, float life) {
		super(position, life);

		particleShape = new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_billboard_instanced"),
				"blood.png");

		newParticlesPerFrame = 1;
		maxParticles = 10;
		particlesLife = 2000.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		float scale = MathUtil.random(0.05f, 0.1f);

		Particle p;
		p = new Particle(MathUtil.random(500f, maxLife), new Vector3(position), scale);
		p.velocity = new Vector3(MathUtil.random(-0.5f, 0.5f), MathUtil.random(0f, 0.65f), MathUtil.random(-0.5f, 0.5f));

		return p;
	}
}
