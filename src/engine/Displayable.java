package engine;

import engine.entities.Camera;

public interface Displayable{

	public void update(float dt);
	
	public void render(Camera camera);
}
