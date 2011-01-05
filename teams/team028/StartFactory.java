package team028;

import battlecode.common.BuilderController;
import battlecode.common.GameActionException;
import battlecode.common.MovementController;
import battlecode.common.RobotController;
import battlecode.common.SensorController;

public class StartFactory extends Unit {
	MovementController myMotor;
	SensorController mySensor;
	BuilderController myBuilder;

	public StartFactory(RobotController rc,MovementController m,SensorController s,BuilderController b){
		//myRC is part of Unit- call the parent myBuilder to initialize it
		super(rc);
		myMotor=m;
		mySensor=s;
		myBuilder =b;
	}
	@Override
	public void doStuff() throws GameActionException {
		myRC.setIndicatorString(2, "I'm a factory.");

	}

}
