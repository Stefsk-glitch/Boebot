package com.roboworks.robot.hardware.interfaces.callbacks;

import com.roboworks.robot.hardware.Bluetooth;

public interface BluetoothCallback {
	void onBluetoothReceived(Bluetooth src, byte[] bytes);
}
