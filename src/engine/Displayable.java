package engine;

/**
 * Highest interface of the engine. Allows you to update and render any object
 *
 * @author Gaktan
 */
public interface Displayable {

	/**
	 * Should be used to delete the object
	 */
	public void delete();

	/**
	 * Used to render an object
	 *
	 * @param camera
	 */
	public void render();

	/**
	 * Used to update an object's logic
	 *
	 * @param dt
	 *            Delta time
	 * @return false if you want to delete the object
	 */
	public boolean update(float dt);
}
