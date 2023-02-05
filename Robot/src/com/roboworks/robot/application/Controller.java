package com.roboworks.robot.application;

import TI.BoeBot;
import com.roboworks.robot.hardware.*;
import com.roboworks.robot.hardware.interfaces.Claw;
import com.roboworks.robot.hardware.interfaces.Led;
import com.roboworks.robot.hardware.interfaces.Motor;
import com.roboworks.robot.logic.*;
import com.roboworks.robot.logic.remote.*;
import com.roboworks.robot.logic.route.Instruction;
import com.roboworks.robot.logic.route.Position;
import com.roboworks.robot.logic.route.RouteCallback;
import com.roboworks.robot.logic.route.RouteController;
import com.roboworks.robot.util.Config;
import com.roboworks.robot.util.Updatable;

import java.util.ArrayList;
import java.util.List;

public class Controller implements RemoteCallback, LineControllerCallback, RouteCallback, MotorControllerCallback {

	private List<Updatable> updatables = new ArrayList<>();

	private LocalRemote localRemote;
	private InfraredRemote infraredRemote;
	private InfraredSensor infraredSensor;
	private BluetoothRemote bluetoothRemote;
	private Bluetooth bluetooth;

	private RouteController routeController;

	private List<Led> leds = new ArrayList<>();
	private MotorController motorController;
	private NotificationController notificationController;
	private FlagMotor flagMotor;
	private BoeBotButton emergencyButton;
	private LineSensor leftLineSensor;
	private LineSensor rightLineSensor;
	private LineSensor middleLineSensor;
	private LineController lineController;
	private ClawController clawController;


	private Claw claw;


	public Controller() {
		for (int i = 0; i < Config.NEOPIXELS.length; i++) {
			leds.add(new NeoPixel(Config.NEOPIXELS[i]));
		}
		updatables.addAll(leds);
		Motor motor = new ServoMotor(Config.SERVO_LEFT, Config.SERVO_RIGHT);
		updatables.add(motor);
		updatables.add(notificationController = new NotificationController(leds, new Buzzer(Config.BUZZER)));
		updatables.add(flagMotor = new FlagMotor(Config.FLAG_MOTOR));

		updatables.add(motorController = new MotorController(motor, notificationController, flagMotor, this));
		updatables.add(new UltrasonicSensor(Config.TRIGGER_COLLISION, Config.ECHO_COLLISION, motorController));

		updatables.add(claw = new ServoClaw(Config.CLAW));

		updatables.add(clawController = new ClawController(claw));


		routeController = new RouteController(this, this.flagMotor, this.clawController);
		updatables.add(new UltrasonicSensor(Config.TRIGGER_GRIPPER, Config.ECHO_GRIPPER, routeController));

		updatables.add(lineController = new LineController(this));
		updatables.add(leftLineSensor = new LineSensor(Config.FOLLOWER_LEFT, lineController));
		updatables.add(rightLineSensor = new LineSensor(Config.FOLLOWER_RIGHT, lineController));
		updatables.add(middleLineSensor = new LineSensor(Config.FOLLOWER_MIDDLE, lineController));
		lineController.setLeft(leftLineSensor);
		lineController.setRight(rightLineSensor);
		lineController.setMiddle(middleLineSensor);


		updatables.add(localRemote = new LocalRemote(this));
		updatables.add(emergencyButton = new BoeBotButton(Config.EMERGENCY_BUTTON, localRemote));
		localRemote.setEmergencyButton(emergencyButton);

		updatables.add(infraredRemote = new InfraredRemote(this, Config.INFRARED_REMOTE));
		updatables.add(infraredSensor = new InfraredSensor(Config.INFRARED, infraredRemote));
		infraredRemote.setInfrared(infraredSensor);

		updatables.add(bluetoothRemote = new BluetoothRemote(this));
		updatables.add(bluetooth = new Bluetooth(bluetoothRemote));
		bluetoothRemote.setBluetooth(bluetooth);
	}

	public void run() {
		while (true) {
			for (Updatable updatable : updatables) {
				updatable.update();
			}
			BoeBot.wait(1);
		}
	}

	@Override
	public void onRemoteSignal(Remote src, RemoteSignal remoteSignal) {
		if (remoteSignal == null) return;
		RemoteSignal.Signal signal = remoteSignal.getSignal();

		Instruction instruction;
		RemoteSignal.PositionSignal positionSignal;
		if (signal == null) return;
		switch (signal) {
			case EMERGENCY_STOP:
			case EMERGENCY_RESUME:

			case TURN_LEFT:
			case TURN_RIGHT:
			case FORWARD:
			case BACKWARD:
			case STOP:
			case SPEED_INC:
			case SPEED_DEC:

			case LINE_MODE:
			case REMOTE_MODE:
			case MANUAL_MODE:
				if (motorController != null) motorController.onSignal(signal);
				break;
			case CLAW_OPEN:
				if (claw != null) claw.open(1000);
				break;
			case CLAW_CLOSE:
				if (claw != null) claw.close(1000);
				break;
			case DELIVER:
				if (!(remoteSignal instanceof RemoteSignal.PositionSignal)) break;
				positionSignal = (RemoteSignal.PositionSignal) remoteSignal;
				instruction = new Instruction(Instruction.Type.DELIVER, new Position(positionSignal.getX(), positionSignal.getY()));
				routeController.addInstruction(instruction);
				break;
			case RETRIEVE:
				if (!(remoteSignal instanceof RemoteSignal.PositionSignal)) break;
				positionSignal = (RemoteSignal.PositionSignal) remoteSignal;
				instruction = new Instruction(Instruction.Type.RETRIEVE, new Position(positionSignal.getX(), positionSignal.getY()));
				routeController.addInstruction(instruction);
				break;
		}
	}


	@Override
	public void onLineDetect(LineController src, LineSignal lineSignal) {
		motorController.onLineDetect(src, lineSignal);
		routeController.onLineDetect(src, lineSignal);
	}

	@Override
	public void onDirectionChange(RouteController src, Movement direction) {
		if (motorController.getMode() == MotorController.Mode.GRID)
		switch (direction) {
			case LEFT:
				motorController.onSignal(RemoteSignal.Signal.TURN_LEFT);
				break;
			case BACKWARD:
				motorController.onSignal(RemoteSignal.Signal.BACKWARD);
				break;
			case FORWARD:
				motorController.onSignal(RemoteSignal.Signal.FORWARD);
				break;
			case RIGHT:
				motorController.onSignal(RemoteSignal.Signal.TURN_RIGHT);
				break;
			case TURN_BACKWARD:
				motorController.onSignal(RemoteSignal.Signal.TURN_BACKWARD);
				break;
			case STOP:
				motorController.onSignal(RemoteSignal.Signal.STOP);
				break;

		}
	}

	@Override
	public void moveCompleted() {
		routeController.moveCompleted();
	}
}
