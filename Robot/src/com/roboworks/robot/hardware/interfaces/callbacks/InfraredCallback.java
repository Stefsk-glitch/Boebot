package com.roboworks.robot.hardware.interfaces.callbacks;

import com.roboworks.robot.hardware.InfraredSensor;

public interface InfraredCallback {
	void onInfraredReceived(InfraredSensor src, int data);
}
