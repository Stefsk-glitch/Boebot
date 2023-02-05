package com.roboworks.robot.logic;

import com.roboworks.robot.hardware.LineSensor;
import com.roboworks.robot.hardware.interfaces.callbacks.LineSensorCallBack;
import com.roboworks.robot.util.Config;
import com.roboworks.robot.util.Updatable;

import static com.roboworks.robot.logic.LineControllerCallback.LineSignal.*;

public class LineController implements Updatable, LineSensorCallBack {
	private LineSensor left;
	private LineSensor middle;
	private LineSensor right;
	private LineControllerCallback callback;
	private int lastLeft;
	private int lastRight;
	private int lastMiddle;

	public LineController(LineControllerCallback callback) {
		this.callback = callback;
	}

	public void setLeft(LineSensor left) {
		this.left = left;
	}

	public void setMiddle(LineSensor middle) {
		this.middle = middle;
	}

	public void setRight(LineSensor right) {
		this.right = right;
	}

	@Override
	public void update() {
	}

	@Override
	public void onLineRead(LineSensor src, int data) {
		if (src == left) lastLeft = data;
		if (src == right) lastRight = data;
		if (src == middle) lastMiddle = data;

		boolean leftLine = lastLeft > Config.LINE_THRESHOLD;
		boolean rightLine = lastRight > Config.LINE_THRESHOLD;
		boolean middleLine = lastMiddle > Config.LINE_THRESHOLD;

		if (callback == null) return;
		if (middleLine && leftLine && rightLine) callback.onLineDetect(this, CROSS_SECTION);
		else if (leftLine) callback.onLineDetect(this, LEFT);
		else if (rightLine) callback.onLineDetect(this, RIGHT);
		else if (middleLine) callback.onLineDetect(this, STRAIGHT);
	}
}
