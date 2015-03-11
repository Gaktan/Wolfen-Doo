package engine;

import java.util.ArrayList;
import java.util.List;

import engine.entities.Camera;

public class DisplayableList implements Displayable{
	
	public List<Displayable> list;
	private boolean delete = false;
	
	public DisplayableList() {
		list = new ArrayList<Displayable>();
	}
	
	public void add(Displayable d){
		list.add(d);
	}
	
	public void remove(Displayable d){
		list.remove(d);
	}

	@Override
	public boolean update(float dt) {
		
		List<Displayable> deleteList = new ArrayList<Displayable>();
		
		for(Displayable d : list){
			boolean b = d.update(dt);
			if(!b)
				deleteList.add(d);
		}
		
		for(Displayable d : deleteList){
			list.remove(d);
		}
		
		return !delete;
	}

	@Override
	public void render(Camera camera) {
		for(Displayable d : list){
			d.render(camera);
		}
	}

	@Override
	public void delete() {
		delete = true;
	}

}
