package engine.shapes;

import engine.shapes.ShaderProgram.Uniform;

public class ShapeInstancedSprite extends ShapeInstancedQuadTexture {

	protected int imageWidth, imageHeight, spritesWidth, spritesHeight;

	public ShapeInstancedSprite(ShaderProgram shaderProgram, String texture, int imageWidth, int imageHeight,
			int spritesWidth, int spritesHeight) {
		super(shaderProgram, texture);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	protected ShapeInstancedSprite(ShaderProgram shaderProgram, int textureID, int imageWidth, int imageHeight,
			int spritesWidth, int spritesHeight) {
		super(shaderProgram, textureID);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	@Override
	public Shape copy() {
		Shape shape = new ShapeInstancedSprite(shaderProgram, textureID, imageWidth, imageHeight, spritesWidth,
				spritesHeight);
		return shape;
	}

	@Override
	public void preRender() {
		super.preRender();
		shaderProgram.setUniform(Uniform.imageInfo, imageWidth, imageHeight, spritesWidth, spritesHeight);
	}

	@Override
	public void postRender() {
		super.postRender();
		shaderProgram.setUniform(Uniform.imageInfo, 1f, 1f, 1f, 1f);
	}
}
