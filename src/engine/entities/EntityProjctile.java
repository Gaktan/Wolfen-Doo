package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.game.Map;
import engine.util.MathUtil;

public class EntityProjctile extends EntityLine {

	private Map map;
	private int bounces;

	public EntityProjctile(Vector3f position, Vector3f direction, Map map) {
		super(position, direction);

		this.map = map;

		this.velocity = (Vector3f) direction.normalise();
		this.velocity.scale(2.2f);

		this.bounces = -1;
	}

	@Override
	public boolean update(float dt) {

		boolean b = false;

		if(position.x < 0 || position.z < 0 || position.x > map.x || position.z > map.y) {
			return false;
		}

		Vector3f normal = new Vector3f();
		Vector3f impactPosition = new Vector3f();

		for(float i = 0f; i < 1f; i += 0.01f)
		{
			int x = (int) (position.x + (velocity.x * i) + 0.5f);
			int z = (int) (position.z + (velocity.z * i) + 0.5f);

			if(position.y + (velocity.y * i) > 0.5f)
				break;
			if(position.y + (velocity.y * i) < -0.5f)
				break;

			Displayable d = map.list.get(x, z);
			if (d instanceof Entity)
			{
				Entity e = (Entity) d;

				if(e.isSolid())
				{
					b = true;

					impactPosition.z = position.z + (velocity.z * i);
					impactPosition.y = position.y + (velocity.y * i);
					impactPosition.x = position.x + (velocity.x * i);

					normal.x = x - impactPosition.x;
					normal.z = z - impactPosition.z;

					if(Math.abs(normal.x) > Math.abs(normal.z))
					{
						normal.z = 0;
						normal.x = -normal.x;
						impactPosition.x = (int) impactPosition.x + 0.5f;
					}
					else
					{
						normal.x = 0;
						normal.z = -normal.z;
						impactPosition.z = (int) impactPosition.z + 0.5f;
					}

					break;
				}
			}
		}

		if(!b)
		{
			for(float i = 0f; i < 1f; i += 0.01f)
			{
				if(position.y + (velocity.y * i) < -0.5f)
				{
					b = true;
					normal.y = 1f;
					impactPosition.y = -0.5f;
					impactPosition.x = position.x + (velocity.x * i);
					impactPosition.z = position.z + (velocity.z * i);
					break;
				}
				if(position.y + (velocity.y * i) > 0.5f)
				{
					b = true;
					normal.y = -1f;
					impactPosition.y = 0.5f;
					impactPosition.x = position.x + (velocity.x * i);
					impactPosition.z = position.z + (velocity.z * i);
					break;
				}
			}
		}

		if(b) {

			if(normal.length() != 0)
				normal.normalise();

			if(bounces != 0)
			{
				if(normal.x != 0) 
				{
					velocity.x = -velocity.x;
					position.x = impactPosition.x;
				}

				else if(normal.z != 0) 
				{
					velocity.z = -velocity.z;
					position.z = impactPosition.z;
				}

				else if(normal.y != 0)
				{
					velocity.y = -velocity.y;
					position.y = impactPosition.y;
				}

				bounces--;
			}

			else
			{
				normal.scale(MathUtil.random(0.99f, 1.01f));

				Particle e = new Particle(map.game, map.game.shapeImpact, 400, new Vector3f());

				Vector3f newPos = new Vector3f(impactPosition);
				Vector3f newRot = new Vector3f();

				float pi2 = (float) (Math.PI / 2);

				if(normal.x != 0) 
				{
					newRot.y = pi2 * normal.x;
					newPos.x = impactPosition.x + normal.x * 0.01f;
				}

				else if(normal.z != 0) 
				{
					newRot.y = pi2 - pi2 * normal.z;
					newPos.z = impactPosition.z + normal.z * 0.01f;
				}

				else if(normal.y != 0)
				{
					newRot.x = pi2 * -normal.y;
					newPos.y = (int) (position.y + 0.5f) + 0.5f * -normal.y * 0.99f;
				}

				e.actor.position = newPos;
				e.actor.rotation = newRot;
				e.actor.scale = 0.1f;

				e.setPaused(true);

				map.game.ac.add(e);

				return false;
			}
		}

		return super.update(dt);
	}

}
