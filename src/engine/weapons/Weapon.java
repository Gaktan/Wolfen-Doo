package engine.weapons;

import engine.Displayable;
import engine.entities.Camera;

public abstract class Weapon implements Displayable{
	
	protected Camera camera;
	
	public Weapon(Camera camera) {
		this.camera = camera;
	}

	public abstract void fire();
}
