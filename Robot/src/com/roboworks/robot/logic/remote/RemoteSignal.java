package com.roboworks.robot.logic.remote;

public class RemoteSignal {
	private Signal signal;

	public RemoteSignal(Signal signal) {
		this.signal = signal;
	}

	public Signal getSignal() {
		return signal;
	}

	public enum Signal {
		EMERGENCY_STOP,
		EMERGENCY_RESUME,

		TURN_BACKWARD,
		TURN_LEFT,
		TURN_RIGHT,
		FORWARD,
		BACKWARD,
		STOP,
		SPEED_INC,
		SPEED_DEC,

		LINE_MODE,
		REMOTE_MODE,
		GRID_MODE,
		MANUAL_MODE,

		CLAW_OPEN,
		CLAW_CLOSE,

		DELIVER,
		RETRIEVE,
	}

	public static class PositionSignal extends RemoteSignal {
		private int x;
		private int y;

		public PositionSignal(Signal signal, int x, int y) {
			super(signal);
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}
}
