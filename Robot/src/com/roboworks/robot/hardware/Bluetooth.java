package com.roboworks.robot.hardware;

import TI.SerialConnection;
import com.roboworks.robot.hardware.interfaces.callbacks.BluetoothCallback;
import com.roboworks.robot.util.Updatable;

public class Bluetooth implements Updatable {
	private final SerialConnection serial;
	private final BluetoothCallback callback;

	public Bluetooth(BluetoothCallback callback) {
		this.callback = callback;
		this.serial = new SerialConnection();
	}

	public byte[] receive() {
		int count = serial.available();
		if (count > 0) {
			byte[] bytes = new byte[count];
			for (int i = 0; i < count; i++) {
				bytes[i] = (byte) serial.readByte();
			}
			return bytes;
		}
		return null;
	}

	@Override
	public void update() {
		byte[] bytes;
		if ((bytes = receive()) != null) callback.onBluetoothReceived(this, bytes);
	}
}
