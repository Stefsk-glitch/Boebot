package com.roboworks.robot.hardware.interfaces.callbacks;

import com.roboworks.robot.hardware.LineSensor;

public interface LineSensorCallBack {
	void onLineRead(LineSensor src, int data);
}
