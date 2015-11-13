package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.game.Map;
import engine.util.MathUtil;

/**
 * Line that flows through the map and bounces (optionnal)
 * @author Gaktan
 */
public class EntityProjctile extends EntityLine {

	protected Map map;
	protected int bounces;
	
	private static final float SPEED = 2.2f;

	public EntityProjctile(Vector3f position, Vector3f direction, Map map) {
		super(position, new Vector3f());

		this.map = map;

		this.velocity = (Vector3f) direction.normalise();
		this.velocity.scale(SPEED);

		this.bounces = 0;
	}

	@Override
	public boolean update(float dt) {

		boolean b = false;
		boolean drawImpact = true;
		
		boolean result = super.update(dt);
		
		Vector3f.add(position, velocity, positionB);
		
		if (position.x < 0 || position.z < 0 || position.x > map.x || position.z > map.y) {
			return false;
		}

		if (position.y > 0.5f || position.y < -0.5f)
			return false;

		Vector3f normal = new Vector3f();
		Vector3f impactPosition = new Vector3f();
		
		int oldX = -1, oldZ = -1;

		for(float i = 0f; i < 1f; i += 0.025f) {
			if (position.y + (velocity.y * i) > 0.5f)
				break;
			if (position.y + (velocity.y * i) < -0.5f)
				break;

			int x = (int) (position.x + (velocity.x * i) + 0.5f);
			int z = (int) (position.z + (velocity.z * i) + 0.5f);
			
			if(x == oldX && z == oldZ)
				continue;
			
			oldX = x;
			oldZ = z;

			Displayable d = map.list.get(x, z);
			if (d instanceof Entity) {
				Entity e = (Entity) d;

				if (!e.isSolid())
					continue;
				if (!(e instanceof EntityWall))
					continue;
				if (e instanceof EntityDoor)
					drawImpact = false;

				b = true;

				impactPosition.z = position.z + (velocity.z * i);
				impactPosition.y = position.y + (velocity.y * i);
				impactPosition.x = position.x + (velocity.x * i);

				normal.x = x - impactPosition.x;
				normal.z = z - impactPosition.z;

				if(Math.abs(normal.x) > Math.abs(normal.z)) {
					normal.z = 0;
					normal.x = -normal.x;
					impactPosition.x = (int) impactPosition.x + 0.5f;
				}
				else {
					normal.x = 0;
					normal.z = -normal.z;
					impactPosition.z = (int) impactPosition.z + 0.5f;
				}

				break;
			}
		}

		if (!b) {
			for (float i = 0f; i < 1f; i += 0.025f) {
				if (position.y + (velocity.y * i) < -0.5f) {
					b = true;
					normal.y = 1f;
					impactPosition.y = -0.5f;
					impactPosition.x = position.x + (velocity.x * i);
					impactPosition.z = position.z + (velocity.z * i);
					break;
				}
				if (position.y + (velocity.y * i) > 0.5f) {
					b = true;
					normal.y = -1f;
					impactPosition.y = 0.5f;
					impactPosition.x = position.x + (velocity.x * i);
					impactPosition.z = position.z + (velocity.z * i);
					break;
				}
			}
		}

		if (b) {

			if (normal.length() != 0)
				normal.normalise();

			if (bounces != 0) {
				if (normal.x != 0)  {
					velocity.x = -velocity.x;
				}

				else if (normal.z != 0)  {
					velocity.z = -velocity.z;
				}

				else if (normal.y != 0) {
					velocity.y = -velocity.y;
				}

				position.x = impactPosition.x;
				position.y = impactPosition.y;
				position.z = impactPosition.z;

				bounces--;
			}

			else if (drawImpact) {
				normal.scale(MathUtil.random(0.99f, 1.01f));

				Particle e = new Particle(map.game, map.game.shapeImpact, 4000, new Vector3f());

				Vector3f newPos = new Vector3f(impactPosition);
				Vector3f newRot = new Vector3f();

				float pi2 = (float) (Math.PI / 2);

				if (normal.x != 0)  {
					newRot.y = pi2 * normal.x;
					newPos.x = impactPosition.x + normal.x * 0.01f;
				}

				else if (normal.z != 0)  {
					newRot.y = pi2 - pi2 * normal.z;
					newPos.z = impactPosition.z + normal.z * 0.01f;
				}

				else if (normal.y != 0) {
					newRot.x = pi2 * -normal.y;
					newPos.y = (int) (position.y + 0.5f) + 0.5f * -normal.y * 0.99f;
				}

				e.position = newPos;
				e.rotation = newRot;

				e.setPaused(true);

				map.game.ac.add(e);

				return false;
			}
		}

		return result;
	}
}
