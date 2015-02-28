package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.game.GameWolfen;
import engine.shapes.ShapeQuadTexture;
import engine.util.MathUtil;
import engine.util.TextureUtil;

public class BitMapFont {

	private int imageSize;
	private int charSize;
	private int amountOfChars;
	
	private float imageFactor;

	private String chars;

	private ShapeQuadTexture shape;

	public BitMapFont(GameWolfen game, String path, int imageSize, int charSize) {
		StringBuffer sb = new StringBuffer();

		shape = new ShapeQuadTexture(game.shaderProgramTexCamera, path);

		this.imageSize = imageSize;
		this.charSize = charSize;
		amountOfChars = charSize*charSize;
		
		imageFactor = (float) charSize / imageSize;

		for(int i = 0; i < amountOfChars; i++){
			sb.append((char) i); 
		}

		chars = sb.toString();
	}

	public DisplayableList createString(Vector3f position, String str){

		DisplayableList dl = new DisplayableList();
		Vector3f newPosition = new Vector3f();
		Vector3f halfDir = new Vector3f(1, 0, 0);
		halfDir = (Vector3f) halfDir.scale(0.08f);
		
		for(char c : str.toCharArray()){
			int i_c = (int) c;

			if(i_c > amountOfChars){
				i_c = 0;
			}

			EntityActor actorChar = new EntityActor(shape);

			Vector3f.add(position, newPosition, actorChar.position);
			
			int charsPerLine = imageSize / charSize;
			
			float y = i_c / charsPerLine;
			float x = i_c % charsPerLine;

			Vector3f vec = new Vector3f(x, y, imageFactor);
			actorChar.textureCoordinate = vec;
			
			dl.add(actorChar);

			Vector3f.add(newPosition, halfDir, newPosition);
		}
		
		return dl;
	}
	
	public void drawString(Vector3f position, String str, Camera camera){
		DisplayableList dl = createString(position, str);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		dl.render(camera);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
}
