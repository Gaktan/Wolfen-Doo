package engine.generator;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;

import engine.Displayable;
import engine.DisplayableList;
import engine.entities.AABBRectangle;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.game.states.GameStateManager;
import engine.shapes.InstancedTexturedShape;
import engine.shapes.Orientation;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.Matrix4;
import engine.util.Vector3;
import game.game.states.WolfenGameState;

public class Map implements Displayable {

	public class DoorShapeInfo extends ShapeInfo {

		protected InstancedTexturedShape sideShape;
		protected Vector3 openingPosition;
		protected int orientation;
		protected float time;

		public DoorShapeInfo(Shape shape, InstancedTexturedShape sideShape, boolean solid, Vector3 openingPosition,
				int orientation, float time) {
			super(shape, solid);
			this.sideShape = sideShape;
			this.openingPosition = openingPosition;
			this.orientation = orientation;
			this.time = time;
		}
	}

	public class ShapeInfo {

		protected Shape shape;
		protected int amount;
		protected boolean solid;

		public ShapeInfo(Shape shape, boolean solid) {
			this.shape = shape;
			this.solid = solid;
			amount = 0;
		}

		public boolean isSolid() {
			return solid;
		}
	}

	private int sizeX;
	private int sizeY;

	protected char map[][];
	protected HashMap<Character, ShapeInfo> shapeMap;

	protected DisplayableList actorsList;
	protected EntityActor sky;
	protected boolean delete;
	protected Vector3 startingPoint;

	public Map() {
		this(20, 20);
	}

	public Map(int sizeX, int sizeY) {
		shapeMap = new HashMap<Character, ShapeInfo>();
		actorsList = new DisplayableList();
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

		setSize(sizeX, sizeY);

		for (int i = 0; i < sizeX * sizeY; i++) {
			char c = st.charAt(i);

			if (c == ' ')
				continue;

			int y = (i % sizeY);
			int x = sizeX - (i / sizeY) - 1;

			map[x][y] = c;

			ShapeInfo info = shapeMap.get(c);

			if (info == null)
				continue;

			if (info instanceof DoorShapeInfo) {

				DoorShapeInfo doorInfo = (DoorShapeInfo) info;
				EntityDoor door = new EntityDoor((ShapeCubeTexture) info.shape);

				door.position = new Vector3(x, 0, y);
				if ((doorInfo.orientation & (Orientation.NORTH | Orientation.SOUTH)) != 0) {
					door.scale.setX(0.1f);
				}
				else {
					door.scale.setZ(0.1f);
				}

				door.setOriginialPosition(new Vector3(x, 0, y));
				Vector3 openingPosition = new Vector3(doorInfo.openingPosition);
				openingPosition.add(door.position);
				door.setOpeningPosition(openingPosition);
				door.setOpeningTime(doorInfo.time);

				actorsList.add(door);
			}
		} // for

		int[][] orientationArray = new int[sizeX][sizeY];

		// Finding neighbours for orientation
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {

				ShapeInfo wall = shapeMap.get(map[i][j]);

				if (wall == null) {
					continue;
				}

				int amount = 0;

				if (i > 0) {
					ShapeInfo info = shapeMap.get(map[i - 1][j]);
					if (info == null || !info.isSolid()) {
						orientationArray[i][j] += Orientation.WEST;
						amount++;
					}
				}
				if (i < sizeX - 1) {
					ShapeInfo info = shapeMap.get(map[i + 1][j]);
					if (info == null || !info.isSolid()) {
						orientationArray[i][j] += Orientation.EAST;
						amount++;
					}
				}
				if (j > 0) {
					ShapeInfo info = shapeMap.get(map[i][j - 1]);
					if (info == null || !info.isSolid()) {
						orientationArray[i][j] += Orientation.NORTH;
						amount++;
					}
				}
				if (j < sizeY - 1) {
					ShapeInfo info = shapeMap.get(map[i][j + 1]);
					if (info == null || !info.isSolid()) {
						orientationArray[i][j] += Orientation.SOUTH;
						amount++;
					}
				}

				if (wall instanceof DoorShapeInfo) {
					int orientation = orientationArray[i][j];
					if ((orientation & Orientation.NORTH) != 0 && (orientation & Orientation.SOUTH) != 0) {
						orientationArray[i][j] = Orientation.WEST + Orientation.EAST;
						amount = 2;
					}
					else if ((orientation & Orientation.EAST) != 0 && (orientation & Orientation.WEST) != 0) {
						orientationArray[i][j] = Orientation.SOUTH + Orientation.NORTH;
						amount = 2;
					}
				}

				wall.amount += amount;
			}
		}

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			char key = entry.getKey();

			ShapeInfo info = entry.getValue();

			/*if (!(info.shape instanceof InstancedTexturedShape)) {
				continue;
			}*/

			int amount = info.amount;

