package examplefuncsplayer;
//adding a comment to test git
//new comment!
import java.util.ArrayList;

import battlecode.common.BuilderController;
import battlecode.common.Chassis;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Mine;
import battlecode.common.MovementController;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.SensorController;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }
    ComponentType[] testBuilding ={ComponentType.ANTENNA,ComponentType.ARMORY,ComponentType.BEAM,
    		                       ComponentType.BLASTER,ComponentType.BUG,ComponentType.BUILDING_MOTOR,
    		                       ComponentType.BUILDING_SENSOR,ComponentType.CONSTRUCTOR,ComponentType.DISH,
    		                       ComponentType.DROPSHIP,ComponentType.DUMMY,ComponentType.FACTORY,
    		                       ComponentType.FLYING_MOTOR,ComponentType.HAMMER,ComponentType.HARDENED,
    		                       ComponentType.IRON,ComponentType.JUMP,ComponentType.LARGE_MOTOR,ComponentType.MEDIC,
    		                       ComponentType.MEDIUM_MOTOR,ComponentType.NETWORK,ComponentType.PLASMA,ComponentType.PLATING,
    		                       ComponentType.PROCESSOR,ComponentType.RADAR,ComponentType.SATELLITE,ComponentType.SHIELD,
    		                       ComponentType.SIGHT,ComponentType.SMALL_MOTOR,ComponentType.TELESCOPE};
    Boolean tried = false;
	public void run() {
		ComponentController [] components = myRC.newComponents();
		//System.out.println(java.util.Arrays.toString(components));
		//System.out.flush();
		if(myRC.getChassis()==Chassis.BUILDING){
			while(true){
				myRC.yield();
			}
			//System.out.println(components[1].componentClass()+ " length "+components.length);
			//myRC.setIndicatorString(0,components[1].componentClass()+ " length "+components.length);
			//runBuilder((MovementController)components[0],(BuilderController)components[2]);
		}
		else
			runMotor((MovementController)components[0]);
	}

	public void testit(MovementController m) {
		m.withinRange(myRC.getLocation());
	}

	public void runBuilder(MovementController motor, BuilderController builder) {
	
		while (true) {
            try {
            	
            	
            	
//				myRC.yield();
//
//				if(!motor.canMove(myRC.getDirection()))
//					motor.setDirection(myRC.getDirection().rotateRight());
//				else if(myRC.getTeamResources()>=2*Chassis.LIGHT.cost)
//					for(int i=1; i<testBuilding.length; i++){
//						try{
//							builder.build(testBuilding[i],myRC.getLocation().add(myRC.getDirection()),RobotLevel.ON_GROUND);
//							System.out.println(testBuilding[i].name());
//							myRC.yield();
//						}catch(IllegalArgumentException e){
//							System.out.println("catching an error");
//							myRC.yield();
//						}catch(GameActionException e){
//							System.out.println(testBuilding[i].name());
//							myRC.yield();
//						}
//					}
//				//builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
//				
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void runMotor(MovementController motor) {
        
        while (true) {
            try {
                /*** beginning of main loop ***/
            	
                while (motor.isActive()) {
                    myRC.yield();
                }
                //determine components
                ComponentController [] c = myRC.components();
            	SensorController sight = null;
            	BuilderController constructor = null;
            	for(int i=0; i<c.length; i++){
            		if (c[i].type()==ComponentType.CONSTRUCTOR){
            			constructor = (BuilderController) c[i];
            		}else if (c[i].type()==ComponentType.SIGHT){
            			sight = (SensorController) c[i];
            		}
            	}
            	
            	//bug-pathing code
            	//Robot [] nearbyRobots = (sight).senseNearbyGameObjects(Robot.class);
        		//int numberNearby = nearbyRobots.length;
        		//if (!motor.isActive()){
        		//	bug(motor, sight,numberNearby);
            	//}
        		
            	Mine [] nearbyMines = sight.senseNearbyGameObjects(Mine.class);
            	//ArrayList<MapLocation> finalMines = new ArrayList<MapLocation>();
            	MapLocation closestMine = null;
            	int closestDist = 10000;
            	int challengerDist=  10000;
            	//remove the mines that are taken
            	for(int i=0; i<nearbyMines.length; i++){
            		MapLocation pos = sight.senseLocationOf(nearbyMines[i]);
            		Robot guyOnTheMine = null;
            		guyOnTheMine = (Robot) sight.senseObjectAtLocation(pos, RobotLevel.ON_GROUND);
            		if (guyOnTheMine!=null){
            			RobotInfo info = sight.senseRobotInfo(guyOnTheMine);
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
        			motor.setDirection(myRC.getDirection().rotateRight().rotateRight());
    				myRC.setIndicatorString(1, "Searching");
        		}else if (dist<=(constructor.type().range)){//within range to build
        			Direction goaldir = myRC.getLocation().directionTo(goal);
        			if (myRC.getDirection()!=goaldir){
        				motor.setDirection(goaldir);
        				myRC.yield();
        			}
        			if (myRC.getTeamResources()>(Chassis.BUILDING.cost+ComponentType.RECYCLER.cost)){
        				constructor.build(Chassis.BUILDING, goal);
        				myRC.yield();
        				constructor.build(ComponentType.RECYCLER, goal,RobotLevel.ON_GROUND);
        			}else{
        				myRC.setIndicatorString(1, "Insufficient $");
        			}
        		}else{//need to walk there.
    				myRC.setIndicatorString(1, "Need to travel to target");
        			Direction goaldir = myRC.getLocation().directionTo(goal);//TODO this rotation may prevent you from walking on the goal
        			if (myRC.getDirection()==goaldir){//Correct direction
        				while (!motor.canMove(myRC.getDirection())){
            				motor.setDirection(myRC.getDirection().rotateRight());
            				myRC.setIndicatorString(1, "Avoiding obstacle");
            				myRC.yield();
        				}
        				motor.moveForward();
        				myRC.setIndicatorString(1, "Traveling to target");
        			}else{
        				motor.setDirection(goaldir);
        			}
        		}
            	
        		myRC.yield();
            	
            	
//                
//                if (motor.canMove(myRC.getDirection())) {
//                    //System.out.println("about to move");
//                    motor.moveForward();
//                } else {
//                    motor.setDirection(myRC.getDirection().rotateRight());
//                }

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
