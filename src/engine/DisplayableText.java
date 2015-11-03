package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.entities.Camera;
import engine.entities.EntityActor;

/**
 * Used to render text
 * @author Gaktan
 */
public class DisplayableText extends DisplayableList {
	
	private String text;
	private Vector3f position;
	private BitMapFont font;
	private boolean hasDepth;
	
	public DisplayableText(Vector3f position, String text, BitMapFont font, boolean hasDepth) {
		this.position = position;
		this.font = font;
		
		changeText(text);
		this.hasDepth = hasDepth;
	}
	
	/**
	 * You should probably want to use setText instead
	 */
	public void changeText(String newText) {
		
		this.text = newText;
		
		list.clear();
		
		Vector3f newPosition = new Vector3f();
		Vector3f halfDir = new Vector3f(1, 0, 0);
		halfDir = (Vector3f) halfDir.scale(0.08f);
		
		for (char c : newText.toCharArray()) {
			int i_c = (int) c;

			if (i_c > font.getAmountOfChars()) {
				i_c = 0;
			}

			EntityActor actorChar = new EntityActor(font.getShape());

			Vector3f.add(position, newPosition, actorChar.position);
			
			int charsPerLine = font.getImageSize() / font.getCharSize();
			
			float y = i_c / charsPerLine;
			float x = i_c % charsPerLine;

			Vector3f vec = new Vector3f(x, y, font.getImageFactor());
			actorChar.textureCoordinate = vec;
			
			add(actorChar);

			Vector3f.add(newPosition, halfDir, newPosition);
		}
	}

	@Override
	public void render(Camera camera) {
		if (!hasDepth)
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		super.render(camera);
		
		if (!hasDepth)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public String getText() {
		return text;
	}

	/**
	 * Changes current text with new one. If the texts are the same, nothing will be changed
	 * @param text New text
	 */
	public void setText(String text) {
		if(text.equals(this.text))
			return;
		
		changeText(text);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
}
