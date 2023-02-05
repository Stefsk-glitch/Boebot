package com.roboworks.robot.hardware;

import TI.BoeBot;
import com.roboworks.robot.hardware.interfaces.Sensor;
import com.roboworks.robot.hardware.interfaces.callbacks.LineSensorCallBack;

public class LineSensor implements Sensor {
	private int pin;
	private LineSensorCallBack callBack;

	public LineSensor(int pin, LineSensorCallBack callBack) {
		this.pin = pin;
		this.callBack = callBack;
	}

	@Override
	public int read() {
		return BoeBot.analogRead(pin);
	}

	@Override
	public void update() {
		if (callBack != null) callBack.onLineRead(this, read());
	}
}
