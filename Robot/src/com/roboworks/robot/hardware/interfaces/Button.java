package com.roboworks.robot.hardware.interfaces;

import com.roboworks.robot.util.Updatable;

public interface Button extends Updatable {
	boolean isPressed();
}
