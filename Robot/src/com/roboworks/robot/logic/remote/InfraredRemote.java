package com.roboworks.robot.logic.remote;

import com.roboworks.robot.hardware.InfraredSensor;
import com.roboworks.robot.hardware.interfaces.callbacks.InfraredCallback;

import java.util.HashMap;
import java.util.Map;

import static com.roboworks.robot.logic.remote.InfraredRemote.Buttons.*;

public class InfraredRemote implements Remote, InfraredCallback {
	private InfraredSensor infrared;
	private final RemoteCallback callback;
	private final int deviceId;
	private static final Map<Integer, RemoteSignal> buttonMap;

	static {
		buttonMap = new HashMap<>();
		buttonMap.put(OFF, new RemoteSignal(RemoteSignal.Signal.EMERGENCY_STOP));
		buttonMap.put(VOL_MUTE, new RemoteSignal(RemoteSignal.Signal.EMERGENCY_RESUME));
		buttonMap.put(CH_INC, new RemoteSignal(RemoteSignal.Signal.FORWARD));
		buttonMap.put(VOL_INC, new RemoteSignal(RemoteSignal.Signal.TURN_RIGHT));
		buttonMap.put(VOL_DEC, new RemoteSignal(RemoteSignal.Signal.TURN_LEFT));
		buttonMap.put(CH_DEC, new RemoteSignal(RemoteSignal.Signal.BACKWARD));
		buttonMap.put(ONE, new RemoteSignal(RemoteSignal.Signal.CLAW_OPEN));
		buttonMap.put(TWO, new RemoteSignal(RemoteSignal.Signal.CLAW_CLOSE));
		buttonMap.put(ZERO, new RemoteSignal(RemoteSignal.Signal.MANUAL_MODE));
		buttonMap.put(SEVEN, new RemoteSignal(RemoteSignal.Signal.GRID_MODE));
	}

	public InfraredRemote(InfraredSensor infrared, RemoteCallback callback, int deviceId) {
		this.infrared = infrared;
		this.callback = callback;
		this.deviceId = deviceId;
	}

	public InfraredRemote(RemoteCallback callback, int deviceId) {
		this(null, callback, deviceId);
	}

	public void setInfrared(InfraredSensor infrared) {
		this.infrared = infrared;
	}
	@Override
	public void update() {

	}

	private int getDeviceId(int data) {
		return data >> 7;
	}

	private int getButtonId(int data) {
		return data & 0b1111111;
	}

	private RemoteSignal getSignal(int data) {
		return buttonMap.get(getButtonId(data));
	}

	@Override
	public void onInfraredReceived(InfraredSensor src, int data) {
		// convert data to signal enum
		if (src == infrared && getDeviceId(data) == deviceId) callback.onRemoteSignal(this, getSignal(data));
	}

	protected static class Buttons {
		public static final int ONE = 0;
		public static final int TWO = 1;
		public static final int THREE = 2;
		public static final int FOUR = 3;
		public static final int FIVE = 4;
		public static final int SIX = 5;
		public static final int SEVEN = 6;
		public static final int EIGHT = 7;
		public static final int NINE = 8;
		public static final int ZERO = 9;
		public static final int ENTER = 12;
		public static final int CH_INC = 16;
		public static final int CH_DEC = 17;
		public static final int VOL_INC = 18;
		public static final int VOL_DEC = 19;
		public static final int VOL_MUTE = 20;
		public static final int OFF = 21;
		public static final int TV_VCR = 37; /* Why are these the same? */
		public static final int AB = 37;
		public static final int MYSTERY = 72; /* Button between VOL- and CH- */
	}
}
