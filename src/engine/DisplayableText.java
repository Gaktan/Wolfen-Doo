package engine;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.EntityActor;

public class DisplayableText extends DisplayableList {
	
	private String text;
	private Vector3f position;
	private BitMapFont font;
	
	public DisplayableText(Vector3f position, String text, BitMapFont font) {
		this.position = position;
		this.font = font;
		
		changeText(text);
	}
	
	public void changeText(String newText) {
		
		this.text = newText;
		
		list.clear();
		
		Vector3f newPosition = new Vector3f();
		Vector3f halfDir = new Vector3f(1, 0, 0);
		halfDir = (Vector3f) halfDir.scale(0.08f);
		
		for(char c : newText.toCharArray()){
			int i_c = (int) c;

			if(i_c > font.getAmountOfChars()){
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

	public String getText() {
		return text;
	}

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
