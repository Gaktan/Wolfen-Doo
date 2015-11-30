package engine.shapes;

import org.lwjgl.util.vector.Vector4f;

import engine.game.ShaderProgram;

public class ShapeInstancedSprite extends ShapeInstancedQuadTexture {
	
	protected int imageWidth, 
	imageHeight,
	spritesWidth, 
	spritesHeight;

	protected ShapeInstancedSprite(ShaderProgram shaderProgram, int textureID, int imageWidth, int imageHeight,
			int spritesWidth, int spritesHeight) {
		super(shaderProgram, textureID);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	public ShapeInstancedSprite(ShaderProgram shaderProgram, String texture, int imageWidth, int imageHeight,
			int spritesWidth, int spritesHeight) {
		super(shaderProgram, texture);

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.spritesWidth = spritesWidth;
		this.spritesHeight = spritesHeight;
	}

	@Override
	public void preRender() {
		super.preRender();
		shaderProgram.setUniform("u_imageInfo", new Vector4f(imageWidth, imageHeight, spritesWidth, spritesHeight));	
	}

	@Override
	public Shape copy() {
		Shape shape = new ShapeInstancedSprite(shaderProgram, textureID, imageWidth, imageHeight, spritesWidth, spritesHeight);
		shape.init();
		return shape;
	}

}
