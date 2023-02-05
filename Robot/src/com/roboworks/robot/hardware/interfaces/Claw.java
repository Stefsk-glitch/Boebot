package com.roboworks.robot.hardware.interfaces;

import com.roboworks.robot.util.Updatable;

public interface Claw extends Updatable {
	boolean isClosed();
	void open(int milliseconds);
	void close(int milliseconds);
}
