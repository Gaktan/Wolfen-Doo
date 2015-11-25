package engine.generator;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableArray2D;
import engine.animations.AnimatedActor;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.entities.EntityWall;
import engine.game.GameWolfen;
import engine.game.ShaderProgram;
import engine.shapes.*;

/**
 * Used to render a 2D grid map
 */
public class OldMap implements Displayable{

	public EntityActor sky;
	public DisplayableArray2D list;
	public int x, y;
	private boolean delete;

	public GameWolfen game;

	public OldMap(GameWolfen game) {
		this(game, 20, 20, null);
	}

	public OldMap(GameWolfen game, int x, int y, EntityActor sky) {
		list = new DisplayableArray2D(x, y);

		this.game = game;

		if (sky == null) {

			Vector3f downColor = new Vector3f(0.75f, 0.75f, 0.75f);
			Vector3f upColor = new Vector3f(0.35f, 0.75f, 0.9f);

			ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(ShaderProgram.getProgram("color"), upColor, downColor);

			sky = new EntityActor(skyShape);
		}

		this.x = x;
		this.y = y;
	}

	/**
	 * Sets map size
	 * @param x Width
	 * @param y Height
	 */
	public void setSize(int x, int y) {
		this.x = x;
		this.y = y;

		list = new DisplayableArray2D(x, y);
	}

	@Override
	public boolean update(float dt) {
		int x = (int) (game.camera.position.x + 0.5f);
		int z = (int) (game.camera.position.z + 0.5f);

		for (int i = x - 1; i < x + 2; i++) {
			for (int j = z - 1; j < z + 2; j++) {

				Displayable d = list.get(i, j);

				if (!(d instanceof Entity))
					continue;

				Entity e = (Entity) d;

				if (!e.isSolid())
					continue;

				if (game.camera.collide(e)){
					game.camera.collisionHandler(e);
				}
			}
		}

		boolean b = list.update(dt);

		if (!b)
			return false;

		return !delete;
	}

	@Override
	public void render() {
		sky.render();
		list.render();
	}

	/**
	 * Builds a map on a given string (might get removed)
	 * @param st
	 */
	public void buildMapFromString(String st) {

		list = new DisplayableArray2D(x, y);

		ShapeCubeTexture shapeCube = new ShapeCubeTexture(ShaderProgram.getProgram("texture"), "wall");

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				char c = st.charAt((i * y) + j);
				if (c == 'O') {
					newWall(x - i - 1, j, shapeCube, true);
				}
			} // for j
		} // for i

		setOrientation();
		setSky();
	}

	public void newActor(float x, float y, Shape shape, boolean solid) {
		x = this.x - x - 1;
		EntityActor e = new EntityActor(shape);
		e.position = new Vector3f(x, 0, y);
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}

	public void newAnimatedActor(float x, float y, Shape shape, boolean solid) {
		x = this.x - x - 1;
		AnimatedActor e = new AnimatedActor(shape, "test", "a_running_front");
		e.position.x = x;
		e.position.z = y;
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}

	public void newWall(float x, float y, ShapeCubeTexture shape, boolean solid) {
		x = this.x - x - 1;
		EntityWall e = new EntityWall(shape);
		e.position = new Vector3f(x, 0, y);
		e.setSolid(solid);

		list.add(e, (int)x, (int)y);
	}

	/**
	 * @param openingPosition relative position when opened
	 * @param orientation see Orientation Class
	 * @param speed opening speed
	 */
	public void newDoor(float x, float y, ShapeCubeTexture shape, Vector3f openingPosition, int orientation, float time) {
		x = this.x - x - 1;

		EntityDoor e = new EntityDoor(shape);

		e.position = new Vector3f(x, 0, y);
		if ((orientation & (Orientation.NORTH | Orientation.SOUTH)) != 0) {
			e.scale.x = 0.1f;
		}
		else {
			e.scale.z = 0.1f;
		}

		e.setOriginialPosition(new Vector3f(x, 0, y));
		Vector3f.add(e.position, openingPosition, openingPosition);
		e.setOpeningPosition(openingPosition);
		e.setOpeningTime(time);

		list.add(e, (int)x, (int)y);
	}

	/**
	 * Sets all walls orientation so only visible faces are rendered
	 */
	public void setOrientation() {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {

				EntityActor actor = (EntityActor) list.get(i, j);

				if (actor == null){
					continue;
				}

				if (!(actor instanceof EntityWall))
					continue;

				int orientation = 0;

				if (i > 0)
					orientation += processOrientation(actor, i-1, 	j, 		Orientation.WEST);

				if (j > 0)
					orientation += processOrientation(actor, i, 	j-1,	Orientation.SOUTH);

				if (i < x - 1)
					orientation += processOrientation(actor, i+1, 	j,		Orientation.EAST);

				if (j < y - 1)
					orientation += processOrientation(actor, i, 	j+1,	Orientation.NORTH);

				if (actor instanceof EntityWall) {
					EntityWall new_name = (EntityWall) actor;
					new_name.setOrientation(orientation);
				}
			} // for j
		} // for i
	}

	private int processOrientation(EntityActor actor, int i, int j, int o) {
		if (actor instanceof EntityDoor)
			return o;

		Displayable d = list.get(i, j);

		if (!(d instanceof EntityWall)) 
			return o;

		EntityWall ew = (EntityWall) d;

		if (!actor.isSolid() && !ew.isSolid())
			return 0;

		if (ew instanceof EntityDoor) {
			return o;
		}

		if (!ew.isSolid())
			return o;

		return 0;
	}

	/**
	 * Resets the sky after map's size had been changed
	 */
	public void setSky() {
		sky.scale = new Vector3f(x - 0.5f, 1, y - 0.5f);
		sky.position = new Vector3f((x - 1f) / 2, 0, (y - 1f) / 2f);
	}

	@Override
	public void delete() {
		delete = true;
	}

	public int size() {
		return list.size() + 1;
	}

	/**
	 * Casts a ray to find the nearest entity in its direction
	 * @param position Position to cast the ray from
	 * @param ray Direction of the ray
	 * @param distance Maximum distance
	 * @return First Entity found. Null if nothing was found
	 */
	public Entity rayCast(Vector3f position, Vector3f ray, float distance) {
		if (ray.length() == 0)
			return null;

		ray.normalise();

		for (float i = 0f; i < distance; i += 0.04f) {
			if (position.y + (ray.y * i) > 0.5f)
				break;
			if (position.y + (ray.y * i) < -0.5f)
				break;

			int x = (int) (position.x + (ray.x * i) + 0.5f);
			int z = (int) (position.z + (ray.z * i) + 0.5f);

			Displayable d = list.get(x, z);
			if (d instanceof Entity) {
				Entity e = (Entity) d;

				return e;
			}
		} // for i

		return null;
	}
}
