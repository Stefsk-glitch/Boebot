package com.roboworks.robot.hardware.interfaces;

import com.roboworks.robot.util.Updatable;

/**
 * Control the robots motor.
 */
public interface Motor extends Updatable {

	/**
	 * Instantly go to specified speed.
	 *
	 * @param speed Speed to set.
	 */
	void setSpeed(int speed);

	/**
	 * Gradually go to specified speed.
	 *
	 * @param speed        Speed to go to from -100 - 100, negative is reverse.
	 * @param milliseconds Time to take to go to speed in milliseconds.
	 */
	void gotoSpeed(int speed, int milliseconds);

	/**
	 * Go to specified speed over 1 second.
	 *
	 * @param speed Speed to go to from -100 - 100, negative is reverse.
	 */
	void gotoSpeed(int speed);

	/**
	 * Turns the robot by a number of degrees.
	 *
	 * @param degrees The number of degrees to turn the robot,
	 *                negative values turn left, positive turn right.
	 */
	void turn(int degrees);

	/**
	 * Turns the robot to the given direction.
	 *
	 * @param speed     Speed to turn at.
	 * @param direction Direction to turn to,
	 *                  positive values turn right, negative turn left.
	 */
	void turn(int speed, int direction);

	/**
	 * @param speed        Speed to turn at.
	 * @param direction    Direction to turn to.
	 * @param milliseconds Time to take for turn.
	 */
	void turn(int speed, int direction, int milliseconds);

	/**
	 * Gradually stop.
	 *
	 * @param milliseconds Time to take to stop in milliseconds.
	 */
	void stop(int milliseconds);

	/**
	 * Gradually stop over 1 second.
	 */
	void stop();

	/**
	 * Immediately stop robot and prevent movement.
	 */
	void emergencyStop();

	/**
	 * Get the current speed of the motor.
	 *
	 * @return Speed from -100 - 100, negative is reverse.
	 */
	int getSpeed();

	/**
	 * Get the current state of the motor.
	 *
	 * @return Current state of the motor.
	 */
	State getState();

	enum State {
		STOPPED,
		STOPPING,
		RUNNING,
		TURNING
	}
}
