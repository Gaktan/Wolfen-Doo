package engine.entities;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import engine.game.GameWolfen;
import engine.util.Vector3;

/**
 * Draws a line
 *
 * @author Gaktan
 */
public class EntityLine extends Entity {

	public Vector3 positionB;
	protected Color colorA;
	protected Color colorB;

	public EntityLine(Vector3 position, Vector3 positionB) {
		this(position, positionB, new Color(0xff0000), new Color(0xff));
	}

	public EntityLine(Vector3 position, Vector3 positionB, Color colorA, Color colorB) {
		this.position = position;
		this.positionB = positionB;

		this.colorA = colorA;
		this.colorB = colorB;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();

		GL11.glLoadMatrix(GameWolfen.getInstance().current_camera.getProjectionXview().toFloatBuffer());

		GL11.glLineWidth(2.5f);
		GL11.glColor3f(colorA.r, colorA.g, colorA.b);
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex3f(position.getX(), position.getY(), position.getZ());
			GL11.glColor3f(colorB.r, colorB.g, colorB.b);
			GL11.glVertex3f(positionB.getX(), positionB.getY(), positionB.getZ());
		}
		GL11.glEnd();

		GL11.glPopMatrix();
	}
}
