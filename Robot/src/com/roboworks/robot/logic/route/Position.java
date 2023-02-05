package com.roboworks.robot.logic.route;

public class Position {
	private int x;
	private int y;

	private Direction direction;

	public enum Direction {
		NORTH,
		SOUTH,
		WEST,
		EAST
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(int x, int y, Direction direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	public void setOrientation(Direction direction) {
		this.direction = direction;
	}

	public void turnLeft(){
		switch (direction) {
			case NORTH:
				setOrientation(Direction.WEST);
				break;
			case SOUTH:
				setOrientation(Direction.EAST);
				break;
			case WEST:
				setOrientation(Direction.SOUTH);
				break;
			case EAST:
				setOrientation(Direction.NORTH);
				break;
		}
	}

	public void turnRight(){
		switch (direction) {
			case NORTH:
				setOrientation(Direction.EAST);
				break;
			case SOUTH:
				setOrientation(Direction.WEST);
				break;
			case WEST:
				setOrientation(Direction.NORTH);
				break;
			case EAST:
				setOrientation(Direction.SOUTH);
				break;
		}
	}

	public void addX() {
		this.x = this.x + 1;
	}

	public void addY() {
		this.y = this.y + 1;
	}

	public void removeX() {
		this.x = this.x - 1;
	}

	public void removeY() {
		this.y = this.y - 1;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Direction getOrientation() {
		return this.direction;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return x == position.x && y == position.y;
	}

	@Override
	public String toString() {
		return String.format("x = %d, y = %d, %s", x, y, direction);
	}
}
