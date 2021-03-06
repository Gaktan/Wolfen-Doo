package game.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import engine.util.MathUtil;
import engine.util.Vector3;
import game.game.GameWolfen;

public class DungeonGenerator extends Generator {

	class Pair {
		protected int x;
		protected int y;
		protected char c;
		protected ArrayList<Pair> friends;

		public Pair(int x, int y) {
			this(x, y, VOID);
		}

		public Pair(int x, int y, char c) {
			this.x = x;
			this.y = y;
			this.c = c;

			friends = new ArrayList<Pair>();
		}

		@Override
		public boolean equals(Object arg0) {
			if (arg0 instanceof Pair) {
				Pair pair = (Pair) arg0;

				return (pair.x == x && pair.y == y);
			}

			return super.equals(arg0);
		}

		@Override
		public String toString() {
			return "[" + x + ", " + y + "]";
		}
	}

	private static final char ARROW_LEFT = 0x2190;
	private static final char ARROW_RIGHT = 0x2192;
	private static final char ARROW_DOWN = 0x2193;
	private static final char ARROW_UP = 0x2191;
	private static final char START = '�';
	private static final char VOID = ' ';
	private static final char WALL = '*';
	private static final char PORTRAIT = '+';
	private static final char DOOR_NORTH = '|';
	private static final char DOOR_EAST = '_';

	protected long seed;
	protected int roomSize;
	protected int realRoomSize;
	protected Random random;
	protected boolean intersections;
	protected Vector3 startingPoint;
	protected ArrayList<Pair> map;

	protected int realSizeX;
	protected int realSizeY;

	/**
	 *
	 * @param sizeX
	 *
	 * @param sizeY
	 *            Maximum Y size of the map
	 * @param seed
	 *            Seed
	 * @param roomSize
	 *            Size of the inside of a room (counting walls)
	 * @param noIntersection
	 *            True for a straight dungeon, false for intersections
	 */
	public DungeonGenerator() {
		super(3, 3);

		startingPoint = new Vector3();

		roomSize = 3;
		seed = 0;
		intersections = true;
	}

	@Override
	public Map generate() {
		if (GameWolfen.DEBUG) {
			System.out.println("Generating map with seed : " + seed);
			System.out.println("SizeX : " + sizeX + ", SizeY : " + sizeY);
		}
		long time_start = System.currentTimeMillis();

		realSizeX = sizeX;
		realSizeY = sizeY;

		random = new Random(seed);

		map = new ArrayList<Pair>(sizeX * sizeY);

		for (int i = 0; i < sizeX * sizeY; i++) {
			int x = i % sizeX;
			int y = i / sizeX;

			map.add(y * sizeX + x, new Pair(x, y));
		}

		int posX = random(0, sizeX);
		int posY = random(0, sizeY);

		buildMap(posX, posY);

		if (GameWolfen.DEBUG) {
			print();
		}

		char[][] charMap = buildRealMap();

		Map map = new Map(charMap[0].length, charMap.length);
		map.setStartingPoint(startingPoint);

		map.newWall(WALL, "wall.png", true);
		map.newWall(PORTRAIT, "wall_portrait.png", true);
		map.newDoor(DOOR_NORTH, "door.png", "door_side.png", new Vector3(-0.95f, 0, 0), new Vector3(1f, 1f, 0.1f), 800f);
		map.newDoor(DOOR_EAST, "door.png", "door_side.png", new Vector3(0, 0, 0.95f), new Vector3(0.1f, 1f, 1f), 800f);

		// colorScale = 1 / 256
		float colorScale = 0.00390625f;

		Vector3 downColor = new Vector3(237f, 157f, 95f).getScale(colorScale);
		Vector3 upColor = new Vector3(252f, 231f, 227f).getScale(colorScale);
		map.setSky(upColor, downColor);

		StringBuilder sb = new StringBuilder();
		for (char[] cc : charMap) {
			sb.append(cc);
		}
		map.buildMapFromString(sb.toString());

		if (GameWolfen.DEBUG) {
			long time_end = System.currentTimeMillis();
			System.out.println("Done.\nGeneration took : " + (time_end - time_start) + "ms.");
		}

		return map;
	}

