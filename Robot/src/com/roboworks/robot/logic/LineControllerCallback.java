package com.roboworks.robot.logic;

public interface LineControllerCallback {
	void onLineDetect(LineController src, LineSignal lineSignal);

	enum LineSignal {
		CROSS_SECTION,
		LEFT,
		RIGHT,
		STRAIGHT
	}
}
