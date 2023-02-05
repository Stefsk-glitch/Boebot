package com.roboworks.robot.hardware.interfaces;

import com.roboworks.robot.util.Updatable;

public interface Sensor extends Updatable {
	/**
	 * Read value from sensor.
	 * @return Integer value read from sensor.
	 */
	int read();
}
