package engine;

import engine.entities.Camera;

public interface Displayable{

	/**
	 * 
	 * @param dt Delta time
	 * @return false if you want to delete the object
	 */
	public boolean update(float dt);
	
	public void render(Camera camera);
	
	public void delete();
}
