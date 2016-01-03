package engine.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
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

	protected int VBO;
	protected int VAO;

	public EntityLine(Vector3 position, Vector3 positionB) {
		this(position, positionB, new Vector3(1f, 0f, 0f), new Vector3(0f, 0f, 1f));
	}

	public EntityLine(Vector3 position, Vector3 positionB, Vector3 colorA, Vector3 colorB) {
		this.position = position;
		this.positionB = positionB;

		this.colorA = colorA;
		this.colorB = colorB;

		VAO = GL30.glGenVertexArrays();
		VBO = GL15.glGenBuffers();
	}

	/*

	@Override
	public void render() {
		GL11.glPushMatrix();

		GL11.glLoadMatrix(GameStateManager.getCurrentGameState().current_camera.getProjectionXview().toFloatBuffer());

		GL11.glLineWidth(2.5f);
		GL11.glColor3f(colorA.r, colorA.g, colorA.b);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(position.getX(), position.getY(), position.getZ());
			GL11.glColor3f(colorB.r, colorB.g, colorB.b);
			GL11.glVertex3f(positionB.getX(), positionB.getY(), positionB.getZ());
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}
	*/

	@Override
	public void render() {

		ShaderProgram program = ShaderProgram.getProgram("color");

		if (program == null)
			return;

		program.bind();

		program.setUniform("u_model", Matrix4.createIdentityMatrix());

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
