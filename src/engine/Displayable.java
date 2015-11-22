package engine;

/**
 * Highest interface of the engine. Allows you to update and render any object
 * @author Gaktan
 */
public interface Displayable {

	/**
	 * Used to update an object's logic
	 * @param dt Delta time
	 * @return false if you want to delete the object
	 */
	public boolean update(float dt);
	
	/**
	 * Used to render an object
	 * @param camera
	 */
	public void render();
	
	/**
	 * Should be used to delete the object
	 */
	public void delete();
	
	/**
	 * Should be used to get the size of the object
	 * @return The amount of Displayable the object contains
	 */
	public int size();
}