	public void buildMap(int posX, int posY) {
		ArrayList<Pair> donePairs = new ArrayList<Pair>();
		donePairs.add(get(posX, posY));
		donePairs.get(0).c = START;

		for (int i = 0; i < ((sizeX + sizeY) / 2); i++) {
			if (donePairs.isEmpty())
				break;

			Collections.shuffle(donePairs, random);
			Pair currentPair = donePairs.get(0);

			if (!intersections) {
				donePairs.remove(currentPair);
			}

			ArrayList<Pair> neighbours = getNeighbours(currentPair, true);

			if (neighbours.isEmpty()) {
				donePairs.remove(currentPair);
				continue;
			}

			int x = currentPair.x;
			int y = currentPair.y;

			Collections.shuffle(neighbours, random);
			Pair neighbour = neighbours.get(0);

			currentPair.friends.add(neighbour);

			if (x == posX && y == posY)
				;

			else if (x < neighbour.x)
				currentPair.c = ARROW_RIGHT;
			else if (x > neighbour.x)
				currentPair.c = ARROW_LEFT;
			else if (y < neighbour.y)
				currentPair.c = ARROW_DOWN;
			else if (y > neighbour.y)
				currentPair.c = ARROW_UP;

			neighbour.c = 'o';
			donePairs.add(neighbour);
		}

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = 0;
		int maxY = 0;

		// finding X and Y min and Max
		for (Pair p : map) {
			if (p.c == VOID) {
				continue;
			}

			minX = MathUtil.min(minX, p.x);
			minY = MathUtil.min(minY, p.y);

			maxX = MathUtil.max(maxX, p.x);
			maxY = MathUtil.max(maxY, p.y);
		}

		realSizeX = maxX - minX;
		realSizeY = maxY - minY;

		// Trimming the map
		ArrayList<Pair> removeList = new ArrayList<Pair>();
		for (Pair p : map) {
			if (p.x < minX || p.x > maxX) {
				removeList.add(p);
				continue;
			}
			if (p.y < minY || p.y > maxY) {
				removeList.add(p);
				continue;
			}
			if (p.c == VOID) {
				removeList.add(p);
				continue;
			}

			p.x -= minX;
			p.y -= minY;
		}

		for (Pair p : removeList) {
			map.remove(p);
		}
	}

	public char[][] buildRealMap() {

		int width = (realSizeX + 2) * realRoomSize - (realRoomSize - 1);
		int height = (realSizeY + 2) * realRoomSize - (realRoomSize - 1);

		char[][] newMap = new char[height][width];

		for (int i = 0; i < (width * height) - 1; i++) {
			int x = i % width;
			int y = i / width;

			newMap[y][x] = VOID;
		}

		for (Pair p : map) {
			if (p.c == VOID)
				continue;

			int x = p.x * realRoomSize;
			int y = p.y * realRoomSize;

			if (p.c == START) {
				startingPoint.set(x + (realRoomSize * 0.5f), 0f, y + (realRoomSize * 0.5f));
			}

			for (int i = 0; i <= realRoomSize; i++) {
				for (int j = 0; j <= realRoomSize; j++) {
					if (!(i > 0 && i < realRoomSize) || !(j > 0 && j < realRoomSize)) {
						newMap[y + i][x + j] = WALL;
					}

				} // for j
			} // for i
		} // for p

		for (Pair p : map) {
			int x = p.x * realRoomSize;
			int y = p.y * realRoomSize;

			for (Pair f : p.friends) {
				int xf = f.x * realRoomSize;
				int yf = f.y * realRoomSize;

				int r = random(0, 2);

				// Make a door --
				if (r == 0) {
					int medX = ((x + realRoomSize / 2) + (xf + realRoomSize / 2)) / 2;
					int medY = ((y + realRoomSize / 2) + (yf + realRoomSize / 2)) / 2;

					if (xf != x) {
						newMap[medY][medX] = DOOR_EAST;
					}
					else {
						newMap[medY][medX] = DOOR_NORTH;
					}
				} // Door

				// Make a big room --
				else if (r == 1) {
					if (x == xf) {
						if (y < yf) {
							for (int i = x + realRoomSize - 1; i > x; i--) {
								newMap[y + realRoomSize][i] = VOID;
							}
						}
						else {
							for (int i = x + 1; i < x + realRoomSize; i++) {
								newMap[y][i] = VOID;
							}
						}
					} // x == f.x
					else if (y == yf) {
						if (x < xf) {
							for (int i = y + realRoomSize - 1; i > y; i--) {
								newMap[i][x + realRoomSize] = VOID;
							}
						}
						else {
							for (int i = y + 1; i < y + realRoomSize; i++) {
								newMap[i][x] = VOID;
							}
						}
					} // y == f.y
				} // Big room
			} // for f

			int medX = (x + realRoomSize / 2);
			int medY = (y + realRoomSize / 2);

			replaceCharRandom(PORTRAIT, WALL, x, medY, newMap, 10);
			replaceCharRandom(PORTRAIT, WALL, medX, y, newMap, 10);
			replaceCharRandom(PORTRAIT, WALL, medX, y + realRoomSize, newMap, 10);
			replaceCharRandom(PORTRAIT, WALL, x + realRoomSize, medY, newMap, 10);
		}

		return newMap;
	}

