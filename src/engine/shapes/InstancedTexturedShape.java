package engine.shapes;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

public abstract class InstancedTexturedShape extends TexturedShape {

	protected int instancedVBO;

	public InstancedTexturedShape(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public InstancedTexturedShape(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public void dispose() {
		GL15.glDeleteBuffers(instancedVBO);
		super.dispose();
	}

	@Override
	public void render() {
	}

	public abstract void render(int amount);

	public abstract void setData(FloatBuffer data);
}
