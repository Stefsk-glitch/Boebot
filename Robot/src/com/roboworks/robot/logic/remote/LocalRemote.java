package com.roboworks.robot.logic.remote;

import com.roboworks.robot.hardware.interfaces.Button;
import com.roboworks.robot.hardware.interfaces.callbacks.ButtonCallback;

public class LocalRemote implements Remote, ButtonCallback {
	private Button emergencyButton;
	private final RemoteCallback callback;

	public LocalRemote(Button emergencyButton, RemoteCallback callback) {
		this.emergencyButton = emergencyButton;
		this.callback = callback;
	}

	public LocalRemote(RemoteCallback callback) {
		this(null, callback);
	}

	public void setEmergencyButton(Button button) {
		this.emergencyButton = button;
	}

	@Override
	public void update() {

	}

	@Override
	public void onButtonChanged(Button src, boolean state) {
		if (src == emergencyButton && state)
			callback.onRemoteSignal(this, new RemoteSignal(RemoteSignal.Signal.EMERGENCY_STOP));
	}
}
