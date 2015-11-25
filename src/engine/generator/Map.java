package engine.generator;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableList;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.game.GameWolfen;
import engine.game.ShaderProgram;
import engine.shapes.Instantiable;
import engine.shapes.Orientation;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInstancedCubeTexture;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MatrixUtil;

public class Map implements Displayable {

	private int sizeX;
	private int sizeY;

	protected char map[][];

	protected EntityActor sky;

	protected HashMap<Character, ShapeInfo> shapeMap;

	protected DisplayableList actorsList;

	protected boolean delete;

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

	public class DoorShapeInfo extends ShapeInfo {

		protected Vector3f openingPosition;
		protected int orientation;
		protected float time;

		public DoorShapeInfo(Shape shape, boolean solid, Vector3f openingPosition, 
				int orientation, float time) {

			super(shape, solid);
			this.openingPosition = openingPosition;
			this.orientation = orientation;
			this.time = time;
		}		
	}

	public Map() {
		this(20, 20);
	}

	public Map(int sizeX, int sizeY) {
		shapeMap = new HashMap<Character, ShapeInfo>();

		actorsList = new DisplayableList();

		delete = false;

		setSize(sizeX, sizeY);
	}

	public void newWall(char c, String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedCubeTexture(
				ShaderProgram.getProgram("texture_instanced"), texture), solid);

		shapeMap.put(c, info);
	}

	public void newBillboard(char c, String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_billboard_instanced"), texture), solid);

		shapeMap.put(c, info);
	}

	/**
	 * @param openingPosition Relative position when opened
	 * @param orientation See engine.shapes.Orientation Class
	 * @param time Time to open
	 */
	public void newDoor(char c, String texture, Vector3f openingPosition, int orientation, float time) {

		ShapeCubeTexture shape = new ShapeCubeTexture(ShaderProgram.getProgram("texture"), texture);		

		DoorShapeInfo info = new DoorShapeInfo(shape, true, openingPosition, orientation, time);

		shapeMap.put(c, info);
	}

	/**
	 * Builds a map on a given string (might get removed)
	 * @param st
	 */
	public void buildMapFromString(String st) {

		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				char c = st.charAt((i * sizeY) + j);

				if (c == ' ')
					continue;

				int x = sizeX - 1 - i;
				int y = j;

				map[x][y] = c;

				ShapeInfo info = shapeMap.get(c);

				if (info == null)
					continue;

				if (info.shape instanceof Instantiable) {
					info.amount++;
				}

				if (info instanceof DoorShapeInfo) {

					DoorShapeInfo doorInfo = (DoorShapeInfo) info;
					EntityDoor door = new EntityDoor((ShapeCubeTexture) info.shape);

					door.position = new Vector3f(x, 0, y);
					if ((doorInfo.orientation & (Orientation.NORTH | Orientation.SOUTH)) != 0) {
						door.scale.x = 0.1f;
					}
					else {
						door.scale.z = 0.1f;
					}

					door.setOriginialPosition(new Vector3f(x, 0, y));
					Vector3f openingPosition = new Vector3f(doorInfo.openingPosition);
					Vector3f.add(door.position, openingPosition, openingPosition);
					door.setOpeningPosition(openingPosition);
					door.setOpeningTime(doorInfo.time);

					actorsList.add(door);
				}

			} // for j
		} // for i

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			char key = entry.getKey();

			ShapeInfo info = entry.getValue();

			if (!(info.shape instanceof Instantiable)) {
				continue;
			}

			int amount = info.amount;

			FloatBuffer fb = BufferUtils.createFloatBuffer(amount * (3 + 16));

			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {

					if (map[i][j] != key)
						continue;

					float[] color = new float[3];

					color[0] = 1.0f;
					color[1] = 1.0f;
					color[2] = 1.0f;
					fb.put(color);

					Matrix4f model = MatrixUtil.createIdentityMatrix();
					model.m30 = i;
					model.m31 = 0f;
					model.m32 = j;
					model.store(fb);

				} // for j
			} // for i

			fb.flip();

			((Instantiable) info.shape).setData(fb);
		}

	}

	@Override
	public boolean update(float dt) {

		int x = (int) (GameWolfen.getInstance().camera.position.x + 0.5f);
		int z = (int) (GameWolfen.getInstance().camera.position.z + 0.5f);

		for (int i = x - 1; i < x + 2; i++) {
			for (int j = z - 1; j < z + 2; j++) {

				EntityActor e = getActor(i, j);

				if (e == null) {
					ShapeInfo info = get(i, j);

					if (info == null)
						continue;

					if (!info.isSolid())
						continue;

					e = new EntityActor(null);
					e.position.set(i, 0, j);
				}

				if (GameWolfen.getInstance().camera.collide(e)){
					GameWolfen.getInstance().camera.collisionHandler(e);
				}
			}
		}

		actorsList.update(dt);

		return !delete;
	}

	@Override
	public void render() {
		sky.render();

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			ShapeInfo info = entry.getValue();

			if (info.shape instanceof Instantiable) {
				info.shape.preRender();
				((Instantiable) info.shape).render(info.amount);
				info.shape.postRender();
			}
		}

		actorsList.render();
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public int size() {

		int total = 0;

		for (Entry<Character, ShapeInfo> entry : shapeMap.entrySet()) {
			ShapeInfo info = entry.getValue();

			total += info.amount;
		}
		
		total += actorsList.size();

		return total;
	}

	public void setSky(EntityActor sky) {
		sky.scale = new Vector3f(sizeX - 0.5f, 1, sizeY - 0.5f);
		sky.position = new Vector3f((sizeX - 1f) / 2, 0, (sizeY - 1f) / 2f);

		this.sky = sky;
	}

	public void setSize(int sizeX, int sizeY) {
		this.setSizeX(sizeX);
		this.setSizeY(sizeY);
		map = new char[sizeX][sizeY];
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

					if ((int) (door.getOriginialPosition().x + 0.5f) == x 
							&& (int) (door.getOriginialPosition().z + 0.5f) == y) {
						return door;
					}
					continue;
				}

				if ((int) (actor.position.x + 0.5f) == x && (int) (actor.position.z + 0.5f) == y) {
					return actor;
				}
			}
		}

		return null;
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

			Entity d = getActor(x, z);
			if (d != null) {
				return d;
			}
		} // for i

		return null;
	}

	public boolean isSolid(int x, int y) {
		if (!inRange(x, 0, sizeX) || !inRange(y, 0, sizeY)) {
			return false;
		}

		return shapeMap.get(map[x][y]).solid;
	}

	private boolean inRange(int n, int min, int max) {
		return n >= min && n < max;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
}