			FloatBuffer fb = BufferUtils.createFloatBuffer(amount * (3 + 16 + 1));

			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {

					if (map[i][j] != key)
						continue;

					int orientation = orientationArray[i][j];

					if (orientation == 0) {
						continue;
					}

					float pi = 3.1416f;

					float offsetValue = 0.5f;

					// Side door
					if (info instanceof DoorShapeInfo) {
						offsetValue = -0.5f;
					}

					if ((orientation & Orientation.NORTH) != 0) {
						storeFace(new Vector3(i, 0f, j - offsetValue), new Vector3(0f, pi, 0f), fb);
					}
					if ((orientation & Orientation.SOUTH) != 0) {
						storeFace(new Vector3(i, 0f, j + offsetValue), new Vector3(0f), fb);
					}
					if ((orientation & Orientation.EAST) != 0) {
						storeFace(new Vector3(i + offsetValue, 0f, j), new Vector3(0f, pi * 0.5f, 0f), fb);
					}
					if ((orientation & Orientation.WEST) != 0) {
						storeFace(new Vector3(i - offsetValue, 0f, j), new Vector3(0f, pi * -0.5f, 0f), fb);
					}

				} // for j
			} // for i

			fb.flip();

			if (info instanceof DoorShapeInfo) {
				DoorShapeInfo door = (DoorShapeInfo) info;
				door.sideShape.setData(fb);
			}
			else {
				((InstancedTexturedShape) info.shape).setData(fb);
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

	public ShapeInfo get(int x, int y) {
		if (!inRange(x, 0, sizeX) || !inRange(y, 0, sizeY)) {
			return null;
		}

		return shapeMap.get(map[x][y]);
	}

	public EntityActor getActor(int x, int y) {
		for (Displayable d : actorsList) {

			if (d instanceof EntityActor) {
				EntityActor actor = (EntityActor) d;

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

		return shapeMap.get(map[x][y]).solid;
	}

	public void newBillboard(char c, String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_billboard_instanced"), texture), solid);

		shapeMap.put(c, info);
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
		ShapeCubeTexture shape = new ShapeCubeTexture(ShaderProgram.getProgram("texture"), texture);

		ShapeInstancedQuadTexture sideShape = new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_instanced"), sideTexture);

		DoorShapeInfo info = new DoorShapeInfo(shape, sideShape, true, openingPosition, orientation, time);

		shapeMap.put(c, info);
	}

	public void newWall(char c, String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_instanced"),
				texture), solid);

		shapeMap.put(c, info);
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

		for (float i = 0f; i < distance; i += 0.04f) {
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

			if (info.shape instanceof InstancedTexturedShape) {
				info.shape.preRender();
				((InstancedTexturedShape) info.shape).render(info.amount);
				info.shape.postRender();
			}
			if (info instanceof DoorShapeInfo) {
				DoorShapeInfo door = (DoorShapeInfo) info;
				door.sideShape.preRender();
				door.sideShape.render(info.amount);
				door.sideShape.postRender();
			}
		}

		actorsList.render();
	}

	public void setSize(int sizeX, int sizeY) {
		this.setSizeX(sizeX);
		this.setSizeY(sizeY);
		map = new char[sizeX][sizeY];
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

	public void setSky(EntityActor sky) {
		sky.scale = new Vector3(sizeX - 0.5f, 1, sizeY - 0.5f);
		sky.position = new Vector3((sizeX - 1f) / 2, 0, (sizeY - 1f) / 2f);

		this.sky = sky;
	}

	public void setStartingPoint(Vector3 coord) {
		startingPoint.set(coord);
	}

	@Override
	public boolean update(float dt) {

		int x = (int) (((WolfenGameState) GameStateManager.getCurrentGameState()).getPlayer().position.getX() + 0.5f);
		int z = (int) (((WolfenGameState) GameStateManager.getCurrentGameState()).getPlayer().position.getZ() + 0.5f);

		AABBRectangle playerAABB = ((WolfenGameState) GameStateManager.getCurrentGameState()).getPlayer().collisionRectangle;

		for (int i = x - 1; i < x + 2; i++) {
			for (int j = z - 1; j < z + 2; j++) {

				AABBRectangle rect;
				EntityActor e = getActor(i, j);

				if (e == null) {
					ShapeInfo info = get(i, j);

					if (info == null)
						continue;

					if (!info.isSolid())
						continue;

					rect = new AABBRectangle(new Vector3(i, 0, j));
				}
				else {
					rect = new AABBRectangle(e);
				}

				if (playerAABB.collide(rect)) {
					Vector3 resolution = playerAABB.resolveCollision(rect);
					((WolfenGameState) GameStateManager.getCurrentGameState()).getPlayer().position.add(resolution);
				}
			}
		}

		actorsList.update(dt);

		return !delete;
	}

	private boolean inRange(int n, int min, int max) {
		return n >= min && n < max;
	}
}
