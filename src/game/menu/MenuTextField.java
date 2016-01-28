package game.menu;

import engine.entities.BitMapFont;
import engine.game.Controls;
import engine.game.Controls.ControlsCharListener;
import engine.shapes.Shape;
import engine.util.Vector3;

public class MenuTextField extends MenuButton implements ControlsCharListener {

	public static final String CURSOR_CHAR = "|";
	protected static final float CURSOR_TIME = 800f;

	protected ControlsCharListener controlsCharListener;
	protected boolean currentlyEditing;
	protected boolean editingChanged;
	protected float cursorTimer;

	public MenuTextField(Shape shape, String text, Vector3 position, float scale, BitMapFont font) {
		super(shape, text, position, scale, font);

		currentlyEditing = false;
		cursorTimer = CURSOR_TIME;
	}

	@Override
	public void onKeyPress(char key) {
		if (controlsCharListener != null) {
			setText(text.getText().replace(CURSOR_CHAR, ""));
			controlsCharListener.onKeyPress(key);
		}
	}

	public void setControlsCharListener(ControlsCharListener listener) {
		controlsCharListener = listener;
	}

	@Override
	public boolean update(float dt) {
		if (!currentlyEditing && editingChanged) {
			Controls.removeControlsCharListener(this);
		}
		if (currentlyEditing) {
			cursorTimer -= dt;

			if (cursorTimer < 0) {
				cursorTimer = CURSOR_TIME;

				if (text.getText().endsWith(CURSOR_CHAR)) {
					setText(text.getText().replace(CURSOR_CHAR, ""));
				}
				else {
					setText(text.getText() + CURSOR_CHAR);
				}
			}
		}

		return super.update(dt);
	}

	public void setEditing(boolean editing) {

		if (editing && !currentlyEditing) {
			currentlyEditing = true;
			Controls.addControlsCharListener(this);
		}
		else {
			setText(text.getText().replace(CURSOR_CHAR, ""));
			currentlyEditing = false;
		}

		editingChanged = true;
	}
}
