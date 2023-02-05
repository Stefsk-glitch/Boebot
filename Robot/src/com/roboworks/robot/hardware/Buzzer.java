package com.roboworks.robot.hardware;

import TI.BoeBot;
import TI.PWM;
import TI.PinMode;

public class Buzzer implements com.roboworks.robot.hardware.interfaces.Buzzer {
	private final PWM pwm;


	public Buzzer(int pin) {
		BoeBot.setMode(pin, PinMode.Output);
		pwm = new PWM(pin, 0);
	}

	@Override
	public void startBuzz() {
		pwm.update(60);
	}

	@Override
	public void stopBuzz() {
		pwm.update(0);
	}
}
