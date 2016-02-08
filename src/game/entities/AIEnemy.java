package game.entities;

import engine.entities.Entity;
import engine.game.Player;
import engine.game.states.GameStateManager;
import engine.shapes.ShapeSprite;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.game.states.WolfenGameState;
import game.generator.Map;

public class AIEnemy extends EntityAI {

	public class StateWalking extends AIState {

		protected float idleDelay;
		protected float idleTimer;

		@Override
		public void start(float dt) {
			Vector3 goal = new Vector3(position);
			goal.addX(MathUtil.random(-2f, 2f));
			goal.addZ(MathUtil.random(-2f, 2f));
			setDestination(goal, false);

			setOnArrival(new OnArrival() {
				@Override
				public void onArrival() {
					setDone();
				}
			});
		}

		@Override
		public void progress(float dt) {
			lookForPlayer();
		}

		@Override
		public void done(float dt) {
			idleTimer = 0f;
			idleDelay = MathUtil.random(1000f, 3000f);
		}

		@Override
		public void idle(float dt) {
			idleTimer += dt;

			lookForPlayer();

			if (idleTimer > idleDelay) {
				setStarting();
			}
		}

		public void lookForPlayer() {
			Vector3 playerAngle = player.getViewAngle().toVector();

			float dot = playerAngle.dot(lookingDirection);

			if (dot < -0.5f) {
				Vector3 ray = position.getSub(player.position);
				Entity e = map.rayCast(player.position, ray, 10f);

				if (e == AIEnemy.this) {
					setState(new StateAttack());
				}
			}
		}
	}

	public class StateAttack extends AIState {

		protected static final float DISTANCE_TO_SHOOT = 3f;
		protected static final float DISTANCE_TO_FORGET = 10f;

		protected static final float WEAPON_COOLDOWN = 700f;
		protected float weaponTimer;

		@Override
		public void start(float dt) {
			Vector3 goal = new Vector3(player.position);
			setDestination(goal, true);

			setOnArrival(new OnArrival() {
				@Override
				public void onArrival() {
					setDone();
				}
			});
		}

		@Override
		public void progress(float dt) {
			float distance = position.getDistance(player.position);
			if (distance < DISTANCE_TO_SHOOT) {
				path.clear();
			}
			if (distance > DISTANCE_TO_FORGET) {
				path.clear();
				setState(new StateWalking());
			}
		}

		@Override
		public void done(float dt) {
		}

		@Override
		public void idle(float dt) {
			lookingDirection.set(player.position.getSub(position));

			weaponTimer += dt;

			if (weaponTimer > WEAPON_COOLDOWN) {
				weaponTimer = 0f;

				Vector3 linePosition = new Vector3(position);
				linePosition.addY(MathUtil.random(-0.2f, 0f));

				EAngle direction = new EAngle();
				direction.lookAt(position, player.position);
				direction.yaw += MathUtil.random(-10f, 10f) + 90f;
				direction.pitch += MathUtil.random(-8f, 8f);

				Vector3 lineVector = direction.toVector().getNegate();
				lineVector.normalize();

				linePosition.add(lineVector.getScale(0.5f));

				EntityProjectile projectile = new EntityProjectile(linePosition, lineVector,
						((WolfenGameState) GameStateManager.getCurrentGameState()).getMap());
				((WolfenGameState) GameStateManager.getCurrentGameState()).add(projectile);
			}

			float distance = position.getDistance(player.position);
			if (distance > DISTANCE_TO_SHOOT) {
				setStarting();
			}
		}
	}

	protected AIState ai_state;
	protected Player player;

	public AIEnemy(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation, map);
		player = ((WolfenGameState) GameStateManager.getCurrentGameState()).getPlayer();
		setState(new StateWalking());
	}

	@Override
	public boolean update(float dt) {
		ai_state.update(dt);
		return super.update(dt);
	}

	public AIState getState() {
		return ai_state;
	}

	public void setState(AIState ai_state) {
		this.ai_state = ai_state;
	}
}
