package engine.shapes;

import java.nio.FloatBuffer;

public interface Instantiable {
	
	public void render(int amount);
	
	public void setData(FloatBuffer data);

}
