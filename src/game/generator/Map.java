package game.generator;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;

import engine.entities.Displayable;
import engine.entities.DisplayableList;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.shapes.InstancedTexturedShape;
import engine.shapes.Orientation;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInsideOutCubeColor;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;
import game.generator.MapUtil.ShapeInfo;
import game.generator.MapUtil.ShapeInfoDoor;

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
	 * @param mapData
	 */
	public void buildMapFromString(String mapData) {
		if (mapData.length() != (sizeX * sizeY)) {
			System.err.println("Warning! Map data doesn't match given map size. Either you didn't set the correct"
					+ " size or map data is wrong");
			return;
		}
		for (int i = 0; i < sizeX * sizeY; i++) {
			char c = mapData.charAt(i);

			if (c == ' ')
				continue;

			int x = i % sizeX;
			int y = i / sizeX;

			map[y][x] = c;

			ShapeInfo info = shapeMap.get(c);

			if (info == null)
				continue;

			if (info instanceof ShapeInfoDoor) {
				ShapeInfoDoor doorInfo = (ShapeInfoDoor) info;
				EntityDoor door = new EntityDoor((ShapeCubeTexture) doorInfo.getShape());

				door.position = new Vector3(x, 0, y);
				door.scale.set(doorInfo.getScale());

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
					ShapeInfo info = get(j, i - 1);
					if (info == null || (!info.isSolid() && info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.WEST;
						faces++;
					}
				}
				if (i < sizeY - 2) {
					ShapeInfo info = get(j, i + 1);
					if (info == null || (!info.isSolid() && info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.EAST;
						faces++;
					}
				}
				if (j > 0) {
					ShapeInfo info = get(j - 1, i);
					if (info == null || (!info.isSolid() && info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.NORTH;
						faces++;
					}
				}
				if (j < sizeX - 2) {
					ShapeInfo info = get(j + 1, i);
					if (info == null || (!info.isSolid() && info.isWall()) || info.isBillboard()) {
						orientationArray[i][j] += Orientation.SOUTH;
						faces++;
					}
				}
				if (wall instanceof ShapeInfoDoor) {
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
					if (info instanceof ShapeInfoDoor) {
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

			if (info instanceof ShapeInfoDoor) {
				ShapeInfoDoor door = (ShapeInfoDoor) info;
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
			info.dispose();
		}
		for (EntityActor a : actorsList) {
			a.dispose();
		}
		sky.shape.dispose();
		sky.dispose();
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

			if (actor.position.almostEquals(new Vector3(x, 0, y), 0.5f)) {
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

			if (actor.position.almostEquals(new Vector3(x, 0, y), 0.5f)) {
				return actor;
			}
		}

		return null;
	}

	public void newBillboard(char c, String texture, boolean solid) {
		shapeMap.put(c, MapUtil.newBillboard(texture, solid));
	}

	/**
	 * @param openingPosition
	 *            Relative position when opened
	 * @param scale
	 *            Door scale
	 * @param time
	 *            Time to open
	 */
	public void newDoor(char c, String texture, String sideTexture, Vector3 openingPosition, Vector3 scale, float time) {
		shapeMap.put(c, MapUtil.newDoor(texture, sideTexture, openingPosition, scale, time));
	}

	public void newWall(char c, String texture, boolean solid) {
		shapeMap.put(c, MapUtil.newWall(texture, solid));
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
			if (info instanceof ShapeInfoDoor) {
				ShapeInfoDoor door = (ShapeInfoDoor) info;
				InstancedTexturedShape sideShape = door.getSideShape();

				sideShape.preRender();
				sideShape.render(info.getAmount());
				sideShape.postRender();
			}
		}

		actorsList.render();
	}

	@Override
	public boolean update(float dt) {
		actorsList.update(dt);
		return !delete;
	}

	public void addActor(EntityActor actor) {
		actorsList.add(actor);
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

	public ShapeInfo get(int x, int y) {
		if (!MathUtil.inRange(x, 0, sizeX) || !MathUtil.inRange(y, 0, sizeY)) {
			return null;
		}

		return shapeMap.get(map[y][x]);
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
}
