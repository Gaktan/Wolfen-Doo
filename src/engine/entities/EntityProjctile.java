package engine.entities;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.Displayable;
import engine.game.GameWolfen;
import engine.game.Map;
import engine.shapes.Shape;
import engine.shapes.ShapeQuad;

public class EntityProjctile extends EntityLine {

	Map map;

	public EntityProjctile(Vector3f position, Vector3f direction, Map map) {
		super(position, direction);

		this.map = map;

		this.velocity = (Vector3f) direction.normalise();
		this.velocity.scale(0.1f);
	}

	@Override
	public boolean update(float dt) {

		boolean b = false;

		Vector3f normal = new Vector3f();

		if(position.x < 0 || position.z < 0 || position.x > map.x || position.z > map.y) {
			b = true;
		}

		if(position.y + direction.y < -0.5f)
		{
			b = true;
			normal.y = 1f;
		}
		if(position.y + direction.y > 0.5f)
		{
			b = true;
			normal.y = -1f;
		}


		if(!b)
		{
			int x = (int) (position.x + direction.x + 0.5f);
			int z = (int) (position.z + direction.z + 0.5f);
			Displayable d = map.list.get(x, z);
			if (d instanceof Entity) {
				Entity e = (Entity) d;

				if(e.isSolid()) {
					b = true;
					normal.x = x - (position.x + direction.x);
					normal.z = z - (position.z + direction.z);

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
			normal.normalise();
			EntityActor e = new EntityActor(map.game.shapeImpact);
			//Vector3f.add(position, direction, e.position);

			e.position.x = position.x + (direction.x * 0.99f);
			e.position.y = position.y + (direction.y * 0.99f);
			e.position.z = position.z + (direction.z * 0.99f);
			
			float pi2 = (float) (Math.PI / 2);


			if(normal.x != 0) 
			{
				e.rotation.y = pi2 * normal.x;
			}

			else if(normal.z != 0) 
			{
				e.rotation.y = pi2 - pi2 * normal.z;
			}

			else if(normal.y != 0)
			{
				e.rotation.x = pi2 * -normal.y;
			}
			e.scale = 0.1f;

			map.game.ac.add(e);
			return false;
		}


		return super.update(dt);
	}

}
