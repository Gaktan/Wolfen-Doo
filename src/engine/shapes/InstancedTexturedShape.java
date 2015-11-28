package engine.shapes;

import java.nio.FloatBuffer;

import engine.game.ShaderProgram;

public abstract class InstancedTexturedShape extends TexturedShape {

	protected int instancedVBO;

	public InstancedTexturedShape(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public InstancedTexturedShape(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	public abstract void render(int amount);

	public abstract void setData(FloatBuffer data);

	@Override
	public void render() {}
}
