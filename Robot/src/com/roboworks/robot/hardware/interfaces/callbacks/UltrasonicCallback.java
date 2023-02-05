package com.roboworks.robot.hardware.interfaces.callbacks;

import com.roboworks.robot.hardware.UltrasonicSensor;

public interface UltrasonicCallback {
	void onUltrasonicSignal(UltrasonicSensor src, int distance);


}
