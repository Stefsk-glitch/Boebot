package com.roboworks.robot.logic.route;

public interface RouteCallback {
	void onDirectionChange(RouteController src, Movement direction);

	enum Movement {
		LEFT,
		BACKWARD,
		FORWARD,
		RIGHT,
		TURN_BACKWARD,
		STOP,
	}
}
