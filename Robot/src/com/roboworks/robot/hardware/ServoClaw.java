package com.roboworks.robot.hardware;

import TI.Servo;
import TI.Timer;
import com.roboworks.robot.hardware.interfaces.Claw;
import com.roboworks.robot.util.Config;

public class ServoClaw implements Claw {
	private final Servo servo;
	private int targetState;
	private Timer adjustTimer;
	private int SPEED_STEP = 10;

	/* TODO callback for when open/close completes? */
	public ServoClaw(int pin) {
		this.servo = new Servo(pin);
		servo.update(Config.CLAW_CLOSED);
		targetState = servo.getPulseWidth();
		adjustTimer = new Timer(0);
	}

	@Override
	public boolean isClosed() {
		return servo.getPulseWidth() == Config.CLAW_CLOSED;
	}

	@Override
	public void open(int milliseconds) {
		if (milliseconds == 0) {
			targetState = Config.CLAW_OPEN;
			servo.update(Config.CLAW_OPEN);
			return;
		}
		int diff = Math.abs(Config.CLAW_OPEN - servo.getPulseWidth());
		int newTargetState = Config.CLAW_OPEN;
		if (diff == 0 || targetState == newTargetState) return;
		targetState = newTargetState;
		adjustTimer.setInterval(milliseconds / diff * SPEED_STEP);
	}

	@Override
	public void close(int milliseconds) {
		if (milliseconds == 0) {
			targetState = Config.CLAW_CLOSED;
			servo.update(Config.CLAW_CLOSED);
			return;
		}
		int diff = Math.abs(Config.CLAW_CLOSED - servo.getPulseWidth());
		int newTargetState = Config.CLAW_CLOSED;
		if (diff == 0 || targetState == newTargetState) return;
		targetState = newTargetState;
		adjustTimer.setInterval(milliseconds / diff * SPEED_STEP);
	}


	@Override
	public void update() {
		int state = servo.getPulseWidth();
		if (adjustTimer.timeout() && state != targetState) {
			int diff = Math.abs(state - targetState);
			if (SPEED_STEP < diff) diff = SPEED_STEP;
			servo.update(state + (state > targetState ? -diff : diff));
			state = servo.getPulseWidth();
		}
	}
}
