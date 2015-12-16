package game.menu;

import engine.BitMapFont;
import engine.game.Controls;
import engine.game.Controls.ControlsCharListener;
import engine.shapes.Shape;
import engine.util.Vector3;

public class MenuTextField extends MenuButton implements ControlsCharListener {

	protected ControlsCharListener controlsCharListener;
	protected boolean currentlyEditing;
	protected boolean editingChanged;

	public MenuTextField(Shape shape, String text, Vector3 position, float scale, BitMapFont font) {
		super(shape, text, position, scale, font);

		currentlyEditing = false;
	}

	@Override
	public void onKeyPress(char key) {
		if (controlsCharListener != null) {
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
		return super.update(dt);
	}

	public void setEditing(boolean editing) {

		if (editing && !currentlyEditing) {
			currentlyEditing = true;
			Controls.addControlsCharListener(this);
		}
		else {
			currentlyEditing = false;
		}

		editingChanged = true;
	}
}
