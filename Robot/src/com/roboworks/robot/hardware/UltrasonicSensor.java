package com.roboworks.robot.hardware;

import TI.BoeBot;
import TI.PinMode;
import TI.Timer;
import com.roboworks.robot.hardware.interfaces.Sensor;
import com.roboworks.robot.hardware.interfaces.callbacks.UltrasonicCallback;

/**
 * Ultrasonic sensor, for collision detection.
 */
public class UltrasonicSensor implements Sensor {
	private final int triggerPin;
	private final int echoPin;
	private int lastRead;
	private Timer timer;
	private static final int DELAY = 70;
	private UltrasonicCallback callback;
	/* Used to calculate distance in cm from pulse width */
	private static final int SCALE = 58;

	/**
	 * Construct sensor using trigger and echo pin numbers.
	 *
	 * @param triggerPin Pin to trigger sensor.
	 * @param echoPin    Pin to read echo from sensor.
	 * @param callback   Callback function on incoming collision.
	 */
	public UltrasonicSensor(int triggerPin, int echoPin, UltrasonicCallback callback) {
		BoeBot.setMode(triggerPin, PinMode.Output);
		BoeBot.setMode(echoPin, PinMode.Input);
		this.triggerPin = triggerPin;
		this.echoPin = echoPin;
		this.timer = new Timer(DELAY);
		this.lastRead = readSensor();
		this.callback = callback;
	}

	/**
	 * Read the distance from the ultrasonic sensor.
	 *
	 * @return Integer representing the distance.
	 */
	@Override
	public int read() {
		if (!timer.timeout())
			return lastRead;

		return readSensor();
	}

	private int readSensor() {
		BoeBot.digitalWrite(triggerPin, true);
		BoeBot.wait(1);
		BoeBot.digitalWrite(triggerPin, false);
		lastRead = BoeBot.pulseIn(echoPin, true, 10000) / SCALE;
		return lastRead;
	}

	@Override
	public void update() {
		if (callback != null) callback.onUltrasonicSignal(this, read());
	}
}

