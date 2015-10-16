package engine.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import org.lwjgl.util.vector.Vector2f;

import engine.game.GameWolfen;
import engine.game.Map;

public class MazeGenerator extends Generator{
	
	public MazeGenerator(GameWolfen game, int sizeX, int sizeY) {
		super(game, sizeX, sizeY);
	}

	@Override
	public Map generate() {
		int total = sizeX*sizeY;
		boolean visited[][] = new boolean[sizeX][sizeY];
		int visitedCount = 0;

		StringBuilder map = new StringBuilder();

		for(int i = 0; i < total; i++){
			map.append('O');
		}

		Stack<Vector2f> stack = new Stack<Vector2f>();

		int currentX = 1;
		int currentY = 1;
		visited[currentX][currentY] = true;
		visitedCount++;

		while(visitedCount <= total){

			ArrayList<Vector2f> neighbours = new ArrayList<Vector2f>();

			int x = currentX, y = currentY;

			int difference = 2;

			if(x-difference >= 1){
				if(!visited[x-difference][y])
					neighbours.add(new Vector2f(x-difference, y));
			}

			if(x+difference < sizeX-1){
				if(!visited[x+difference][y])
					neighbours.add(new Vector2f(x+difference, y));
			}

			if(y-difference >= 1){
				if(!visited[x][y-difference])
					neighbours.add(new Vector2f(x, y-difference));
			}

			if(y+difference < sizeY-1){
				if(!visited[x][y+difference])
					neighbours.add(new Vector2f(x, y+difference));
			}

			if(!neighbours.isEmpty()){
				Collections.shuffle(neighbours);
				Vector2f neighbour = neighbours.get(0);

				int x2 = (int) neighbour.getX(), y2 = (int) neighbour.getY();

				stack.push(neighbour);

				int x3 = ((x2 + x)/2);
				int y3 = ((y2 + y)/2);

				int diff = (x3 * sizeY) + y3;

				map.setCharAt(diff, ' ');
				diff = (x * sizeY) + y;
				map.setCharAt(diff, ' ');

				currentX = (int) neighbour.getX();
				currentY = (int) neighbour.getY();
				visited[currentX][currentY] = true;
				visitedCount++;
			}

			else if(!stack.isEmpty()){
				Vector2f temp = stack.pop();
				currentX = (int) temp.getX();
				currentY = (int) temp.getY();
			}

			else{
				break;
			}
		}

		Map mapR = new Map(game, sizeX, sizeY, null);

		mapR.buildMapFromString(map.toString());
		return mapR;
	}
}
