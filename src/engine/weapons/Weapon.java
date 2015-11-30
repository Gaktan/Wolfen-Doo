package engine.weapons;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.game.GameWolfen;
import engine.util.MathUtil;

public abstract class Weapon implements Displayable{

	protected Camera camera;

	// SHOOTING --

	protected float cooldownTime;
	protected float currentCooldown;

	protected int shotsLeft;
	protected int shotsCapacity;
	protected int totalAmmo;

	// RELOADING --

	protected float reloadingTime;
	protected float currentReloading;

	protected DisplayableText reloadingText;
	protected DisplayableText ammoText;

	// BOBBING --
	protected static Vector3f POSITION_CENTER;
	protected static Vector3f POSITION_LEFT;
	protected static Vector3f POSITION_RIGHT;

	protected BobbingState bobbingState;
	protected float timeStamp;
	protected float bobbingTime;
	
	protected Vector3f bendingCurve;
	protected Vector3f lastKnownPosition;

	// 0f, -.25f
	// TODO: change this
	static {
		POSITION_CENTER = new Vector3f(0f, -.575f, 0f);
		POSITION_LEFT = new Vector3f(-0.2f, -.575f, 0f);
		POSITION_RIGHT = new Vector3f(0.2f, -.575f, 0f);
	}

	enum BobbingState {
		TO_LEFT,
		TO_RIGHT,
		TO_CENTER,
		IDLE;
		
		static BobbingState randomDir() {
			return ((int) (Math.random()+0.5) == 1) ? TO_RIGHT : TO_LEFT;
		}
	}

	protected boolean moving;

	// WEAPON_SPRITE
	protected AnimatedActor weaponSprite;

	public Weapon(Camera camera, float coolDownTime, int shotsCapacity, float reloadingTime, int totalAmmo, float bobbingTime) {
		this.camera = camera;

		this.cooldownTime = coolDownTime;
		this.shotsCapacity = shotsCapacity;
		this.reloadingTime = reloadingTime;
		this.totalAmmo = totalAmmo;
		this.bobbingTime = bobbingTime;

		currentCooldown = 0f;
		shotsLeft = shotsCapacity;
		currentReloading = reloadingTime;

		reloadingText = GameWolfen.getInstance().bmf.createString(new Vector3f(0, 0, 0), "", 1f, TextPosition.CENTER);
		ammoText = GameWolfen.getInstance().bmf.createString(new Vector3f(1f, -1f, 0), "", 1f, TextPosition.RIGHT);
		updateAmmoText();

		bobbingState = BobbingState.IDLE;
		lastKnownPosition = POSITION_CENTER;
		bendingCurve = new Vector3f(0, -0.2f, 0);
		timeStamp = 0f;
		moving = false;
	}

	public abstract void fire();

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

	public void forceReload() {
		if (shotsLeft > 0 && shotsLeft < shotsCapacity && totalAmmo > 0) {
			totalAmmo += shotsLeft;
			shotsLeft = 0;
		}
	}

	protected void reload(float dt) {
		if (totalAmmo == 0) {
			reloadingText.setText("no ammo...");
			return;
		}

		currentReloading -= dt;

		reloadingText.setText("reloading...");

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

		reloadingText.setText("");	
		updateAmmoText();
	}

	protected void updateAmmoText() {
		ammoText.setText(shotsLeft + "/" + totalAmmo);
	}

	public boolean update(float dt) {
		currentCooldown -= dt;

		timeStamp += dt;

		if (bobbingState == BobbingState.TO_RIGHT) {
			moveToPosition(lastKnownPosition, POSITION_RIGHT);

			if (timeStamp > bobbingTime) {
				bobbingState = BobbingState.TO_LEFT;
				lastKnownPosition = POSITION_RIGHT;
				timeStamp = 0f;
			}
		}
		else if (bobbingState == BobbingState.TO_LEFT) {
			moveToPosition(lastKnownPosition, POSITION_LEFT);

			if (timeStamp > bobbingTime) {
				bobbingState = BobbingState.TO_RIGHT;
				lastKnownPosition = POSITION_LEFT;
				timeStamp = 0f;
			}
		}
		else if (bobbingState == BobbingState.TO_CENTER) {
			moveToPosition(lastKnownPosition, POSITION_CENTER);

			if (timeStamp > bobbingTime) {
				lastKnownPosition = POSITION_CENTER;
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

		weaponSprite.update(dt);
		ammoText.update(dt);
		reloadingText.update(dt);
		return true;
	}

	public void setMoving(boolean moving) {

		if (this.moving == moving)
			return;
		
		this.moving = moving;
		
		lastKnownPosition = new Vector3f(weaponSprite.position);
		timeStamp = 0f;

		if (moving)
			bobbingState = BobbingState.randomDir();
		else
			bobbingState = BobbingState.TO_CENTER;

	}

	protected void moveToPosition(Vector3f start, Vector3f goal) {
		Vector3f vDiff = new Vector3f();
		Vector3f.sub(goal, start, vDiff);
		float diff = vDiff.length();

		Vector3f currentPos = MathUtil.approach(goal, start, (timeStamp / bobbingTime) * diff);

		Vector3f bending = new Vector3f(bendingCurve);
		bending.scale(diff);

		currentPos.x += bending.x * Math.sin((timeStamp / bobbingTime) * Math.PI);
		currentPos.y += bending.y * Math.sin((timeStamp / bobbingTime) * Math.PI);
		currentPos.z += bending.z * Math.sin((timeStamp / bobbingTime) * Math.PI);

		weaponSprite.position.set(currentPos);
	}

	@Override
	public void render() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		weaponSprite.render();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		reloadingText.render();
		ammoText.render();
	}

	@Override
	public int size() {
		return reloadingText.size() + ammoText.size() + weaponSprite.size();
	}

	@Override
	public void delete() {}
}
