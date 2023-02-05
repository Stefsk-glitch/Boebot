package com.roboworks.robot.util;

import com.roboworks.robot.logic.route.Position;

public class Config {
	/* Servos */
	public static final int SERVO_LEFT = 12;
	public static final int SERVO_RIGHT = 13;

	/* Ultrasonic sensor Collision */
	public static final int TRIGGER_COLLISION = 5;
	public static final int ECHO_COLLISION = 6;

	/* Ultrasonic sensor Gripper*/
	public static final int TRIGGER_GRIPPER = 14;
	public static final int ECHO_GRIPPER = 15;

	/* Default Position */
	public static final Position DEFAULT_POSITION = new Position(0,-1, Position.Direction.SOUTH);

	/* Claw */
	public static final int CLAW = 10;
	public static final int CLAW_OPEN = 1900;
	public static final int CLAW_CLOSED = 1400;

	/* LEDs */
	public static final int[] NEOPIXELS = new int[]{0, 1, 2, 3, 4, 5};

	/* Buttons */
	public static final int EMERGENCY_BUTTON = 0;

	/* Infrared */
	public static final int INFRARED = 3;

	/* Buzzer */
	public static final int BUZZER = 7;

	/* Line Followers */
	public static final int FOLLOWER_LEFT = 0;
	public static final int FOLLOWER_MIDDLE = 1;
	public static final int FOLLOWER_RIGHT = 2;
	public static final int LINE_THRESHOLD = 1000;

	/* Infrared remote device id */
	public static final int INFRARED_REMOTE = 1;

	/* Flag motor */
	public static final int FLAG_MOTOR = 4;

}
