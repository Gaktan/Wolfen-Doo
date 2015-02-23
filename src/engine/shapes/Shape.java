package engine.shapes;

import shaders.ShaderProgram;

public abstract class Shape {
	
	protected int VBO, VAO, EBO;
	protected ShaderProgram shaderProgram;
	
	public Shape() {}
	
	public Shape(ShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		init();
	}
	
	public abstract void init();
	
	public void autoRender(){
		preRender();
		render();
		postRender();
	}
	
	public abstract void preRender();
	public abstract void render();
	public abstract void postRender();
	
	public ShaderProgram getShaderProgram(){
		return shaderProgram;
	}

}
