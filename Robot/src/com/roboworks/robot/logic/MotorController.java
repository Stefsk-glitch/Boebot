package com.roboworks.robot.logic;

import TI.Timer;
import com.roboworks.robot.hardware.FlagMotor;
import com.roboworks.robot.hardware.UltrasonicSensor;
import com.roboworks.robot.hardware.interfaces.Motor;
import com.roboworks.robot.hardware.interfaces.callbacks.UltrasonicCallback;
import com.roboworks.robot.logic.remote.RemoteSignal.Signal;
import com.roboworks.robot.util.Updatable;

public class MotorController implements Updatable, LineControllerCallback, UltrasonicCallback {

	private Motor motor;
	private FlagMotor flagMotor;
	private boolean collision;
	private NotificationController notificationController;
	private Timer collisionTimer;
	private boolean emergencyStopped;
	private boolean isStopped;
	private MotorControllerCallback callback;


	private Mode mode;
	private State state;
	private LineSignal line;
	private int speed = 30;

	public MotorController(Motor motor, NotificationController notificationController, FlagMotor flagMotor, MotorControllerCallback callback) {
		if (motor == null) throw new IllegalArgumentException("Motor cannot be null");

		this.flagMotor = flagMotor;
		this.motor = motor;
		this.notificationController = notificationController;
		collisionTimer = new Timer(5000);
		this.collision = false;
		mode = Mode.GRID;
		state = State.NONE;
		emergencyStopped = false;
		isStopped = false;
		this.callback = callback;
	}

	@Override
	public void onUltrasonicSignal(UltrasonicSensor src, int distance) {
		if (distance < 30 && distance > 0) {
			if (!collision) {
				collision = true;
				notificationController.onCollision();
				if (motor.getState() != Motor.State.TURNING && motor.getState() != Motor.State.STOPPING) {
					if (distance < 15) motor.emergencyStop();
					else motor.stop();
					collisionTimer.mark();
				}
			}
		} else {
			collision = false;
			notificationController.onNormal();
		}
	}

	public void emergencyStop() {
		emergencyStopped = true;
	}

	public void emergencyResume() {
		emergencyStopped = false;
		isStopped = false;
	}

	@Override
	public void update() {
		if (emergencyStopped) {
			motor.emergencyStop();
			if (!isStopped) {
				flagMotor.turn();
				isStopped = true;
			}
			return;
		}

		switch (mode) {
			case GRID:
				grid();
				break;
			case LINE:
				line();
				break;
			case REMOTE:
				remote();
				break;
			case MANUAL:
				manual();
		}
	}

	private void grid() {
		switch (state) {
			case TURN_BACKWARD:
				/* fallthrough */
			case LEFT:
				callback.moveCompleted();
				motor.turn(15, -1);
				break;
			case RIGHT:
				callback.moveCompleted();
				motor.turn(15, 1);
				break;
			case BACKWARD:
				callback.moveCompleted();
				motor.gotoSpeed(-30);
				break;
			case FORWARD:
				callback.moveCompleted();
				line();
				break;
			case STOP:
				if (motor.getState() == Motor.State.STOPPED) callback.moveCompleted();
				motor.gotoSpeed(0, 500);
				break;
		}
	}

	private void line() {
		if (collision) {
			motor.gotoSpeed(0);
			if (collisionTimer.timeout()) {
				notificationController.buzz();
			}
			return;
		}
		if (line == LineSignal.LEFT) {
//			motor.turn(15, -1);
			motor.turn(10, -1, 12);
		} else if (line == LineSignal.RIGHT) {
//			motor.turn(15, 1);
			motor.turn(10, 1, 12);
		} else {
//			motor.setSpeed(20);
			motor.gotoSpeed(20, 120);
		}
	}

	private void remote() {

	}

	private void manual() {
		switch (state) {
			case LEFT:
				motor.turn(speed / 2, -1);
				break;
			case BACKWARD:
				motor.setSpeed(-speed);
				break;
			case FORWARD:
				motor.setSpeed(speed);
				break;
			case RIGHT:
				motor.turn(speed / 2, 1);
				break;
			case STOP:
				motor.setSpeed(0);
				break;
		}
	}

	public void onSignal(Signal signal) {
		if (signal == Signal.EMERGENCY_STOP) emergencyStop();
		if (signal == Signal.FORWARD) state = State.FORWARD;
		if (signal == Signal.BACKWARD) state = State.BACKWARD;
		if (signal == Signal.TURN_BACKWARD) state = State.TURN_BACKWARD;
		if (signal == Signal.TURN_LEFT) state = State.LEFT;
		if (signal == Signal.TURN_RIGHT) state = State.RIGHT;
		if (signal == Signal.STOP) state = State.STOP;
		if (signal == Signal.SPEED_INC) if (speed + 5 <= 100) speed += 5;
		if (signal == Signal.SPEED_DEC) if (speed - 5 >= 0) speed -= 5;
		if (signal == Signal.EMERGENCY_RESUME) emergencyResume();
		if (signal == Signal.LINE_MODE) mode = Mode.LINE;
		if (signal == Signal.GRID_MODE) mode = Mode.GRID;
		if (signal == Signal.REMOTE_MODE) mode = Mode.REMOTE;
		if (signal == Signal.MANUAL_MODE) mode = Mode.MANUAL;
	}

	@Override
	public void onLineDetect(LineController src, LineSignal lineSignal) {
		if (mode != Mode.LINE && mode != Mode.GRID) return;
		line = lineSignal;
//		if (lineSignal == LineSignal.LEFT) state = State.LEFT;
//		if (lineSignal == LineSignal.RIGHT) state = State.RIGHT;
//		if (lineSignal == LineSignal.STRAIGHT) state = State.FORWARD;
	}

	public Mode getMode() {
		return mode;
	}

	public enum State {
		LEFT,
		BACKWARD,
		FORWARD,
		RIGHT,
		STOP,
		NONE, TURN_BACKWARD,
	}

	public enum Mode {
		GRID,
		LINE,
		REMOTE,
		MANUAL,
	}
}
