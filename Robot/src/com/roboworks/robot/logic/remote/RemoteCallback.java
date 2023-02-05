package com.roboworks.robot.logic.remote;

public interface RemoteCallback {
	void onRemoteSignal(Remote src, RemoteSignal signal);
}
