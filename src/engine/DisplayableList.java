package engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Acts as an ArrayList for Displayables
 * @author Gaktan
 */
public class DisplayableList implements Displayable, Iterable<Displayable> {
	
	public List<Displayable> list;
	private List<Displayable> toBeAdded;
	private List<Displayable> toBeDeleted;
	private boolean delete = false;
	
	public DisplayableList() {
		list = new ArrayList<Displayable>();
		toBeAdded = new ArrayList<Displayable>();
		toBeDeleted = new ArrayList<Displayable>();
	}
	
	/**
	 * Adds a displayable to the list
	 * @param d Displayable to add
	 */
	public void add(Displayable d) {
		toBeAdded.add(d);
	}
	
	/**
	 * Removes a displayable from the list
	 * @param d Displayable to remove
	 */
	public void remove(Displayable d) {
		toBeDeleted.add(d);
	}

	@Override
	public boolean update(float dt) {

		for (Displayable d : toBeAdded) {
			list.add(d);
		}
		
		toBeAdded.clear();
		
		for (Displayable d : list) {
			boolean b = d.update(dt);
			if (!b)
				toBeDeleted.add(d);
		}
		
		for (Displayable d : toBeDeleted) {
			list.remove(d);
		}
		
		toBeDeleted.clear();
		
		return !delete;
	}

	@Override
	public void render() {
		for (Displayable d : list) {
			d.render();
		}
	}

	@Override
	public void delete() {
		delete = true;
	}
	
	public int size() {
		int size = 0;
		
		for (Displayable d : list) {
			size += d.size();
		}
		
		return size;
	}

	@Override
	public Iterator<Displayable> iterator() {
		return list.iterator();
	}

}
