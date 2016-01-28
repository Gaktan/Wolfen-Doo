package game.entities;

import org.lwjgl.opengl.GL11;

import engine.entities.BitMapFont;
import engine.entities.DisplayableText;
import engine.entities.DisplayableText.TextPosition;
import engine.entities.EntityActor;
import engine.game.FrameBuffer.RenderLast;
import engine.game.states.GameStateManager;
import engine.shapes.ShaderProgram;
import engine.shapes.Shape;
import engine.shapes.ShapeInstancedSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.game.states.WolfenGameState;

public class EntityBubble extends EntityActor {

	public enum BubblePosition {
		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
	}

	public interface OnSpeechDone {
		public void onSpeechDone();
	}

	protected BitMapFont font;
	protected DisplayableText text;
	protected Vector3 target;
	protected final float timer;
	protected float timestamp;
	protected BubblePosition bubblePosition;
	protected OnSpeechDone onSpeechDone;

	public EntityBubble(Shape shape, Vector3 target, String text, float timer, BubblePosition bubblePosition) {
		super(shape);
		this.target = target;
		this.timer = timer;
		this.bubblePosition = bubblePosition;

		timestamp = 0f;

		font = new BitMapFont(new ShapeInstancedSprite(ShaderProgram.getProgram("texture_instanced"), "scumm_font.png",
				128, 256, 8, 11));

		this.text = font.createString(position, text, 0.5f, TextPosition.CENTER);
	}

	@Override
	public void dispose() {
		super.dispose();
		text.dispose();
		font.dispose();
	}

	@Override
	public void render() {
		float distance = GameStateManager.getCurrentGameState().current_camera.position.getSub(position).length();

		if (distance < 4f) {
			((WolfenGameState) GameStateManager.getCurrentGameState()).frameBuffer.addRenderLast(new RenderLast() {
				@Override
				public void renderLast() {
					GL11.glDisable(GL11.GL_CULL_FACE);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					EntityBubble.super.render();
					text.render();
					GL11.glEnable(GL11.GL_CULL_FACE);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
				}
			});
		}
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);

		timestamp += dt;

		if (timestamp > timer) {
			if (onSpeechDone != null) {
				onSpeechDone.onSpeechDone();
			}
			return false;
		}

		Vector3 vec = GameStateManager.getCurrentGameState().current_camera.getCorrectedViewAngle().toVector();
		vec.cross(new Vector3(0f, 1f, 0f));
		vec.normalize();

		float angle = MathUtil.atan2(vec.getZ(), vec.getX());
		angle = -MathUtil.toDegrees(angle);
		text.setRotation(angle);
		text.updateText();
		text.update(dt);

		float scale = 0.75f;
		if (bubblePosition == BubblePosition.BOTTOM_LEFT || bubblePosition == BubblePosition.TOP_LEFT) {
			scale = -0.75f;
			this.scale.setX(MathUtil.getNegative(this.scale.getX()));
		}
		else {
			this.scale.setX(MathUtil.getPositive(this.scale.getX()));
		}
		vec.scale(scale);

		float _y = 0.375f;
		if (bubblePosition == BubblePosition.BOTTOM_LEFT || bubblePosition == BubblePosition.BOTTOM_RIGHT) {
			_y = -0.1f;
			this.scale.setY(MathUtil.getPositive(this.scale.getY()));
		}
		else {
			this.scale.setY(MathUtil.getNegative(this.scale.getY()));
		}
		vec.addY(_y);

		position.set(target.getAdd(vec));

		return result;
	}

	public void setOnSpeechDone(OnSpeechDone onSpeechDone) {
		this.onSpeechDone = onSpeechDone;
	}
}
