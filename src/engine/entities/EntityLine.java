package engine.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.shapes.ShaderProgram;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.Shape;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Draws a line
 *
 * @author Gaktan
 */
public class EntityLine extends Entity {

	public Vector3 positionB;
	protected Vector3 colorA;
	protected Vector3 colorB;

	protected ShaderProgram program;

	// TODO: change to cube
	protected int VBO;
	protected int VAO;

	public EntityLine(Vector3 position, Vector3 positionB, ShaderProgram program) {
		this(position, positionB, new Vector3(1f, 0f, 0f), new Vector3(0f, 0f, 1f), program);
	}

	public EntityLine(Vector3 position, Vector3 positionB, Vector3 color, ShaderProgram program) {
		this(position, positionB, color, color, program);
	}

	public EntityLine(Vector3 position, Vector3 positionB, Vector3 colorA, Vector3 colorB, ShaderProgram program) {
		this.position = position;
		this.positionB = positionB;

		this.colorA = colorA;
		this.colorB = colorB;

		this.program = program;
		VAO = GL30.glGenVertexArrays();
		VBO = GL15.glGenBuffers();
	}

	/**
	 * Line - AABB Intersection
	 *
	 * @param aabb
	 * @return Null if there is no intersection. Otherwise intersection point
	 */
	public Vector3 collide(AABB aabb) {
		float low = 0;
		float high = 1;

		Vector3 aabbMin = new Vector3(aabb.position);
		Vector3 aabbMax = new Vector3(aabb.position);
		aabbMin.add(aabb.scale.getScale(-0.5f));
		aabbMax.add(aabb.scale.getScale(0.5f));

		Vector3 result = clipLine(aabbMin.getX(), aabbMax.getX(), position.getX(), positionB.getX(), low, high);
		if (result == null) {
			return null;
		}
		low = result.getX();
		high = result.getY();

		result = clipLine(aabbMin.getY(), aabbMax.getY(), position.getY(), positionB.getY(), low, high);
		if (result == null) {
			return null;
		}
		low = result.getX();
		high = result.getY();

		result = clipLine(aabbMin.getZ(), aabbMax.getZ(), position.getZ(), positionB.getZ(), low, high);
		if (result == null) {
			return null;
		}
		low = result.getX();
		high = result.getY();

		// The formula for I: http://youtu.be/USjbg5QXk3g?t=6m24s
		Vector3 b = positionB.getSub(position);
		Vector3 intersection = position.getAdd(b.getScale(low));

		return intersection;
	}

	private Vector3 clipLine(float aabbMin, float aabbMax, float v0, float v1, float low, float high) {
		// low and high are the results from all clipping so far.

		// dim_low and dim_high are the results we're calculating for this
		// current dimension.
		float dim_low, dim_high;

		// Find the point of intersection in this dimension only as a fraction
		// of the total vector http://youtu.be/USjbg5QXk3g?t=3m12s
		dim_low = (aabbMin - v0) / (v1 - v0);
		dim_high = (aabbMax - v0) / (v1 - v0);

		// Make sure low is less than high
		if (dim_high < dim_low) {
			float temp = dim_high;
			dim_high = dim_low;
			dim_low = temp;
		}

		// If this dimension's high is less than the low we got then we
		// definitely missed. http://youtu.be/USjbg5QXk3g?t=7m16s
		if (dim_high < low)
			return null;

		// Likewise if the low is less than the high.
		if (dim_low > high)
			return null;

		// Add the clip from this dimension to the previous results
		// http://youtu.be/USjbg5QXk3g?t=5m32s
		low = MathUtil.max(dim_low, low);
		high = MathUtil.min(dim_high, high);

		if (low > high)
			return null;

		return new Vector3(low, high, 0f);
	}

	@Override
	public void dispose() {
		GL30.glDeleteVertexArrays(VAO);
		GL15.glDeleteBuffers(VBO);
	}

	@Override
	public void render() {
		program.bind();
		program.setUniform(Uniform.model, Matrix4.createIdentityMatrix());
		program.setUniform(Uniform.color, new Vector3(1f));

		FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * 4);
		position.store(vertices);
		colorA.store(vertices);
		positionB.store(vertices);
		colorB.store(vertices);
		vertices.flip();

		// VAO
		GL30.glBindVertexArray(VAO);

		// VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

		GL20.glEnableVertexAttribArray(0);
		// v - position in layout (see shader)
		// v - Nb of component per vertex (2 for 2D (x, y))
		// v - Normalized ? (between 0 - 1)
		// v - Offset between things (size of a line)
		// v - Where to start ?
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * Shape.FLOAT_SIZE, 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * Shape.FLOAT_SIZE, 3 * Shape.FLOAT_SIZE);

		GL11.glLineWidth(5f);

		GL11.glDrawArrays(GL11.GL_LINES, 0, 2);

		GL11.glLineWidth(1f);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);

		ShaderProgram.unbind();
	}
}
