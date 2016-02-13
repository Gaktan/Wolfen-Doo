package game.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import engine.entities.AABB;
import engine.entities.AABBRectangle;
import engine.entities.Entity;
import engine.entities.EntityActor;
import engine.shapes.InstancedTexturedShape;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

public class MapUtil {

	public static class ShapeInfoDoor extends ShapeInfo {

		protected InstancedTexturedShape sideShape;
		protected Vector3 openingPosition;
		protected Vector3 scale;
		protected float time;

		public ShapeInfoDoor(Shape shape, InstancedTexturedShape sideShape, Vector3 openingPosition, Vector3 scale,
				float time) {
			super(shape, true, true);
			this.sideShape = sideShape;
			this.openingPosition = openingPosition;
			this.scale = scale;
			this.time = time;
		}

		@Override
		public void dispose() {
			sideShape.dispose();
			super.dispose();
		}

		public InstancedTexturedShape getSideShape() {
			return sideShape;
		}

		protected Vector3 getOpeningPosition() {
			return openingPosition;
		}

		protected Vector3 getScale() {
			return scale;
		}

		protected float getTime() {
			return time;
		}
	}

	public static class ShapeInfo {

		protected Shape shape;
		protected int amount;
		protected boolean solid;
		protected boolean wall;

		public ShapeInfo(Shape shape, boolean solid, boolean wall) {
			this.shape = shape;
			this.solid = solid;
			this.wall = wall;
			amount = 0;
		}

		public void dispose() {
			shape.dispose();
		}

		public boolean isBillboard() {
			return !wall;
		}

		public boolean isSolid() {
			return solid;
		}

		public boolean isWall() {
			return wall;
		}

		public boolean isDoor() {
			return this instanceof ShapeInfoDoor;
		}

		public int getAmount() {
			return amount;
		}

		public Shape getShape() {
			return shape;
		}
	}

	public static class Pair implements Comparable<Pair> {
		protected int x, y;
		protected float f_score;
		protected float g_score;

		protected Pair parent;

		public Pair(int x, int y) {
			this.x = x;
			this.y = y;

			g_score = Float.POSITIVE_INFINITY;
			f_score = Float.POSITIVE_INFINITY;
		}

		public float getDistance(Pair goal) {
			return MathUtil.abs(x - goal.x) * MathUtil.abs(y - goal.y);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Pair) {
				Pair pair = (Pair) o;
				return (x == pair.x) && (y == pair.y);
			}
			return false;
		}

		@Override
		public int compareTo(Pair arg0) {
			return Float.compare(f_score, arg0.f_score);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setF(float f) {
			f_score = f;
		}

		public float getF() {
			return f_score;
		}

		public void setG(float g) {
			g_score = g;
		}

		public float getG() {
			return g_score;
		}

		public void setParent(Pair parent) {
			this.parent = parent;
		}

		public Vector3 toVector3() {
			return new Vector3(x, 0f, y);
		}

		public List<Pair> traceBack() {
			List<Pair> list = new ArrayList<Pair>();
			Pair current = this;
			while (current != null) {
				list.add(current);
				current = current.parent;
			}
			Collections.reverse(list);
			return list;
		}
	}

	/**
	 * Pathfind from a goal to a start
	 *
	 * @param map
	 *            Map where to perform the pathfinding
	 * @param start
	 *            Position of the start
	 * @param goal
	 *            Position of the goal
	 * @param canOpenDoors
	 *            Tells if the path can go through doors or not
	 * @return A list containing the points to go through from start to end
	 */
	public static List<Pair> createPath(Map map, Vector3 start, Vector3 goal, boolean canOpenDoors) {
		int x = (int) (start.getX() + 0.5f);
		int z = (int) (start.getZ() + 0.5f);
		Pair p_start = new Pair(x, z);
		p_start.setF(0f);

		x = (int) (goal.getX() + 0.5f);
		z = (int) (goal.getZ() + 0.5f);
		Pair p_goal = new Pair(x, z);

		List<Pair> closedSet = new ArrayList<Pair>();
		List<Pair> openSet = new ArrayList<Pair>();
		openSet.add(p_start);

		while (!openSet.isEmpty()) {
			Collections.sort(openSet);
			Pair current = openSet.get(0);

			if (current.equals(p_goal)) {
				return current.traceBack();
			}

			openSet.remove(current);
			closedSet.add(current);

			for (int i = 0; i < 4; i++) {
				int x2 = current.getX();
				int y2 = current.getY();

				if (i == 0) {
					x2--;
				}
				else if (i == 1) {
					x2++;
				}
				else if (i == 2) {
					y2++;
				}
				else {
					y2--;
				}

				if (x2 < 0 || x2 > map.getSizeX()) {
					continue;
				}
				if (y2 < 0 || y2 > map.getSizeY()) {
					continue;
				}

				ShapeInfo info = map.get(x2, y2);
				if (info != null && info.isSolid() && !(info instanceof ShapeInfoDoor && canOpenDoors)) {
					continue;
				}

				Pair neighbour = new Pair(x2, y2);

				if (closedSet.contains(neighbour)) {
					continue;
				}

				float tentative_g_score = current.getG() + current.getDistance(neighbour);

				int index = openSet.indexOf(neighbour);

				if (index >= 0) {
					neighbour = openSet.get(index);
					if (tentative_g_score >= neighbour.getG()) {
						continue;
					}
				}
				else {
					openSet.add(neighbour);
				}

				neighbour.setParent(current);
				neighbour.setG(tentative_g_score);
				neighbour.setF(neighbour.getG() + neighbour.getDistance(p_goal));
			}
		}

		return null;
	}

