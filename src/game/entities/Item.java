package game.entities;

import engine.entities.EntityActor;
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
		super.setUniforms();
		shape.getShaderProgram().setUniform("u_spriteNumber", itemNumber);
	}

	public int getValue() {
		return value;
	}

	public int getItemNumber() {
		return itemNumber;
	}
}
