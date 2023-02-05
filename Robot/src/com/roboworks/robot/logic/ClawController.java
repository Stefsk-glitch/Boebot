package com.roboworks.robot.logic;

import com.roboworks.robot.hardware.ServoClaw;
import com.roboworks.robot.hardware.interfaces.Claw;
import com.roboworks.robot.util.Updatable;


public class ClawController implements Updatable {
	private Claw servoClaw;

	public ClawController(Claw servoClaw) {
		this.servoClaw = servoClaw;
	}


	public void quickRelease(){
		servoClaw.open(0);
	}

	public boolean isClosed(){
		return servoClaw.isClosed();
	}

	public void close(){
		servoClaw.close(1000);
	}

	public void release(){
		servoClaw.open(1000);
	}

	@Override
	public void update() {

	}
}
