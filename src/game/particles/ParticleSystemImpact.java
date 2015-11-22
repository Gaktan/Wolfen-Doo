package game.particles;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.game.ShaderProgram;
import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;

public class ParticleSystemImpact extends ParticleSystem {

	private Vector3f direction;
	private Vector3f impactNormal;

	public ParticleSystemImpact(Vector3f position, Vector3f direction, Vector3f impactNormal) {
		super(position, 1);

		this.direction = direction;
		direction.normalise();
		this.impactNormal = impactNormal;

		particleShape = new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_billboard_instanced"), "particle");

		newParticlesPerFrame = 10;
		maxParticles = 10;
		particlesLife = 450.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		Particle p = new Particle(maxLife, new Vector3f(position), 0.025f);

		p.velocity = new Vector3f(direction);

		if (impactNormal.x != 0) {
			p.velocity.x = -direction.x;

			p.velocity.y = MathUtil.random(0.1f, 1.1f);
			p.velocity.x *= MathUtil.randomNegative(-.2f, 2.f);
			p.velocity.z *= MathUtil.randomNegative(-.2f, 2.f);
		}

		else if (impactNormal.z != 0) {
			p.velocity.z = -direction.z;

			p.velocity.y = MathUtil.random(0.1f, 1.1f);
			p.velocity.x *= MathUtil.randomNegative(-.2f, 1.f);
		}

		else if (impactNormal.y != 0) {
			p.velocity.y = -direction.y;

			p.velocity.x *= MathUtil.randomNegative(-.2f, 1f);
			p.velocity.z *= MathUtil.randomNegative(-.2f, 1f);
		}

		if (impactNormal.y > 0) {
			p.position.y += 0.1f;
			p.velocity.y *= 3f;
		}

		p.velocity.normalise();
		p.velocity.scale(0.8f);

		float green = MathUtil.random(0.5f, 1.f);
		p.color = new Color(1.f, green, 0.2f);

		return p;
	}

}
