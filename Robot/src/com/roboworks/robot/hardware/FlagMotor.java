package com.roboworks.robot.hardware;

import TI.BoeBot;
import TI.PWM;
import TI.PinMode;
import TI.Timer;
import com.roboworks.robot.util.Updatable;

public class FlagMotor implements Updatable {

	private int pin;
	private PWM pwm;
	private Timer timer = new Timer(1000);


	public FlagMotor(int pin) {
		this.pin = pin;
		pwm =  new PWM(pin, 0);
	}

	public void turn(){
		BoeBot.setMode(pin, PinMode.Output);
		pwm.update(255);
	}



	@Override
	public void update() {
		if (timer.timeout()){
			pwm.stop();
		}
	}
}
