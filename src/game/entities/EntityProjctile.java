package game.entities;

import org.newdawn.slick.Color;

import engine.entities.EntityActor;
import engine.entities.EntityLine;
import engine.game.states.GameStateManager;
import engine.generator.Map;
import engine.generator.Map.DoorShapeInfo;
import engine.generator.Map.ShapeInfo;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.game.states.WolfenGameState;
import game.particles.ParticleSystemImpact;

/**
 * Line that flows through the map and bounces (optionnal)
 *
 * @author Gaktan
 */
public class EntityProjctile extends EntityLine {

	protected static final float SPEED;
	static {
		SPEED = 3.5f;
	}

	protected Map map;

	protected int bounces;

	public EntityProjctile(Vector3 position, Vector3 direction, Map map) {
		super(position, new Vector3(), new Color(0xffff4c), new Color(0xffde4c));

		this.map = map;

		velocity = direction.getNormalize();
		velocity.scale(SPEED);

		bounces = 0;
	}

	@Override
	public boolean update(float dt) {

		boolean b = false;

		position.addX(velocity.getX() * (dt / 100.0f));
		position.addY(velocity.getY() * (dt / 100.0f));
		position.addZ(velocity.getZ() * (dt / 100.0f));

		positionB = position.getAdd(velocity.getNormalize().getScale(2.0f));
		// Vector3.add(position, velocity, positionB);

		if (position.getX() < 0 || position.getZ() < 0 || position.getX() > map.getSizeX()
				|| position.getZ() > map.getSizeY()) {
			return false;
		}

		if (position.getY() > 0.5f || position.getY() < -0.5f)
			return false;

		Vector3 normal = new Vector3();
		Vector3 impactPosition = new Vector3();

		int oldX = -1, oldZ = -1;

		for (float i = 0f; i < 0.25f; i += 0.025f) {
			if (position.getY() + (velocity.getY() * i) < -0.5f) {
				b = true;
				normal.setY(1f);
				impactPosition.setY(-0.5f);
				impactPosition.setX(position.getX() + (velocity.getX() * i));
				impactPosition.setZ(position.getZ() + (velocity.getZ() * i));
				break;
			}
			if (position.getY() + (velocity.getY() * i) > 0.5f) {
				b = true;
				normal.setY(-1f);
				impactPosition.setY(0f);
				impactPosition.setX(position.getX() + (velocity.getX() * i));
				impactPosition.setZ(position.getZ() + (velocity.getZ() * i));
				break;
			}

			int x = (int) (position.getX() + (velocity.getX() * i) + 0.5f);
			int z = (int) (position.getZ() + (velocity.getZ() * i) + 0.5f);

			if (x == oldX && z == oldZ)
				continue;

			oldX = x;
			oldZ = z;

			ShapeInfo info = map.get(x, z);

			if (info != null) {

				if (!info.isSolid())
					continue;

				if (info instanceof DoorShapeInfo) {
					/*
					EntityDoor door = (EntityDoor) map.getActor(x, z);
					if (door.getState() != DoorState.OPEN) {
						return false;
					}
					*/
					continue;
				}

				b = true;

				impactPosition.setX(position.getX() + (velocity.getX() * i));
				impactPosition.setY(position.getY() + (velocity.getY() * i));
				impactPosition.setZ(position.getZ() + (velocity.getZ() * i));

				normal.setX(x - impactPosition.getX());
				normal.setZ(z - impactPosition.getZ());

				if (Math.abs(normal.getX()) > Math.abs(normal.getZ())) {
					normal.setZ(0f);
					normal.setX(-normal.getX());
					impactPosition.setX((int) impactPosition.getX() + 0.5f);
				}
				else {
					normal.setX(0f);
					normal.setZ(-normal.getZ());
					impactPosition.setZ((int) impactPosition.getZ() + 0.5f);
				}

				break;
			}
		}

		if (b) {

			if (normal.length() != 0)
				normal.normalize();

			if (bounces > 0) {
				if (normal.getX() != 0) {
					velocity.setX(-velocity.getX());
				}

				else if (normal.getZ() != 0) {
					velocity.setZ(-velocity.getZ());
				}

				else if (normal.getY() != 0) {
					velocity.setY(-velocity.getY());
				}

				position.set(impactPosition);

				bounces--;
			}

			else {
				createImpact(impactPosition, normal);
				return false;
			}
		}

		return !delete;
	}

	protected void createImpact(Vector3 impactPosition, Vector3 normal) {
		normal.scale(MathUtil.random(0.99f, 1.01f));

		EntityActor e = new EntityActor(null);

		Vector3 newPos = new Vector3(impactPosition);
		Vector3 newRot = new Vector3();

		float pi2 = (float) (Math.PI / 2);

		if (normal.getX() != 0) {
			newRot.setY(pi2 * normal.getX());
			newPos.setX(impactPosition.getX() + normal.getX() * 0.01f);
		}

		else if (normal.getZ() != 0) {
			newRot.setY(pi2 - pi2 * normal.getZ());
			newPos.setZ(impactPosition.getZ() + normal.getZ() * 0.01f);
		}

		else if (normal.getY() != 0) {
			newRot.setX(pi2 * -normal.getY());
			newPos.setY((int) (impactPosition.getY() + 0.5f) + 0.5f * -normal.getY() * 0.99f);
		}

		e.position = newPos;
		e.rotation = newRot;
		e.scale.scale(0.1f);

		((WolfenGameState) GameStateManager.getCurrentGameState()).addBulletHole(e);

		ParticleSystemImpact particles = new ParticleSystemImpact(new Vector3(newPos), new Vector3(velocity),
				new Vector3(normal));

		((WolfenGameState) GameStateManager.getCurrentGameState()).add(particles);
	}
}
