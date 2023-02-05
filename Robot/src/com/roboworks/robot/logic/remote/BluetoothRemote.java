package com.roboworks.robot.logic.remote;

import com.roboworks.robot.hardware.Bluetooth;
import com.roboworks.robot.hardware.interfaces.callbacks.BluetoothCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.roboworks.robot.logic.remote.BluetoothRemote.Commands.*;

public class BluetoothRemote implements Remote, BluetoothCallback {
	private Bluetooth bluetooth;
	private final RemoteCallback callback;
	private static final Map<Byte, Function<ArrayList<Byte>, RemoteSignal>> commandMap;
	private ArrayList<Byte> buffer;

	static {
		commandMap = new HashMap<>();
		commandMap.put(EMERGENCY_RESUME, x -> new RemoteSignal(RemoteSignal.Signal.EMERGENCY_RESUME));
		commandMap.put(EMERGENCY_STOP, x -> new RemoteSignal(RemoteSignal.Signal.EMERGENCY_STOP));
		commandMap.put(DELIVER, x -> {
			if (x.size() < 3) return null;
			return new RemoteSignal.PositionSignal(RemoteSignal.Signal.DELIVER, x.get(1), x.get(2));
		});
		commandMap.put(RETRIEVE, x -> {
			if (x.size() < 3) return null;
			return new RemoteSignal.PositionSignal(RemoteSignal.Signal.RETRIEVE, x.get(1), x.get(2));
		});

//		commandMap.put(EMERGENCY_RESUME, RemoteCallback.Signal.EMERGENCY_RESUME);
//		commandMap.put(EMERGENCY_STOP, RemoteCallback.Signal.EMERGENCY_STOP);
//		commandMap.put((byte) '[', RemoteCallback.Signal.CLAW_OPEN);
//		commandMap.put((byte) ']', RemoteCallback.Signal.CLAW_CLOSE);
//		commandMap.put((byte) 'w', RemoteCallback.Signal.FORWARD);
//		commandMap.put((byte) 'a', RemoteCallback.Signal.TURN_LEFT);
//		commandMap.put((byte) 'd', RemoteCallback.Signal.TURN_RIGHT);
//		commandMap.put((byte) 's', RemoteCallback.Signal.BACKWARD);
//		commandMap.put((byte) ' ', RemoteCallback.Signal.STOP);
//		commandMap.put((byte) ';', RemoteCallback.Signal.SPEED_DEC);
//		commandMap.put((byte) '\'', RemoteCallback.Signal.SPEED_INC);
//		commandMap.put((byte) '1', RemoteCallback.Signal.LINE_MODE);
//		commandMap.put((byte) '2', RemoteCallback.Signal.REMOTE_MODE);
//		commandMap.put((byte) '3', RemoteCallback.Signal.MANUAL_MODE);
	}

	public BluetoothRemote(Bluetooth bluetooth, RemoteCallback callback) {
		this.bluetooth = bluetooth;
		this.callback = callback;
		this.buffer = new ArrayList<>();
	}

	public BluetoothRemote(RemoteCallback callback) {
		this(null, callback);
	}

	public void setBluetooth(Bluetooth bluetooth) {
		this.bluetooth = bluetooth;
	}

	@Override
	public void update() {

	}

	@Override
	public void onBluetoothReceived(Bluetooth src, byte[] bytes) {
		if (src != null && src == bluetooth) {
			buffered(bytes);
		}
	}

	private void buffered(byte[] bytes) {
		for (byte b : bytes) {
			switch (b) {
				case START:
					buffer.clear();
					break;
				case END:
					if (callback == null || buffer.size() < 1) break;
					Function<ArrayList<Byte>, RemoteSignal> function = commandMap.get(buffer.get(0));
					if (function == null) break;

					RemoteSignal signal = function.apply(buffer);
					if (signal != null) callback.onRemoteSignal(this, signal);

					buffer.clear();
					break;
				default:
					buffer.add(b);
					break;
			}
		}
	}

	protected static class Commands {
		public static final byte START = -2;
		public static final byte END = -1;
		public static final byte EMERGENCY_STOP = 1;
		public static final byte EMERGENCY_RESUME = 2;
		public static final byte DELIVER = 20;
		public static final byte RETRIEVE = 21;
		public static final byte GET_QUEUE = 30;
		public static final byte SEND_QUEUE = 31;
	}
}
