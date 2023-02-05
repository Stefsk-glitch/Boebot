package com.roboworks.robot.logic.route;

import com.roboworks.robot.hardware.FlagMotor;
import com.roboworks.robot.hardware.UltrasonicSensor;
import com.roboworks.robot.hardware.interfaces.callbacks.UltrasonicCallback;
import com.roboworks.robot.logic.ClawController;
import com.roboworks.robot.logic.LineController;
import com.roboworks.robot.logic.LineControllerCallback;
import com.roboworks.robot.util.Config;

import java.util.LinkedList;
import java.util.Queue;

import static com.roboworks.robot.logic.route.RouteCallback.Movement.*;


public class RouteController implements LineControllerCallback, UltrasonicCallback {

	private Position position;
	private RouteCallback callback;

	private ClawController clawController;
	private FlagMotor flagMotor;

	private LineSignal lastSignal;
	private RouteCallback.Movement move;
	private Queue<Instruction> instructions;
	private Queue<RouteCallback.Movement> moveQueue;
	private boolean moving;
	private boolean processing;
	private int ultrasonicSignal;
	private int ultrasonicIsClear;

	public RouteController(RouteCallback callback, FlagMotor flagMotor, ClawController clawController) {
		this.clawController = clawController;
		this.flagMotor = flagMotor;
		this.instructions = new LinkedList<>();
		this.position = new Position(Config.DEFAULT_POSITION.getX(), Config.DEFAULT_POSITION.getY(), Config.DEFAULT_POSITION.getOrientation());
		this.callback = callback;
		this.processing = false;
		this.ultrasonicSignal = 0;
//		this.addInstruction(new Instruction(Instruction.Type.RETRIEVE, new Position(0, 0)));
//		this.addInstruction(new Instruction(Instruction.Type.DELIVER, new Position(2, 2)));
//		this.addInstruction(new Instruction(Instruction.Type.RETRIEVE, new Position(0, 0)));
		moveQueue = new LinkedList<>();
		moving = false;
	}

	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}

	@Override
	public void onLineDetect(LineController src, LineSignal lineSignal) {
		RouteCallback.Movement newMove = null;


		Instruction instruction = instructions.peek();
		if (instruction == null) return;
		Position destination = instruction.getPosition();

		if (ultrasonicSignal > 2 && instruction.getType() == Instruction.Type.RETRIEVE && !clawController.isClosed()) {
			newMove = STOP;
			clawController.close();
		}

		if (instruction.getType().equals(Instruction.Type.RETRIEVE) && clawController.isClosed()) {
			newMove = FORWARD;
		}


		if (!processing && !instructions.isEmpty()) {
			if (instruction.getType() == Instruction.Type.RETRIEVE) {
				clawController.release();
				System.out.println(ultrasonicIsClear);
				System.out.println(position);
				System.out.println(Config.DEFAULT_POSITION);
//				System.out.println(position.equals(Config.DEFAULT_POSITION));
				if (ultrasonicIsClear < 50 && !position.equals(Config.DEFAULT_POSITION)){
					System.out.println("return");
					return;
				}
			}
			processing = true;
			newMove = getNewDirection(position, destination);
		}

		if (move == LEFT && lineSignal == LineSignal.LEFT) {
			newMove = FORWARD;
			position.turnLeft();
		}
		if (move == RIGHT && lineSignal == LineSignal.RIGHT) {
			newMove = FORWARD;
			position.turnRight();
		}
		/* Assume turn backwards turns left */
		if (move == TURN_BACKWARD && lineSignal == LineSignal.LEFT && lastSignal != LineSignal.RIGHT) {
			newMove = FORWARD;
			position.turnLeft();
		}

		if (lineSignal == LineSignal.CROSS_SECTION && lastSignal != LineSignal.CROSS_SECTION) {
			switch (position.getOrientation()) {
				case NORTH:
					position.removeY();
					break;
				case SOUTH:
					position.addY();
					break;
				case WEST:
					position.removeX();
					break;
				case EAST:
					position.addX();
					break;
			}

			if (position.equals(destination)) {
				if (instruction.getType() == Instruction.Type.DELIVER)
					clawController.release();
				newMove = STOP;
				instructions.remove();
				processing = false;
				flagMotor.turn();
			} else {
				newMove = getNewDirection(position, destination);
			}
			System.out.println(position);
		}

		if (newMove != null && newMove != move) {
			move = newMove;
			if (move == RIGHT || move == LEFT) {
				moveQueue.add(STOP);
			}
			moveQueue.add(move);
//			System.out.println(move);
//			callback.onDirectionChange(this, move);
		}

//		System.out.println(moving);
		if (!moving && !moveQueue.isEmpty()) {
			callback.onDirectionChange(this, moveQueue.peek());
			moveQueue.remove();
			moving = true;
		}

		lastSignal = lineSignal;
	}

	private RouteCallback.Movement getNewDirection(Position position, Position destination) {
		RouteCallback.Movement newMove = FORWARD;
		if (position.getY() != destination.getY()) {
			Position.Direction required = position.getY() > destination.getY() ? Position.Direction.NORTH : Position.Direction.SOUTH;
			switch (position.getOrientation()) {
				case NORTH:
					if (required == Position.Direction.SOUTH) newMove = TURN_BACKWARD;
					break;
				case SOUTH:
					if (required == Position.Direction.NORTH) newMove = TURN_BACKWARD;
					break;
				case WEST:
					if (required == Position.Direction.NORTH) newMove = RIGHT;
					if (required == Position.Direction.SOUTH) newMove = LEFT;
					break;
				case EAST:
					if (required == Position.Direction.NORTH) newMove = LEFT;
					if (required == Position.Direction.SOUTH) newMove = RIGHT;
					break;
			}
		} else if (position.getX() != destination.getX()) {
			Position.Direction required = position.getX() > destination.getX() ? Position.Direction.WEST : Position.Direction.EAST;
			switch (position.getOrientation()) {
				case NORTH:
					if (required == Position.Direction.EAST) newMove = RIGHT;
					if (required == Position.Direction.WEST) newMove = LEFT;
					break;
				case SOUTH:
					if (required == Position.Direction.EAST) newMove = LEFT;
					if (required == Position.Direction.WEST) newMove = RIGHT;
					break;
				case WEST:
					if (required == Position.Direction.EAST) newMove = TURN_BACKWARD;
					break;
				case EAST:
					if (required == Position.Direction.WEST) newMove = TURN_BACKWARD;
					break;
			}
		}
		return newMove;
	}

	@Override
	public void onUltrasonicSignal(UltrasonicSensor src, int distance) {
//		System.out.println(distance);
		if (distance > 30 ){
			ultrasonicIsClear++;
		} else ultrasonicIsClear = 0;
		if (distance < 3 && distance >= 0){
			ultrasonicSignal++;
		} else ultrasonicSignal = 0;
	}




	public void moveCompleted() {
		moving = false;
	}
}
