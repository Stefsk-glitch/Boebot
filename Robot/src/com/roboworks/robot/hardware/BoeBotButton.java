package com.roboworks.robot.hardware;

import TI.BoeBot;
import TI.PinMode;
import com.roboworks.robot.hardware.interfaces.Button;
import com.roboworks.robot.hardware.interfaces.callbacks.ButtonCallback;

public class BoeBotButton implements Button {

	private final int pin;
	private final ButtonCallback callback;
	private boolean state;

	public BoeBotButton(int pin, ButtonCallback callback) {
		BoeBot.setMode(pin, PinMode.Input);
		this.pin = pin;
		this.callback = callback;
		this.state = false;
	}

	@Override
	public boolean isPressed() {
		return !BoeBot.digitalRead(pin);
	}

	@Override
	public void update() {
		boolean newState = isPressed();

		if (state != newState) {
			state = newState;
			if (callback != null) callback.onButtonChanged(this, state);
		}
	}
}