	public Pair get(int x, int y) {
		return map.get(y * realSizeX + x);
	}

	public ArrayList<Pair> getNeighbours(int x, int y, boolean foundNeighour) {
		ArrayList<Pair> list = new ArrayList<Pair>();

		if (x > 0) {
			Pair p = get(x - 1, y);
			if (!(p.c != VOID && foundNeighour))
				list.add(p);
		}

		if (x < realSizeX - 1) {
			Pair p = get(x + 1, y);
			if (!(p.c != VOID && foundNeighour))
				list.add(p);
		}

		if (y > 0) {
			Pair p = get(x, y - 1);
			if (!(p.c != VOID && foundNeighour))
				list.add(p);
		}

		if (y < realSizeY - 1) {
			Pair p = get(x, y + 1);
			if (!(p.c != VOID && foundNeighour))
				list.add(p);
		}

		return list;
	}

	public ArrayList<Pair> getNeighbours(Pair p, boolean foundNeighour) {
		return getNeighbours(p.x, p.y, foundNeighour);
	}

	public long getSeed() {
		return seed;
	}

	public void print() {
		char[][] newMap = new char[realSizeY + 1][realSizeX + 1];

		for (Pair p : map) {
			newMap[p.y][p.x] = p.c;
		}

		for (char[] cc : newMap) {
			for (char c : cc) {
				System.out.print(c);
			}
			System.out.println();
		}
	}

	public int random(int min, int max) {
		return min + (int) (random.nextDouble() * (max - min));
	}

	public void replaceChar(char replace, char find, int x, int y, char[][] map) {
		if (map[y][x] == find) {
			setChar(replace, x, y, map);
		}
	}

	public void replaceCharRandom(char replace, char find, int x, int y, char[][] map, int probability) {
		int r = random(0, 100);

		if (r < probability) {
			replaceChar(replace, find, x, y, map);
		}
	}

	public void setChar(char c, int x, int y, char[][] map) {
		map[y][x] = c;
	}

	@Override
	public DungeonGenerator setSizeX(int sizeX) {
		return (DungeonGenerator) super.setSizeX(sizeX);
	}

	@Override
	public DungeonGenerator setSizeY(int sizeY) {
		return (DungeonGenerator) super.setSizeY(sizeY);
	}

	/**
	 * Sets the seed
	 *
	 * @param seed
	 *            Seed
	 */
	public DungeonGenerator setSeed(long seed) {
		this.seed = seed;
		return this;
	}

	/**
	 * Sets the rooms size
	 *
	 * @param roomSize
	 *            Rooms size
	 */
	public DungeonGenerator setRoomSize(int roomSize) {
		this.roomSize = roomSize;
		if (this.roomSize < 3)
			this.roomSize = 3;

		realRoomSize = this.roomSize + 1;
		if (realRoomSize % 2 != 0)
			realRoomSize--;

		return this;
	}

	/**
	 * Sets if one room can be connected with several rooms. If not, it will be
	 * a one-way dungeon, similar to a maze
	 *
	 * @param intersections
	 *            Interestions
	 */
	public DungeonGenerator setIntersections(boolean intersections) {
		this.intersections = intersections;
		return this;
	}
}
