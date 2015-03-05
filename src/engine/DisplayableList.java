package engine;

import java.util.ArrayList;
import java.util.List;

import engine.entities.Camera;

public class DisplayableList implements Displayable{
	
	private List<Displayable> list;
	
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
	public void update(float dt) {
		for(Displayable d : list){
			d.update(dt);
		}
	}

	@Override
	public void render(Camera camera) {
		for(Displayable d : list){
			d.render(camera);
		}
	}

}
