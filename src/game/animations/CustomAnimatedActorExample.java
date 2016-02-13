package game.animations;

import engine.entities.AABB;
import engine.entities.AABBRectangle;
import engine.entities.DisplayableList;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.shapes.ShapeSprite;
import engine.util.Vector3;
import game.entities.EntityAI;
import game.entities.EntityBubble;
import game.entities.EntityBubble.BubblePosition;
import game.entities.EntityBubble.OnSpeechDone;
import game.generator.Map;

public class CustomAnimatedActorExample extends EntityAI {

	protected ShapeQuadTexture bubbleShape;
	protected DisplayableList<EntityBubble> bubbles;

	public CustomAnimatedActorExample(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation, map);

		bubbleShape = new ShapeQuadTexture(ShaderProgram.getProgram("texture_billboard"), "speech_bubble.png");
		bubbles = new DisplayableList<EntityBubble>();

		position.setX(3f);
		position.setZ(5f);

		setDestination(new Vector3(22f, 0f, 10f), true);
		setOnArrival(new OnArrival() {
			@Override
			public void onArrival() {
				__talk("I have arrived!\n\nYay!", 5000f, BubblePosition.BOTTOM_LEFT, null);
			}
		});

		__talk("Hello friend!\n\nHow are you doing?", 9000f, BubblePosition.TOP_LEFT, new OnSpeechDone() {
			@Override
			public void onSpeechDone() {
				__talk("BLBLBLB", 5000f, BubblePosition.BOTTOM_RIGHT, null);
			}
		});
	}

	@Override
	public void dispose() {
		bubbleShape.dispose();
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
		bubbles.render();
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);

		bubbles.update(dt);

		// Collision
		/*
		AABB aabb = getAABB();
		map.resolveCollision(aabb);
		position.set(aabb.position);
		*/

		return result;
	}

	public void __talk(String text, float timer, BubblePosition bubblePosition, OnSpeechDone onSpeechDone) {
		EntityBubble bubbleActor = new EntityBubble(bubbleShape, position, text, timer, bubblePosition);
		bubbleActor.scale.setY(0.5f);
		bubbleActor.setOnSpeechDone(onSpeechDone);
		bubbles.add(bubbleActor);
	}

	@Override
	public AABB getAABB() {
		AABBRectangle r = new AABBRectangle(new Vector3(position), new Vector3(scale));
		r.scale.scale(0.01f);
		r.scale.setY(1f);
		return r;
	}
}
