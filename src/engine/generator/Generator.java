package engine.generator;

import engine.game.GameWolfen;

/**
 * The Generator class is used to generate any kind of 2D map
 * 
 * @author Gaktan
 */
public abstract class Generator {

	protected int sizeX;
	protected int sizeY;
	protected GameWolfen game;
	
	public static final int MIN_SIZE = 3;
	
	/**
	 * 
	 * @param game Game instance (might be removed in the future)
	 * @param size Size of the map (min: 3, max: unlimited)
	 */
	public Generator(GameWolfen game, int sizeX, int sizeY) {
		super();
		this.game = game;
		
		this.sizeX = sizeX;
		if(this.sizeX < MIN_SIZE)
			this.sizeX = MIN_SIZE;
		
		this.sizeY = sizeY;
		if(this.sizeY < MIN_SIZE)
			this.sizeY = MIN_SIZE;
	}

	/**
	 * Method to be implemented by sub-classes to generate a new map
	 * 
	 * @return newly generated Map
	 */
	public abstract OldMap generate();
}
