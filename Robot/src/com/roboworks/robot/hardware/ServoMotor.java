package com.roboworks.robot.hardware;

import TI.Servo;
import TI.Timer;
import com.roboworks.robot.hardware.interfaces.Motor;

/**
 * Implementation of Motor using Servos.
 */
public class ServoMotor implements Motor {
	private Servo left;
	private Servo right;

	/* Speed to target in update method. */
	private int targetSpeedLeft;
	private int targetSpeedRight;

	/* To go back to old speed after turning. */
	private int oldSpeedLeft;
	private int oldSpeedRight;
	private boolean turning = false;
	/* How much to adjust speed at each timer interval. */
	private final int SPEED_STEP = 5;
	private Timer adjustTimer;

	/* Pulsewidth at which the servo is stopped. */
	private static final int STOPPED = 1500;
	/* Internal max speed. */
	private static final int MAX = 200;

	/* Non normalized speed at which to turn, can't be greater than MAX / 2 */
	private static final int TURN_SPEED = 30;
	/* Wait time to turn a single degree when turning at TURN_SPEED */
	private static final double TURN_WAIT = 477.78;
	private Timer turnTimer;

	/* Used to convert between user input speed and internal speed. */
	private static final int NORMALIZE = MAX / 100; /* 100 is max speed user can set. */

	private State state;

	/**
	 * Construct a ServoMotor which uses the specified pins.
	 *
	 * @param pinLeft  Pin connected to the left servo.
	 * @param pinRight Pin connected to the right servo.
	 */
	public ServoMotor(int pinLeft, int pinRight) {
		this.left = new Servo(pinLeft);
		this.right = new Servo(pinRight);
		adjustTimer = new Timer(0);
		turnTimer = new Timer(0);
		setSpeed(0);
	}

	@Override
	public void setSpeed(int speed) {
		if (speed > 100 || speed < -100) throw new IllegalArgumentException();

		targetSpeedLeft = speed * NORMALIZE;
		targetSpeedRight = speed * NORMALIZE;

		setSpeedLeft(targetSpeedLeft);
		setSpeedRight(targetSpeedRight);
	}

	@Override
	public void gotoSpeed(int speed, int milliseconds) {
		if (speed > 100 || speed < -100) throw new IllegalArgumentException();
		int targetSpeed = speed * NORMALIZE;

		adjustTimer(milliseconds, targetSpeed, targetSpeed);
		targetSpeedLeft = targetSpeedRight = targetSpeed;
	}

	@Override
	public void gotoSpeed(int speed) {
		gotoSpeed(speed, 1000);

	}

	@Override
	public void turn(int degrees) {
		turn(TURN_SPEED / NORMALIZE, degrees);
		turnTimer.setInterval((int) (TURN_WAIT * Math.abs(degrees) / TURN_SPEED));
	}

	@Override
	public void turn(int speed, int direction) {
		turn(speed, direction, 0);
		if (true) return;
	}

	@Override
	public void turn(int speed, int direction, int milliseconds) {
		int newTargetLeft, newTargetRight;

		if (getSpeedLeft() == getSpeedRight()) {
			oldSpeedLeft = targetSpeedLeft;
			oldSpeedRight = targetSpeedRight;
		}
		if (direction > 0) {
			/* TODO correctly calculate relative speed */
			newTargetLeft = oldSpeedLeft + speed * NORMALIZE;
			newTargetRight = oldSpeedRight - speed * NORMALIZE;
		} else {
			newTargetLeft = oldSpeedLeft - speed * NORMALIZE;
			newTargetRight = oldSpeedRight + speed * NORMALIZE;
		}

		if (milliseconds == 0) {
			setSpeedLeft(newTargetLeft);
			setSpeedRight(newTargetRight);
		} else {
			adjustTimer(milliseconds, newTargetLeft, newTargetRight);
		}


		targetSpeedLeft = newTargetLeft;
		targetSpeedRight = newTargetRight;

	}

	private void adjustTimer(int milliseconds, int newTargetLeft, int newTargerRight) {
		int diffL = Math.abs(getSpeedLeft() - newTargetLeft);
		int diffR = Math.abs(getSpeedRight() - newTargerRight);
		int diffMax = Math.max(diffL, diffR);

		if (diffMax == 0 || targetSpeedRight == newTargerRight || targetSpeedLeft == newTargetLeft) return;
		adjustTimer.setInterval(milliseconds / diffMax * SPEED_STEP);
	}

	@Override
	public void stop(int milliseconds) {
		gotoSpeed(0, milliseconds);
	}

	@Override
	public void stop() {
		gotoSpeed(0);
	}

	@Override
	public void emergencyStop() {
		setSpeed(0);
	}

	/**
	 * Get speed of left servo.
	 *
	 * @return Non normalized speed.
	 */
	private int getSpeedLeft() {
		return left.getPulseWidth() - STOPPED;
	}

	/**
	 * Get speed of right servo.
	 *
	 * @return Non normalized speed.
	 */
	private int getSpeedRight() {
		return -(right.getPulseWidth() - STOPPED);
	}

	/**
	 * Set speed of left servo.
	 *
	 * @param speed Non normalized speed.
	 */
	private void setSpeedLeft(int speed) {
		left.update(STOPPED + speed);
	}

	/**
	 * Set speed of right servo.
	 *
	 * @param speed Non normalized speed.
	 */
	private void setSpeedRight(int speed) {
		right.update(STOPPED - speed);
	}

	@Override
	public int getSpeed() {
		int speedL = Math.abs(STOPPED - left.getPulseWidth());
		int speedR = Math.abs(STOPPED - right.getPulseWidth());

		return (speedL + speedR) / (2 * NORMALIZE);
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void update() {
		int speedLeft = getSpeedLeft();
		int speedRight = getSpeedRight();

		if (turnTimer.timeout() && turning) {
			turn(0, 0);
		}

		if (adjustTimer.timeout()) {
			if (speedLeft != targetSpeedLeft) {
				int diff = Math.abs(speedLeft - targetSpeedLeft);
				if (SPEED_STEP < diff) diff = SPEED_STEP;
				setSpeedLeft(speedLeft + (speedLeft > targetSpeedLeft ? -diff : diff));
			}
			if (speedRight != targetSpeedRight) {
				int diff = Math.abs(speedRight - targetSpeedRight);
				if (SPEED_STEP < diff) diff = SPEED_STEP;
				setSpeedRight(speedRight + (speedRight > targetSpeedRight ? -diff : diff));
			}
		}

		if (targetSpeedLeft == 0 && targetSpeedRight == 0 && (speedLeft != 0 || speedRight != 0))
			state = State.STOPPING;
		else if (speedLeft == 0 && speedRight == 0) state = State.STOPPED;
		else if (speedLeft != speedRight) state = State.TURNING;
		else if (speedLeft == speedRight) state = State.RUNNING;
	}
}
