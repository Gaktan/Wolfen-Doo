package engine.weapons;

import org.lwjgl.opengl.GL11;

import engine.BitMapFont;
import engine.Displayable;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.animations.AnimatedActor;
import engine.game.Player;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeInstancedSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public abstract class Weapon implements Displayable {

	enum BobbingState {
		TO_LEFT, TO_RIGHT, TO_CENTER, IDLE;

		static BobbingState randomDir() {
			return (MathUtil.random(0f, 1f) > 0.5f) ? TO_RIGHT : TO_LEFT;
		}
	}

	protected static final String TEXT_RELOADING = "Reloading...";

	protected static Vector3 POSITION_CENTER;
	protected static Vector3 POSITION_LEFT;
	protected static Vector3 POSITION_RIGHT;

	static {
		POSITION_CENTER = new Vector3(0f, -.58f, 0f);
		POSITION_LEFT = new Vector3(-0.2f, -.58f, 0f);
		POSITION_RIGHT = new Vector3(0.2f, -.58f, 0f);
	}

	protected Player player;

	// SHOOTING
	protected boolean firing;

	protected float cooldownTime;
	protected float currentCooldown;

	protected int shotsLeft;
	protected int shotsCapacity;

	// RELOADING
	protected int totalAmmo;
	protected float reloadingTime;
	protected float currentReloading;

	protected DisplayableText reloadingText;
	protected DisplayableText ammoText;
	protected BobbingState bobbingState;
	protected boolean reloading;

	// BOBBING
	protected float timeStamp;
	protected float bobbingTime;
	protected Vector3 bendingCurve;

	protected Vector3 lastKnownPosition;
	protected boolean moving;

	// WEAPON_SPRITE
	protected AnimatedActor weaponSprite;

	public Weapon(Player player) {
		this.player = player;

		currentCooldown = 0f;
		shotsLeft = shotsCapacity;
		currentReloading = reloadingTime;

		BitMapFont bmf = new BitMapFont(new ShapeInstancedSprite(ShaderProgram.getProgram("texture_camera_instanced"),
				"scumm_font.png", 128, 256, 8, 11));

		reloadingText = bmf.createString(new Vector3(0f), TEXT_RELOADING, 1f, TextPosition.CENTER);
		ammoText = bmf.createString(new Vector3(1f, -1f, 0f), "", 1f, TextPosition.RIGHT);
		updateAmmoText();

		bobbingState = BobbingState.IDLE;
		lastKnownPosition = POSITION_CENTER;
		bendingCurve = new Vector3(0, -0.2f, 0);
		timeStamp = 0f;
		moving = false;
	}

	@Override
	public void delete() {
	}

	@Override
	public void dispose() {
		reloadingText.dispose();
		ammoText.dispose();
		weaponSprite.dispose();
	}

	protected abstract void fire();

	public void forceReload() {
		if (shotsLeft > 0 && shotsLeft < shotsCapacity && totalAmmo > 0) {
			totalAmmo += shotsLeft;
			shotsLeft = 0;
		}
	}

	public boolean isFiring() {
		return firing;
	}

	@Override
	public void render() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		weaponSprite.render();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		if (reloading) {
			reloadingText.render();
		}
		ammoText.render();
	}

	public void setFiring(boolean firing) {
		this.firing = firing;
	}

	public void setMoving(boolean moving) {
		if (this.moving == moving)
			return;

		this.moving = moving;

		lastKnownPosition = new Vector3(weaponSprite.position);
		timeStamp = 0f;

		if (moving)
			bobbingState = BobbingState.randomDir();
		else
			bobbingState = BobbingState.TO_CENTER;

	}

	@Override
	public boolean update(float dt) {
		currentCooldown -= dt;

		timeStamp += dt;

		if (bobbingState == BobbingState.TO_RIGHT) {
			moveToPosition(lastKnownPosition, POSITION_RIGHT);

			if (timeStamp > bobbingTime) {
				bobbingState = BobbingState.TO_LEFT;
				lastKnownPosition.set(POSITION_RIGHT);
				weaponSprite.position.set(POSITION_RIGHT);
				timeStamp = 0f;
			}
		}
		else if (bobbingState == BobbingState.TO_LEFT) {
			moveToPosition(lastKnownPosition, POSITION_LEFT);

			if (timeStamp > bobbingTime) {
				bobbingState = BobbingState.TO_RIGHT;
				lastKnownPosition.set(POSITION_LEFT);
				weaponSprite.position.set(POSITION_LEFT);
				timeStamp = 0f;
			}
		}
		else if (bobbingState == BobbingState.TO_CENTER) {
			moveToPosition(lastKnownPosition, POSITION_CENTER);

			if (timeStamp > bobbingTime) {
				lastKnownPosition.set(POSITION_CENTER);
				weaponSprite.position.set(POSITION_CENTER);
				timeStamp = 0f;

				if (!moving)
					bobbingState = BobbingState.IDLE;
				else
					bobbingState = BobbingState.randomDir();
			}
		}

		if (shotsLeft <= 0 && currentCooldown < 0) {
			reload(dt);
		}

		else if (firing) {
			fire();
		}

		weaponSprite.update(dt);
		ammoText.update(dt);
		reloadingText.update(dt);

		return true;
	}

	protected boolean canFire() {
		if (currentCooldown > 0f)
			return false;

		if (shotsLeft <= 0) {
			return false;
		}

		shotsLeft--;
		currentCooldown = cooldownTime;
		updateAmmoText();

		bobbingState = BobbingState.TO_CENTER;

		return true;
	}

	protected void moveToPosition(Vector3 start, Vector3 goal) {
		float diff = goal.getSub(start).length();

		float percent = (timeStamp / bobbingTime);

		Vector3 currentPos = MathUtil.smoothApproach(start, goal, percent);

		Vector3 bending = new Vector3(bendingCurve);
		bending.scale(diff);

		currentPos.addX(bending.getX() * MathUtil.sin(percent * MathUtil.PI));
		currentPos.addY(bending.getY() * MathUtil.sin(percent * MathUtil.PI));
		currentPos.addZ(bending.getZ() * MathUtil.sin(percent * MathUtil.PI));

		weaponSprite.position.set(currentPos);
	}

	protected void reload(float dt) {
		if (totalAmmo == 0) {
			return;
		}

		currentReloading -= dt;

		reloading = true;

		if (currentReloading > 0f)
			return;

		currentReloading = reloadingTime;

		totalAmmo -= shotsCapacity;

		if (totalAmmo < 0) {
			shotsLeft = shotsCapacity + totalAmmo;
			totalAmmo = 0;
		}
		else {
			shotsLeft = shotsCapacity;
		}

		reloading = false;
		updateAmmoText();
	}

	protected void updateAmmoText() {
		ammoText.setText(shotsLeft + "/" + totalAmmo);
	}

	protected void setCooldownTime(float cooldown) {
		cooldownTime = cooldown;
	}

	protected void setShotsCapacity(int shotsCapacity) {
		this.shotsCapacity = shotsCapacity;
		shotsLeft = shotsCapacity;
	}

	protected void setReloadingTime(float reloadingTime) {
		this.reloadingTime = reloadingTime;
		currentReloading = reloadingTime;
	}

	public void addAmmo(int ammo) {
		totalAmmo += ammo;
		updateAmmoText();
	}

	protected void setBobbingTime(float bobbingTime) {
		this.bobbingTime = bobbingTime;
	}
}
