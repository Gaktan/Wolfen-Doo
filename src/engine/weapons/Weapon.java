package engine.weapons;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;
import engine.DisplayableText;
import engine.entities.Camera;
import engine.game.GameWolfen;

public abstract class Weapon implements Displayable{

	protected Camera camera;

	protected float cooldownTime;
	protected float currentCooldown;

	protected int shotsLeft;
	protected int shotsCapacity;
	protected int totalAmmo;

	protected float reloadingTime;
	protected float currentReloading;

	protected DisplayableText reloadingText;
	protected DisplayableText ammoText;

	public Weapon(Camera camera, float coolDownTime, int shotsCapacity, float reloadingTime, int totalAmmo) {
		this.camera = camera;

		this.cooldownTime = coolDownTime;
		this.shotsCapacity = shotsCapacity;
		this.reloadingTime = reloadingTime;
		this.totalAmmo = totalAmmo;

		currentCooldown = 0f;
		shotsLeft = shotsCapacity;
		currentReloading = reloadingTime;

		String text = "reloading...";
		float size = (text.length()+0.5f) * 0.5f * -0.05f - 0.1f;

		reloadingText = GameWolfen.getInstance().bmf.createString(new Vector3f(size, 0, 0), "", false);
		ammoText = GameWolfen.getInstance().bmf.createString(new Vector3f(0.6f, -0.95f, 0), "", false);
		updateAmmoText();
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

		if (shotsLeft <= 0 && currentCooldown < 0) {
			reload(dt);
		}

		ammoText.update(dt);
		reloadingText.update(dt);
		return true;
	}

	public void render(Camera camera) {
		reloadingText.render(camera);
		ammoText.render(camera);
	}
	
	@Override
	public int size() {
		return reloadingText.size() + ammoText.size();
	}
}
