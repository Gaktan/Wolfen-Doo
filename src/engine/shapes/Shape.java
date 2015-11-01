package engine.shapes;

import engine.game.ShaderProgram;

/**
 * Abstract class used to created any kind of shape (1D, 2D, 3D, meshes, ...)
 * @author Gaktan
 */
public abstract class Shape {

	protected int VBO, VAO, EBO;
	protected ShaderProgram shaderProgram;

	public Shape() {}

	public Shape(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		init();
	}

	/**
	 * Must only be called once by Shape's constructor
	 */
	protected abstract void init();

	/**
	 * You may use this function to directly render a shape
	 */
	public void autoRender() {
		preRender();
		render();
		postRender();
	}

	/**
	 * Actions to be executed before rendering
	 */
	public abstract void preRender();

	/**
	 * Actual rendering
	 */
	public abstract void render();
	
	/**
	 * Action executed after rendering
	 */
	public abstract void postRender();

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}
}
