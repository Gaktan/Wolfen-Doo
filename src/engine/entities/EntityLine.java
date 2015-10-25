package engine.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.MatrixUtil;

public class EntityLine extends Entity {

	Vector3f direction;
	
	public EntityLine(Vector3f position, Vector3f direction) {
		this.position = new Vector3f(position);
		this.direction = new Vector3f(direction);
	}
	
	@Override
	public void render(Camera camera) {
		// TODO Auto-generated method stub

		GL11.glPushMatrix();
		
		GL11.glLoadMatrix(MatrixUtil.toFloatBuffer(camera.getProjectionXview()));
		
		GL11.glLineWidth(2.5f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(position.x, position.y, position.z);
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex3f(position.x + direction.x, position.y + direction.y, position.z + direction.z);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}

}
