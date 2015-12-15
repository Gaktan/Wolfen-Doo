package engine.shapes;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

/**
 * Abstract class used to created any kind of shape (1D, 2D, 3D, meshes, ...)
 * 
 * @author Gaktan
 */
public abstract class Shape {

	protected int VBO, VAO, EBO;
	protected ShaderProgram shaderProgram;

	public Shape() {
	}

	public Shape(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		init();
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
}
