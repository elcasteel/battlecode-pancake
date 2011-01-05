package team028;

import java.util.ArrayList;

import battlecode.common.BuilderController;
import battlecode.common.Chassis;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Mine;
import battlecode.common.MovementController;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.SensorController;
//light size chassis with a sensor and a myBuilder
public class ConstructorScout extends Unit {
	  ArrayList<MapLocation> mines;
	 
	  MovementController myMotor;
	  SensorController mySensor;
	  BuilderController myBuilder;
	  public ConstructorScout(RobotController rc,MovementController m,SensorController s,BuilderController b){
		  //myRC is part of Unit- call the parent myBuilder to initialize it
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
	public void doStuff() throws GameActionException {

        while (myMotor.isActive()) {
            myRC.yield();
        }
        //determine components
//        ComponentController [] c = myRC.components();
//    	SensorController mySensor = null;
//    	BuilderController myBuilder = null;
//    	for(int i=0; i<c.length; i++){
//    		if (c[i].type()==ComponentType.CONSTRUCTOR){
//    			myBuilder = (BuilderController) c[i];
//    		}else if (c[i].type()==ComponentType.SIGHT){
//    			mySensor = (SensorController) c[i];
//    		}
//    	}
    	
    	//bug-pathing code
    	//Robot [] nearbyRobots = (mySensor).senseNearbyGameObjects(Robot.class);
		//int numberNearby = nearbyRobots.length;
		//if (!myMotor.isActive()){
		//	bug(myMotor, mySensor,numberNearby);
    	//}
		
    	Mine [] nearbyMines = mySensor.senseNearbyGameObjects(Mine.class);
    	//ArrayList<MapLocation> finalMines = new ArrayList<MapLocation>();
    	MapLocation closestMine = null;
    	int closestDist = 10000;
    	int challengerDist=  10000;
    	//remove the mines that are taken
    	for(int i=0; i<nearbyMines.length; i++){
    		MapLocation pos = mySensor.senseLocationOf(nearbyMines[i]);
    		Robot guyOnTheMine = null;
    		guyOnTheMine = (Robot) mySensor.senseObjectAtLocation(pos, RobotLevel.ON_GROUND);
    		if (guyOnTheMine!=null){
    			RobotInfo info = mySensor.senseRobotInfo(guyOnTheMine);
    			if (info.chassis!=Chassis.BUILDING){//someone is there but not a building
    				challengerDist = myRC.getLocation().distanceSquaredTo(pos);
    				if (challengerDist<closestDist){
    					closestDist = challengerDist;
    					closestMine = pos;
    				}
    			}
    		}else{//no one on the mine
    			challengerDist = myRC.getLocation().distanceSquaredTo(pos);
    			if (challengerDist<closestDist){
    				closestDist = challengerDist;
    				closestMine = pos;
    			}
    		}
    	}
    	//TODO go to closest, not the first in the list
    	int dist = 10000;
    	MapLocation goal = null;
    	if (closestMine!=null){
    		goal = closestMine;
    		dist = closestDist;
    	}

    	if (closestMine==null){//no mines; keep looking
    		myMotor.setDirection(myRC.getDirection().rotateRight().rotateRight());
    		myRC.setIndicatorString(1, "Searching");
    	}else{
    		if (dist==0){//we're on the mine, move one square away in any legal direction

    			if(myMotor.canMove(myRC.getDirection()))
    				myMotor.moveForward();
    			else if(myMotor.canMove(myRC.getDirection().opposite()))
    				myMotor.moveBackward();
    			else{
    				Direction direction=myRC.getDirection();
    				while(!myMotor.canMove(direction))
    					direction=direction.rotateLeft();
    				myMotor.setDirection(direction);
    				myRC.yield();
    				while(myMotor.isActive())
    					myRC.yield();
    				myMotor.moveForward();
    			}
    		}
    		if (dist<=(myBuilder.type().range)){//within range to build
    			Direction goaldir = myRC.getLocation().directionTo(goal);
    			if (myRC.getDirection()!=goaldir){
    				myMotor.setDirection(goaldir);
    				myRC.yield();
    			}
    			if (myRC.getTeamResources()>(Chassis.BUILDING.cost+ComponentType.RECYCLER.cost)){
    				myBuilder.build(Chassis.BUILDING, goal);
    				myRC.yield();
    				myBuilder.build(ComponentType.RECYCLER, goal,RobotLevel.ON_GROUND);
    			}else{
    				myRC.setIndicatorString(1, "Insufficient $");
    			}
    		}
    		else{//need to walk there.
    			myRC.setIndicatorString(1, "Need to travel to target");

    			while(myRC.getLocation().distanceSquaredTo(closestMine)>(myBuilder.type().range))
    			{
    				Direction goaldir = myRC.getLocation().directionTo(goal);//TODO this rotation may prevent you from walking on the goal

    				while(!myMotor.canMove(goaldir))
    				{
    					goaldir=goaldir.rotateRight();
    					myRC.setIndicatorString(1, "Avoiding obstacle "+goaldir.toString());
    				}
    				while(myMotor.isActive())
    					myRC.yield();
    				myMotor.setDirection(goaldir);
    				myRC.yield();
    				while(myMotor.canMove(goaldir)&&!myMotor.canMove(goaldir.rotateLeft())&&myRC.getLocation().distanceSquaredTo(closestMine)>(myBuilder.type().range))
    				{
    					while(myMotor.isActive())
    						myRC.yield();
    					myMotor.moveForward();
    					myRC.yield();
    				}
    			}
    		}
//			if (myRC.getDirection()==goaldir){//Correct direction
//				
//				while (!myMotor.canMove(myRC.getDirection())){
//    				myMotor.setDirection(myRC.getDirection().rotateRight());
//    				myRC.setIndicatorString(1, "Avoiding obstacle");
//    				myRC.yield();
//				}
//				myMotor.moveForward();
//				myRC.setIndicatorString(1, "Traveling to target");
//			}else{
//				myMotor.setDirection(goaldir);
//			}
		}
    	
		myRC.yield();
    	
    	
		
	}
	  
}
