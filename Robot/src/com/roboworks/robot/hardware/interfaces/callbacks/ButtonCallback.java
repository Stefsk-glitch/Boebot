package com.roboworks.robot.hardware.interfaces.callbacks;

import com.roboworks.robot.hardware.interfaces.Button;

public interface ButtonCallback {
	void onButtonChanged(Button src, boolean state);
}
