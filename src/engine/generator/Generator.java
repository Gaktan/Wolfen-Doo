package engine.generator;

/**
 * The Generator class is used to generate any kind of 2D map
 * 
 * @author Gaktan
 */
public abstract class Generator {

	public static final int MIN_SIZE = 3;
	protected int sizeX;

	protected int sizeY;

	/**
	 * 
	 * @param size
	 *            Size of the map (min: 3, max: unlimited)
	 */
	public Generator(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		if (this.sizeX < MIN_SIZE)
			this.sizeX = MIN_SIZE;

		this.sizeY = sizeY;
		if (this.sizeY < MIN_SIZE)
			this.sizeY = MIN_SIZE;
	}

	/**
	 * Method to be implemented by sub-classes to generate a new map
	 * 
	 * @return newly generated Map
	 */
	public abstract Map generate();
}
