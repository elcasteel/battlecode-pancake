package team028;

import java.util.ArrayList;

import battlecode.common.BuilderController;
import battlecode.common.MapLocation;
import battlecode.common.Mine;
import battlecode.common.MovementController;
import battlecode.common.RobotController;
import battlecode.common.SensorController;
//light size chassis with a sensor and a constructor
public class ConstructorScout extends Unit {
	  ArrayList<MapLocation> mines;
	 
	  MovementController myMotor;
	  SensorController mySensor;
	  BuilderController myBuilder;
	  public ConstructorScout(RobotController rc,MovementController m,SensorController s,BuilderController b){
		  //myRC is part of Unit- call the parent constructor to initialize it
		  super(rc);
		  myMotor=m;
		  mySensor=s;
		  myBuilder =b;
	  }
	  public void explore(){
	    	//check for a mine within range
		  Mine []mines=mySensor.senseNearbyGameObjects(Mine.class);
		  for(int i=0;i<mines.length;i++)
		  { 
			  
		  }
		  
	  }
	@Override
	public void doStuff() {
		// TODO Auto-generated method stub
		
	}
	  
}
