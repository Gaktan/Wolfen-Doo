package game.entities;

public abstract class AIState {

	protected boolean starting = true;
	protected boolean inProgress = false;
	protected boolean done = false;

	public void update(float dt) {
		if (isStarting()) {
			start(dt);
			nextState();
		}
		else if (isInProgress()) {
			progress(dt);
		}
		else if (isDone()) {
			done(dt);
			nextState();
		}
		else {
			idle(dt);
		}
	}

	public abstract void start(float dt);

	public abstract void progress(float dt);

	public abstract void done(float dt);

	public abstract void idle(float dt);

	public void nextState() {
		if (isStarting()) {
			setInProgress();
		}
		else if (isInProgress()) {
			setDone();
		}
		else if (isDone()) {
			setIdle();
		}
	}

	public boolean isStarting() {
		return starting;
	}

	public void setStarting() {
		starting = true;
		inProgress = false;
		done = false;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress() {
		starting = false;
		inProgress = true;
		done = false;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone() {
		starting = false;
		inProgress = false;
		done = true;
	}

	public void setIdle() {
		starting = false;
		inProgress = false;
		done = false;
	}

}