package game.entities;

import engine.entities.EntityActor;
import engine.shapes.ShaderProgram.Uniform;
import engine.util.Matrix4;
import engine.util.Vector3;

public class Item extends EntityActor {

	protected int value;
	protected int itemNumber;

	public Item(Vector3 position, int itemNumber, int value) {
		super(null);

		this.itemNumber = itemNumber;
		this.value = value;

		float scale = 0.35f;

		this.scale.set(scale);
		this.position.set(position);
		this.position.setY(-0.5f + 0.5f * scale);
	}

	@Override
	public void setUniforms() {
		shape.getShaderProgram().setUniform(Uniform.color, color);
		Matrix4 model = Matrix4.createModelMatrix(position, rotation, scale);
		shape.getShaderProgram().setUniform(Uniform.model, model);
		shape.getShaderProgram().setUniform(Uniform.spriteNumber, itemNumber);
	}

	public int getValue() {
		return value;
	}

	public int getItemNumber() {
		return itemNumber;
	}
}
