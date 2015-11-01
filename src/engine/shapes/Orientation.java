package engine.shapes;

/**
 * Class used to define orientations. Primarily used by EntityWalls. <br>
 * Whenever you see the argument "orientation", refer to this class.
 * 
 * @author Gaktan
 */
public class Orientation {
	public static final int NORTH = 0b1;
	public static final int SOUTH = 0b10;
	public static final int EAST = 0b100;
	public static final int WEST = 0b1000;
	public static final int ALL = 0b1111;
}
