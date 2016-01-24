package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Abstract class used to created any kind of shape (1D, 2D, 3D, meshes, ...)
 *
 * @author Gaktan
 */
public abstract class Shape {

	public static final int FLOAT_SIZE = Float.SIZE / 8;

	protected int VBO, VAO, EBO;
	protected ShaderProgram shaderProgram;

	public Shape(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		init();
	}

	public Shape() {
	}

	/**
	 * You may use this function to directly render a shape
	 */
	public void autoRender() {
		preRender();
		render();
		postRender();
	}

	public Shape copy() {
		return null;
	}

	public void dispose() {
		GL15.glDeleteBuffers(EBO);
		GL15.glDeleteBuffers(VBO);
		GL30.glDeleteVertexArrays(VAO);
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	/**
	 * Action executed after rendering
	 */
	public abstract void postRender();

	/**
	 * Actions to be executed before rendering
	 */
	public abstract void preRender();

	/**
	 * Actual rendering
	 */
	public abstract void render();

	/**
	 * Must only be called once by Shape's constructor
	 */
	protected abstract void init();

	/**
	 * Creates the VAO
	 */
	protected void createArrayObject() {
		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);
	}

	/**
	 * Creates the VBO based on given FloatBuffer
	 *
	 * @param vertices
	 *            FlotaBuffer containing all vertices
	 */
	protected void loadVertices(FloatBuffer vertices) {
		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
	}

	/**
	 * Creates the EBO based on given IntBuffer
	 *
	 * @param indices
	 *            IntBuffer containing all indices
	 */
	protected void loadIndices(IntBuffer indices) {
		EBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
	}

	/**
	 * Set the current VAO's attribs (see ShapeQuadTexture) for an example
	 */
	protected abstract void setAttribs();
}
