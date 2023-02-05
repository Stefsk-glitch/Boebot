package com.roboworks.robot.logic;

import TI.Timer;
import com.roboworks.robot.hardware.Buzzer;
import com.roboworks.robot.hardware.interfaces.Led;
import com.roboworks.robot.util.Updatable;

import java.awt.*;
import java.util.List;

/* TODO refactor to implement interface */
public class NotificationController implements Updatable {
	private List<Led> leds;
	private Buzzer buzzer;
	private boolean onCollision;
	private Timer buzzerTimer;
	private boolean buzzing;

	public NotificationController(List<Led> leds, Buzzer buzzer) {
		this.leds = leds;
		this.buzzer = buzzer;
		buzzerTimer = new Timer(5000);
	}

	public void onCollision() {
		onCollision = true;
		for (Led led : leds) {
			led.setColor(Color.RED);
		}

	}

	public void onNormal() {
		onCollision = false;
		for (Led led : leds) {
			led.setColor(Color.GREEN);
		}
	}

	public void buzz() {
		buzzing = true;
		buzzerTimer.mark();
		buzzer.startBuzz();
	}

	@Override
	public void update() {
		if (buzzing && !onCollision)
			buzzer.stopBuzz();
	}
}
