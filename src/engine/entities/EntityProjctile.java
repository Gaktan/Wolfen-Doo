package engine.entities;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.game.Map;
import engine.shapes.Shape;
import engine.shapes.ShapeQuad;
import engine.util.MathUtil;

public class EntityProjctile extends EntityLine {

	Map map;

	public EntityProjctile(Vector3f position, Vector3f direction, Map map) {
		super(position, direction);

		this.map = map;

		this.velocity = (Vector3f) direction.normalise();
		this.velocity.scale(0.8f);
	}

	@Override
	public boolean update(float dt) {

		boolean b = false;

		Vector3f normal = new Vector3f();

		if(position.x < 0 || position.z < 0 || position.x > map.x || position.z > map.y) {
			b = true;
		}

		if(position.y + velocity.y < -0.5f)
		{
			b = true;
			normal.y = 1f;
		}
		if(position.y + velocity.y > 0.5f)
		{
			b = true;
			normal.y = -1f;
		}


		if(!b)
		{
			int x = (int) (position.x + velocity.x + 0.5f);
			int z = (int) (position.z + velocity.z + 0.5f);
			Displayable d = map.list.get(x, z);
			if (d instanceof Entity) {
				Entity e = (Entity) d;

				if(e.isSolid()) {
					b = true;
					normal.x = x - (position.x + velocity.x);
					normal.z = z - (position.z + velocity.z);

					if(Math.abs(normal.x) > Math.abs(normal.z))
					{
						normal.z = 0;
						normal.x = -normal.x;
					}
					else
					{
						normal.x = 0;
						normal.z = -normal.z;
					}
				}
			}
		}

		if(b) {

			if(normal.length() != 0)
				normal.normalise();
			
			normal.scale(MathUtil.random(0.99f, 1.01f));
			
			Particle e = new Particle(map.game, map.game.shapeImpact, 400, new Vector3f());
			//Vector3f.add(position, direction, e.position);
			
			Vector3f newPos = new Vector3f();
			Vector3f newRot = new Vector3f();
			Vector3f.add(position, velocity, newPos);
			
			float pi2 = (float) (Math.PI / 2);

			if(normal.x != 0) 
			{
				newRot.y = pi2 * normal.x;
				newPos.x = (int) (position.x + velocity.x + 0.5f) + 0.5f * normal.x * 1.01f;
			}

			else if(normal.z != 0) 
			{
				newRot.y = pi2 - pi2 * normal.z;
				newPos.z = (int) (position.z + velocity.z + 0.5f) + 0.5f * normal.z * 1.01f;
			}

			else if(normal.y != 0)
			{
				newRot.x = pi2 * -normal.y;
				newPos.y = (int) (position.y + velocity.z + 0.5f) + 0.5f * -normal.y * 0.99f;
			}
			
			e.actor.position = newPos;
			e.actor.rotation = newRot;
			e.actor.scale = 0.1f;
			
			e.setPaused(true);

			map.game.ac.add(e);
			return false;
		}


		return super.update(dt);
	}

}
