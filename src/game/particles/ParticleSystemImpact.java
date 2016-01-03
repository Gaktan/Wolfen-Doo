package game.particles;

import org.newdawn.slick.Color;

import engine.particles.Particle;
import engine.particles.ParticleSystem;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

public class ParticleSystemImpact extends ParticleSystem {

	private Vector3 direction;
	private Vector3 impactNormal;

	public ParticleSystemImpact(Vector3 position, Vector3 direction, Vector3 impactNormal) {
		super(position, 1);

		this.direction = direction;
		direction.normalize();
		this.impactNormal = impactNormal;

		particleShape = new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_billboard_instanced"),
				"particle.png");

		newParticlesPerFrame = 10;
		maxParticles = 10;
		particlesLife = 850.f;
	}

	@Override
	protected Particle newParticle(float maxLife) {
		Particle p = new Particle(MathUtil.random(maxLife * 0.5f, maxLife), new Vector3(position), 0.025f);

		p.velocity = new Vector3(direction);

		if (impactNormal.getX() != 0) {
			p.velocity.setX(-direction.getX());

			p.velocity.setY(MathUtil.random(0.1f, 1.1f));
			// p.velocity.setX(p.velocity.getX() * MathUtil.randomNegative(-.2f,
			// 2.f));
			p.velocity.setZ(p.velocity.getZ() * MathUtil.random(-.2f, 1.f));
		}

		else if (impactNormal.getZ() != 0) {
			p.velocity.setZ(-direction.getZ());

			p.velocity.setY(MathUtil.random(0.1f, 1.1f));
			p.velocity.setX(p.velocity.getX() * MathUtil.random(-.2f, 1.f));
		}

		else if (impactNormal.getY() != 0) {
			p.velocity.setY(-direction.getY());

			p.velocity.setX(p.velocity.getX() * MathUtil.random(-.2f, 1.f));
			p.velocity.setZ(p.velocity.getZ() * MathUtil.random(-.2f, 1.f));
		}

		if (impactNormal.getY() > 0) {
			p.position.addY(0.1f);
			p.velocity.scale(1f, 3f, 1f);
			// p.velocity.y *= 3f;
		}

		p.velocity.normalize();
		p.velocity.scale(0.8f);

		float green = MathUtil.random(0.5f, 1.f);
		p.color = new Color(1.f, green, 0.2f);
		p.color.scale(1.5f);

		return p;
	}
}
