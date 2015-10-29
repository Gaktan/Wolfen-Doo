package engine;

import engine.entities.Camera;

public class DisplayableArray2D implements Displayable{

	public Displayable[][] list;
	private int sizeX;
	private int sizeY;
	private boolean delete = false;

	public DisplayableArray2D(int sizeX, int sizeY) {
		list = new Displayable[sizeX][sizeY];
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	public boolean update(float dt) {
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeY; j++){
				if(list[i][j] != null){
					
					boolean b = list[i][j].update(dt);
					
					if(!b){
						list[i][j] = null;
					}
				}
			}
		}
		
		return !delete;
	}

	@Override
	public void render(Camera camera) {
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeY; j++){
				if(list[i][j] != null)
					list[i][j].render(camera);
			}
		}
	}

	public void add(Displayable d, int x, int y){
		if(!(x < sizeX && x >= 0))
			return;
		if(!(y < sizeY && y >= 0))
			return;

		list[x][y] = d;
	}
	
	public Displayable get(int x, int y){
		if(x < 0 || y < 0 || x > list.length -1 || y > list[0].length -1)
			return null;
		return list[x][y];
	}
	
	public void delete(){
		delete = true;
	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeY; j++){
				if(list[i][j] != null){
					sb.append("O,");
				}
				else
					sb.append(" ,");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public int size()
	{
		int size = 0;
		for(Displayable[] d2 : list)
		{
			for (Displayable d : d2)
			{
				if(d != null)
					size += d.size();
			}
		}
		return size;
	}
}
