package engine.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.util.MatrixUtil;

/**
 * Draws a line
 * @author Gaktan
 */
public class EntityLine extends Entity {

	protected Vector3f positionB;
	protected Color colorA;
	protected Color colorB;
	
	public EntityLine(Vector3f position, Vector3f positionB, Color colorA, Color colorB) {
		this.position = position;
		this.positionB = positionB;
		
		this.colorA = colorA;
		this.colorB = colorB;
	}

	public EntityLine(Vector3f position, Vector3f positionB) {
		this(position, positionB, new Color(0xff0000), new Color(0xff));
	}

	@Override
	public void render(Camera camera) {
		GL11.glPushMatrix();

		GL11.glLoadMatrix(MatrixUtil.toFloatBuffer(camera.getProjectionXview()));

		GL11.glLineWidth(2.5f);
		GL11.glColor3f(colorA.r, colorA.g, colorA.b);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(position.x, position.y, position.z);
			GL11.glColor3f(colorB.r, colorB.g, colorB.b);
			GL11.glVertex3f(positionB.x, positionB.y, positionB.z);
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}
}
