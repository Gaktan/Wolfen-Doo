package game.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.shapes.InstancedTexturedShape;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

public class MapUtil {

	public static class DoorShapeInfo extends ShapeInfo {

		protected InstancedTexturedShape sideShape;
		protected Vector3 openingPosition;
		protected int orientation;
		protected float time;

		public DoorShapeInfo(Shape shape, InstancedTexturedShape sideShape, Vector3 openingPosition, int orientation,
				float time) {
			super(shape, false, true, false);
			this.sideShape = sideShape;
			this.openingPosition = openingPosition;
			this.orientation = orientation;
			this.time = time;
		}

		public InstancedTexturedShape getSideShape() {
			return sideShape;
		}

		protected Vector3 getOpeningPosition() {
			return openingPosition;
		}

		protected int getOrientation() {
			return orientation;
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
		protected boolean billboard;

		public ShapeInfo(Shape shape, boolean solid, boolean wall, boolean billboard) {
			this.shape = shape;
			this.solid = solid;
			this.wall = wall;
			this.billboard = billboard;
			amount = 0;
		}

		public boolean isBillboard() {
			return billboard;
		}

		public boolean isSolid() {
			return solid;
		}

		public boolean isWall() {
			return wall;
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
	 * @return A list containing the points to go through from start to end
	 */
	public static List<Pair> createPath(Map map, Vector3 start, Vector3 goal) {
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

				ShapeInfo info = map.get(x2, y2);
				if (info != null && info.isSolid()) {
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
				ShaderProgram.getProgram("texture_billboard_instanced"), texture), solid, false, true);

		return info;
	}

	public static DoorShapeInfo newDoor(String texture, String sideTexture, Vector3 openingPosition, int orientation,
			float time) {
		ShapeCubeTexture shape = new ShapeCubeTexture(ShaderProgram.getProgram("texture"), texture);

		ShapeInstancedQuadTexture sideShape = new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_instanced"), sideTexture);

		DoorShapeInfo info = new DoorShapeInfo(shape, sideShape, openingPosition, orientation, time);

		return info;
	}

	public static ShapeInfo newWall(String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(ShaderProgram.getProgram("texture_instanced"),
				texture), solid, true, false);

		return info;
	}
}
