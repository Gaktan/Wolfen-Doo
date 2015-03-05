package engine.generator;

import engine.game.GameWolfen;
import engine.game.Map;

public abstract class Generator {

	public int sizeX;
	public int sizeY;
	protected GameWolfen game;
	public static final int MIN_SIZE = 3;
	
	
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

	public abstract Map generate();
}
