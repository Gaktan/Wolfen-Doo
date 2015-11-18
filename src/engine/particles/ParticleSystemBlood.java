package engine.particles;

import org.lwjgl.util.vector.Vector3f;

import engine.game.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.MathUtil;

public class ParticleSystemBlood extends ParticleSystem {

	private ShapeQuadTexture blood1Shape;
	private ShapeQuadTexture blood2Shape;

	public ParticleSystemBlood(Vector3f position, int life) {
		super(position, life);

		blood1Shape = new ShapeQuadTexture(ShaderProgram.getProgram("texture_billboard"), "blood");
		blood2Shape = new ShapeQuadTexture(ShaderProgram.getProgram("texture_billboard"), "blood2");

		newParticlesPerFrame = 10;
		maxParticles = 100;
		particlesLife = 4000.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		int r = (int) MathUtil.random(0, 2);

		Particle p;

		switch (r) {
		case 0:
			p = new Particle(blood1Shape, MathUtil.random(500, maxLife / 2), new Vector3f(position));
			break;
		default:
			p = new Particle(blood2Shape, MathUtil.random(1000, maxLife), new Vector3f(position));
			break;
		}

		p.velocity = new Vector3f(MathUtil.randomNegative(-1, 1), 0.5f, MathUtil.randomNegative(-1, 1));
		p.scale.scale(MathUtil.random(0.05f, 0.1f));

		return p;
	}

}
