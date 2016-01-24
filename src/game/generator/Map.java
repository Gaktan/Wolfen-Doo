package game.generator;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;

import engine.Displayable;
import engine.DisplayableList;
import engine.entities.AABB;
import engine.entities.AABBRectangle;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.shapes.InstancedTexturedShape;
import engine.shapes.Orientation;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInsideOutCubeColor;
import engine.util.Matrix4;
import engine.util.Vector3;
import game.generator.MapUtil.DoorShapeInfo;
import game.generator.MapUtil.ShapeInfo;

public class Map implements Displayable {

	private int sizeX;
	private int sizeY;

	protected char map[][];
	protected HashMap<Character, ShapeInfo> shapeMap;

	protected DisplayableList<EntityActor> actorsList;
	protected EntityActor sky;
	protected boolean delete;
	protected Vector3 startingPoint;

	public Map() {
		this(20, 20);
	}

	public Map(int sizeX, int sizeY) {
		shapeMap = new HashMap<Character, ShapeInfo>();
		actorsList = new DisplayableList<EntityActor>();
		delete = false;
		startingPoint = new Vector3();

		setSize(sizeX, sizeY);
	}

	/**
	 * Builds a map on a given string
	 *
	 * @param st
	 */
	public void buildMapFromString(String st) {
		for (int i = 0; i < sizeX * sizeY; i++) {
			char c = st.charAt(i);

			if (c == ' ')
				continue;

			int x = i % sizeX;
			int y = i / sizeX;

			map[y][x] = c;

			ShapeInfo info = shapeMap.get(c);

			if (info == null)
				continue;

			if (info instanceof DoorShapeInfo) {
				DoorShapeInfo doorInfo = (DoorShapeInfo) info;
				EntityDoor door = new EntityDoor((ShapeCubeTexture) doorInfo.getShape());

				door.position = new Vector3(x, 0, y);
				if ((doorInfo.getOrientation() & (Orientation.NORTH | Orientation.SOUTH)) != 0) {
					door.scale.setX(0.1f);
				}
				else {
					door.scale.setZ(0.1f);
				}

				door.setOriginialPosition(new Vector3(x, 0, y));
				Vector3 openingPosition = new Vector3(doorInfo.getOpeningPosition());
				openingPosition.add(door.position);
				door.setOpeningPosition(openingPosition);
				door.setOpeningTime(doorInfo.getTime());

				actorsList.add(door);
			}
		} // for

		int[][] orientationArray = new int[sizeY][sizeX];

		// Finding neighbours for orientation
		for (int i = 0; i < sizeY; i++) {
			for (int j = 0; j < sizeX; j++) {

				ShapeInfo wall = shapeMap.get(map[i][j]);

				if (wall == null) {
					continue;
				}

				int faces = 0;

				if (i > 0) {
					ShapeInfo info = shapeMap.get(map[i - 1][j]);
					if (info == null || (!info.isSolid() || !info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.WEST;
						faces++;
					}
				}
				if (i < sizeY - 1) {
					ShapeInfo info = shapeMap.get(map[i + 1][j]);
					if (info == null || (!info.isSolid() || !info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.EAST;
						faces++;
					}
				}
				if (j > 0) {
					ShapeInfo info = shapeMap.get(map[i][j - 1]);
					if (info == null || (!info.isSolid() || !info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.NORTH;
						faces++;
					}
				}
				if (j < sizeX - 1) {
					ShapeInfo info = shapeMap.get(map[i][j + 1]);
					if (info == null || (!info.isSolid() || !info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.SOUTH;
						faces++;
					}
				}
				if (wall instanceof DoorShapeInfo) {
					int orientation = orientationArray[i][j];
					if ((orientation & Orientation.NORTH) != 0 && (orientation & Orientation.SOUTH) != 0) {
						orientationArray[i][j] = Orientation.WEST + Orientation.EAST;
						faces = 2;
					}
					else if ((orientation & Orientation.EAST) != 0 && (orientation & Orientation.WEST) != 0) {
						orientationArray[i][j] = Orientation.SOUTH + Orientation.NORTH;
						faces = 2;
					}
				}

				wall.amount += faces;
			}
		}

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			char key = entry.getKey();

			ShapeInfo info = entry.getValue();

			/*if (!(info.shape instanceof InstancedTexturedShape)) {
				continue;
			}*/

			int amount = info.getAmount();

			FloatBuffer fb = BufferUtils.createFloatBuffer(amount * (3 + 16 + 1));

			for (int i = 0; i < sizeY; i++) {
				for (int j = 0; j < sizeX; j++) {

					if (map[i][j] != key)
						continue;

					int orientation = orientationArray[i][j];

					if (orientation == 0) {
						continue;
					}

					float offsetValue = 0.5f;

					// Side door
					if (info instanceof DoorShapeInfo) {
						offsetValue = -0.5f;
					}

					if (info.wall) {
						if ((orientation & Orientation.NORTH) != 0) {
							storeFace(new Vector3(j - offsetValue, 0f, i), new Vector3(0f, -90f, 0f), fb);
						}
						if ((orientation & Orientation.SOUTH) != 0) {
							storeFace(new Vector3(j + offsetValue, 0f, i), new Vector3(0f, 90f, 0f), fb);
						}
						if ((orientation & Orientation.EAST) != 0) {
							storeFace(new Vector3(j, 0f, i + offsetValue), new Vector3(0f, 0f, 0f), fb);
						}
						if ((orientation & Orientation.WEST) != 0) {
							storeFace(new Vector3(j, 0f, i - offsetValue), new Vector3(0f, 180f, 0f), fb);
						}
					}
					else {
						storeFace(new Vector3(j, 0f, i), new Vector3(0f), fb);
					}

				} // for j
			} // for i

			fb.flip();

			if (info instanceof DoorShapeInfo) {
				DoorShapeInfo door = (DoorShapeInfo) info;
				door.getSideShape().setData(fb);
			}
			else {
				((InstancedTexturedShape) info.getShape()).setData(fb);
			}
		}
	}

	protected void storeFace(Vector3 position, Vector3 rotation, FloatBuffer fb) {
		Vector3 color = new Vector3(1f);
		color.store(fb);
		Matrix4 model = Matrix4.createModelMatrix(position, rotation, new Vector3(1f));
		model.store(fb);
		fb.put(-1f);
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public void dispose() {
		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			ShapeInfo info = entry.getValue();
			Shape shape = info.getShape();
			shape.dispose();
		}
	}

	public ShapeInfo get(int x, int y) {
		if (!inRange(x, 0, sizeX) || !inRange(y, 0, sizeY)) {
			return null;
		}

		return shapeMap.get(map[y][x]);
	}

	public ArrayList<EntityActor> getActors(int x, int y) {
		ArrayList<EntityActor> list = new ArrayList<EntityActor>();

		for (EntityActor actor : actorsList) {
			if (actor instanceof EntityDoor) {
				EntityDoor door = (EntityDoor) actor;

				if ((int) (door.getOriginialPosition().getX() + 0.5f) == x
						&& (int) (door.getOriginialPosition().getZ() + 0.5f) == y) {
					list.add(door);
				}
				continue;
			}

			if ((int) (actor.position.getX() + 0.5f) == x && (int) (actor.position.getZ() + 0.5f) == y) {
				list.add(actor);
			}
		}

		return list;
	}

	public EntityActor getActor(int x, int y) {
		for (EntityActor actor : actorsList) {
			if (actor instanceof EntityDoor) {
				EntityDoor door = (EntityDoor) actor;

				if ((int) (door.getOriginialPosition().getX() + 0.5f) == x
						&& (int) (door.getOriginialPosition().getZ() + 0.5f) == y) {
					return door;
				}
				continue;
			}

			if ((int) (actor.position.getX() + 0.5f) == x && (int) (actor.position.getZ() + 0.5f) == y) {
				return actor;
			}
		}

		return null;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public Vector3 getStartingPoint() {
		return startingPoint;
	}

	public boolean isSolid(int x, int y) {
		if (!inRange(x, 0, sizeX) || !inRange(y, 0, sizeY)) {
			return false;
		}

		return shapeMap.get(map[y][x]).solid;
	}

	public void newBillboard(char c, String texture, boolean solid) {
		shapeMap.put(c, MapUtil.newBillboard(texture, solid));
	}

	/**
	 * @param openingPosition
	 *            Relative position when opened
	 * @param orientation
	 *            See engine.shapes.Orientation Class
	 * @param time
	 *            Time to open
	 */
	public void newDoor(char c, String texture, String sideTexture, Vector3 openingPosition, int orientation, float time) {
		shapeMap.put(c, MapUtil.newDoor(texture, sideTexture, openingPosition, orientation, time));
	}

	public void newWall(char c, String texture, boolean solid) {
		shapeMap.put(c, MapUtil.newWall(texture, solid));
	}

	/**
	 * Casts a ray to find the nearest entity in its direction
	 *
	 * @param position
	 *            Position to cast the ray from
	 * @param ray
	 *            Direction of the ray
	 * @param distance
	 *            Maximum distance
	 * @return First Entity found. Null if nothing was found
	 */
	public Entity rayCast(Vector3 position, Vector3 ray, float distance) {
		if (ray.length() == 0)
			return null;

		ray.normalize();

		for (float i = 0f; i < distance; i += 0.2f) {
			if (position.getY() + (ray.getY() * i) > 0.5f)
				break;
			if (position.getY() + (ray.getY() * i) < -0.5f)
				break;

			int x = (int) (position.getX() + (ray.getX() * i) + 0.5f);
			int z = (int) (position.getZ() + (ray.getZ() * i) + 0.5f);

			Entity d = getActor(x, z);
			if (d != null) {
				return d;
			}
		} // for i

		return null;
	}

	@Override
	public void render() {
		if (sky != null)
			sky.render();

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			ShapeInfo info = entry.getValue();

			Shape shape = info.getShape();

			if (shape instanceof InstancedTexturedShape) {
				shape.preRender();
				((InstancedTexturedShape) shape).render(info.getAmount());
				shape.postRender();
			}
			if (info instanceof DoorShapeInfo) {
				DoorShapeInfo door = (DoorShapeInfo) info;
				InstancedTexturedShape sideShape = door.getSideShape();

				sideShape.preRender();
				sideShape.render(info.getAmount());
				sideShape.postRender();
			}
		}

		actorsList.render();
	}

	public void setSize(int sizeX, int sizeY) {
		this.setSizeX(sizeX);
		this.setSizeY(sizeY);
		map = new char[sizeY][sizeX];
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public void setSky(Vector3 upColor, Vector3 downColor) {
		ShapeInsideOutCubeColor skyShape = new ShapeInsideOutCubeColor(ShaderProgram.getProgram("color"), upColor,
				downColor);
		sky = new EntityActor(skyShape);
		sky.scale = new Vector3(sizeX - 0.5f, 1, sizeY - 0.5f);
		sky.position = new Vector3((sizeX - 1f) * 0.5f, 0, (sizeY - 1f) * 0.5f);
	}

	public void setStartingPoint(Vector3 coord) {
		startingPoint.set(coord);
		startingPoint.setZ(startingPoint.getZ());
	}

	@Override
	public boolean update(float dt) {
		actorsList.update(dt);

		return !delete;
	}

	/**
	 * Tests the collision of a given AABB to the map
	 *
	 * @param aabb
	 *            Object bounding to test for collision
	 * @param x
	 *            X position to test on the map
	 * @param z
	 *            Z position to test on the map
	 * @param entities
	 *            Check for entities as well
	 * @return A vector containing the collision resolution. (0, 0, 0) when
	 *         there is no collision
	 */
	public Vector3 testCollision(AABB aabb, int x, int z, boolean entities) {
		Vector3 result = new Vector3();

		if (!inRange(x, 0, sizeX) || !inRange(z, 0, sizeY)) {
			return result;
		}

		AABB rect = null;

		if (entities) {
			for (EntityActor e : getActors(x, z)) {
				rect = new AABBRectangle(e);
				rect.position.setY(0f);
				if (rect.collide(aabb)) {
					result.add(aabb.resolveCollision(rect));
				}

			}
		}

		ShapeInfo info = get(x, z);
		if (info != null && info.isSolid()) {
			rect = new AABBRectangle(new Vector3(x, 0, z));
			rect.position.setY(0f);
			if (rect.collide(aabb)) {
				result.add(aabb.resolveCollision(rect));
			}
		}

		return result;
	}

	/**
	 * Resolves Collisions with a given AABB with the map<br>
	 * Warning! This will modify AABB's position
	 *
	 * @param aabb
	 *            Bounding Box of the object to resolve
	 */
	public void resolveCollision(AABB aabb) {
		// TODO: fix corner collisions
		int x = (int) (aabb.position.getX() + 0.5f);
		int z = (int) (aabb.position.getZ() + 0.5f);

		Vector3 res = testCollision(aabb, x, z, true);
		aabb.position.add(res);
		res.set(testCollision(aabb, x + 1, z, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x - 1, z, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x, z + 1, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x, z - 1, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x + 1, z + 1, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x + 1, z - 1, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x - 1, z + 1, true));
		aabb.position.add(res);
		res.set(testCollision(aabb, x - 1, z - 1, true));
		aabb.position.add(res);
	}

	private boolean inRange(int n, int min, int max) {
		return n >= min && n < max;
	}

	public void addActor(EntityActor actor) {
		actorsList.add(actor);
	}
}