	public static ShapeInfo newBillboard(String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_billboard_instanced"), texture), solid, false);

		return info;
	}

	public static ShapeInfoDoor newDoor(String texture, String sideTexture, Vector3 openingPosition, Vector3 scale,
			float time) {
		ShapeCubeTexture shape = new ShapeCubeTexture(ShaderProgram.getProgram("texture"), texture);

		ShapeInstancedQuadTexture sideShape = new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_instanced"), sideTexture);

		ShapeInfoDoor info = new ShapeInfoDoor(shape, sideShape, openingPosition, scale, time);

		return info;
	}

	public static ShapeInfo newWall(String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_instanced"),
				texture), solid, true);

		return info;
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
	public static Entity rayCast(Map map, Vector3 position, Vector3 ray, float distance) {
		List<Entity> list = rayCastMultiple(map, position, ray, distance);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * Casts a ray to find all entities in its direction until it hits a wall
	 *
	 * @param position
	 *            Position to cast the ray from
	 * @param ray
	 *            Direction of the ray
	 * @param distance
	 *            Maximum distance
	 * @return List of all entities on the path from the closest to the farthest
	 *         from the original position
	 */
	public static List<Entity> rayCastMultiple(Map map, final Vector3 position, Vector3 ray, float distance) {
		List<Entity> list = new ArrayList<Entity>();

		if (ray.length() == 0)
			return list;

		ray.normalize();

		for (float i = 0f; i < distance; i += 0.2f) {
			if (position.getY() + (ray.getY() * i) > 0.5f)
				break;
			if (position.getY() + (ray.getY() * i) < -0.5f)
				break;

			int x = (int) (position.getX() + (ray.getX() * i) + 0.5f);
			int z = (int) (position.getZ() + (ray.getZ() * i) + 0.5f);

			ShapeInfo info = map.get(x, z);
			if (info != null && info.isSolid() && !info.isDoor()) {
				break;
			}

			Entity d = map.getActor(x, z);
			if (d != null) {
				list.add(d);
			}
		} // for i

		list.sort(new Comparator<Entity>() {
			@Override
			public int compare(Entity o1, Entity o2) {
				float dist1 = position.getDistanceSquared(o1.position);
				float dist2 = position.getDistanceSquared(o2.position);
				return Float.compare(dist1, dist2);
			};
		});

		return list;
	}

	/**
	 * Tests the collision of a given AABB to the map
	 *
	 * @param map
	 *            Map in which to perform the test
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
	public static Vector3 testCollision(Map map, AABB aabb, int x, int z, boolean entities) {
		Vector3 result = new Vector3();

		if (!MathUtil.inRange(x, 0, map.getSizeX()) || !MathUtil.inRange(z, 0, map.getSizeY())) {
			return result;
		}

		AABB rect = null;

		if (entities) {
			for (EntityActor e : map.getActors(x, z)) {
				if (aabb.position.equals(e.position)) {
					continue;
				}
				rect = e.getAABB();
				if (rect.collide(aabb)) {
					result.add(aabb.resolveCollision(rect));
				}
			}
		}

		ShapeInfo info = map.get(x, z);
		if (info != null && info.isSolid() && !(info instanceof ShapeInfoDoor)) {
			rect = new AABBRectangle(new Vector3(x, 0, z));
			if (info.isBillboard()) {
				rect.scale.set(0.5f, 1f, 0.5f);
			}
			if (rect.collide(aabb)) {
				result.add(aabb.resolveCollision(rect));
			}
		}

		return result;
	}

	/**
	 * Resolves collisions of an object on the map
	 *
	 * @param aabb
	 *            Bounding Box of the object to resolve
	 * @return a Vector3 containing the collision resolution
	 */
	public static Vector3 resolveCollision(Map map, AABB aabb) {
		int x = (int) (aabb.position.getX() + 0.5f);
		int z = (int) (aabb.position.getZ() + 0.5f);

		AABB copy = aabb.copy();

		Vector3 res = testCollision(map, copy, x, z, true);
		copy.position.add(res);
		res.set(testCollision(map, copy, x + 1, z, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x - 1, z, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x, z + 1, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x, z - 1, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x + 1, z + 1, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x + 1, z - 1, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x - 1, z + 1, true));
		copy.position.add(res);
		res.set(testCollision(map, copy, x - 1, z - 1, true));
		copy.position.add(res);

		Vector3 result = new Vector3(copy.position.getSub(aabb.position));
		return result;
	}
}
