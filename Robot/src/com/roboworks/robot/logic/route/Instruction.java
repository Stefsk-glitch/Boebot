package com.roboworks.robot.logic.route;

public class Instruction {
	private Position position;
	private Type type;

	public Instruction(Type type, Position position) {
		this.type = type;
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public Type getType() {
		return type;
	}

	public enum Type {
		DELIVER,
		RETRIEVE,
	}

	@Override
	public String toString() {
		return "Instruction{" +
				"position=" + position +
				", type=" + type +
				'}';
	}
}
