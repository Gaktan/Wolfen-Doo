package engine.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.util.MatrixUtil;

/**
 * Draws a line
 * @author Gaktan
 */
public class EntityLine extends Entity {

	protected Vector3f positionB;

	public EntityLine(Vector3f position, Vector3f positionB) {
		this.position = position;
		this.positionB = positionB;
	}

	@Override
	public void render(Camera camera) {
		GL11.glPushMatrix();

		GL11.glLoadMatrix(MatrixUtil.toFloatBuffer(camera.getProjectionXview()));

		GL11.glLineWidth(2.5f);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(position.x, position.y, position.z);
			GL11.glColor3f(0.0f, 1.0f, 0.0f);
			GL11.glVertex3f(positionB.x, positionB.y, positionB.z);
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}
}
