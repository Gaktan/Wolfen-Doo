package engine.shapes;

import engine.shapes.ShaderProgram.Uniform;

public class ShapeSprite extends ShapeQuadTexture {

	protected int imageWidth, imageHeight, spritesWidth, spritesHeight;

	public ShapeSprite(ShaderProgram shaderProgram, String texture, int imageWidth, int imageHeight, int spritesWidth,
			int spritesHeight) {
		super(shaderProgram, texture);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	protected ShapeSprite(ShaderProgram shaderProgram, int textureID, int imageWidth, int imageHeight,
			int spritesWidth, int spritesHeight) {
		super(shaderProgram, textureID);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	@Override
	public Shape copy() {
		Shape shape = new ShapeSprite(shaderProgram, textureID, imageWidth, imageHeight, spritesWidth, spritesHeight);
		shape.init();
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
