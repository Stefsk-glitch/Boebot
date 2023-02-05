package com.roboworks.robot.hardware;

import TI.BoeBot;
import TI.PinMode;
import com.roboworks.robot.hardware.interfaces.Sensor;
import com.roboworks.robot.hardware.interfaces.callbacks.InfraredCallback;

public class InfraredSensor implements Sensor {

	private final int pin;
	private final InfraredCallback callback;

	public InfraredSensor(int pin, InfraredCallback callback) {
		BoeBot.setMode(pin, PinMode.Input);
		this.pin = pin;
		this.callback = callback;
	}

	@Override
	public int read() {
		int pulseLen = BoeBot.pulseIn(pin, false, 6000);
		if (pulseLen > 2000) {
			int data[] = new int[12];
			for (int i = 0; i < 12; i++) {
				data[i] = BoeBot.pulseIn(pin, false, 20000);
			}
			return convert(data);
		}
		return -1;
	}

	private int convert(int[] received) {
		int result = 0;
		for (int i = received.length - 1; i >= 0; i--) {
			result <<= 1;
			if (received[i] > 400 && received[i] < 800) {
				/* 0 */
			} else if (received[i] > 1000 && received[i] < 1400) {
				/* 1 */
				result |= 1;
			} else {
				return -1;
			}
		}
		return result;
	}

	@Override
	public void update() {
		int data = read();
		if (data > 0) callback.onInfraredReceived(this, data);
	}
}
