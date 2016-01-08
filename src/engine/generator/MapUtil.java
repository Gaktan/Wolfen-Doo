package engine.generator;

import engine.shapes.InstancedTexturedShape;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeCubeTexture;
import engine.shapes.ShapeInstancedQuadTexture;
import engine.util.Vector3;

public class MapUtil {

	public static class DoorShapeInfo extends ShapeInfo {

		protected InstancedTexturedShape sideShape;
		protected Vector3 openingPosition;
		protected int orientation;
		protected float time;

		public DoorShapeInfo(Shape shape, InstancedTexturedShape sideShape, Vector3 openingPosition, int orientation,
				float time) {
			super(shape, false, true);
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

		public ShapeInfo(Shape shape, boolean solid, boolean wall) {
			this.shape = shape;
			this.solid = solid;
			this.wall = wall;
			amount = 0;
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

	public static ShapeInfo newBillboard(String texture, boolean solid) {
		ShapeInfo info = new ShapeInfo(new ShapeInstancedQuadTexture(
				ShaderProgram.getProgram("texture_billboard_instanced"), texture), solid, false);

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
				texture), solid, true);

		return info;
	}
}
