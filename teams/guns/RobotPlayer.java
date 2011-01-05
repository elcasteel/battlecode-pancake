package guns;
//adding a comment to test git
//new comment!
import java.awt.Component;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

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
			//System.out.println(components[1].componentClass()+ " length "+components.length);
			//myRC.setIndicatorString(0,components[1].componentClass()+ " length "+components.length);
			runBuilder((MovementController)components[0],(BuilderController)components[2]);
		}
		else
			runMotor((MovementController)components[0]);
	}

	public void testit(MovementController m) {
		m.withinRange(myRC.getLocation());
	}

	public void runBuilder(MovementController motor, BuilderController builder) {
		//get the components
		ComponentController [] c = myRC.components();
    	SensorController sight = null;
    	BuilderController constructor = null;
    	for(int i=0; i<c.length; i++){
    		if (c[i].type()==ComponentType.RECYCLER){
    			constructor = (BuilderController) c[i];
    		}else if (c[i].type()==ComponentType.BUILDING_SENSOR){
    			sight = (SensorController) c[i];
    		}
    	}
    	
    	
		while (true) {
            try {

				myRC.yield();
				
				//build light chassises
				if(!motor.canMove(myRC.getDirection()))
					motor.setDirection(myRC.getDirection().rotateRight());
				else if(myRC.getTeamResources()>=2*Chassis.LIGHT.cost)
					//for(int i=1; i<testBuilding.length; i++){
					builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
				//System.out.println(testBuilding[i].name());
				
				//try to equip constructors
				Robot [] nearbyRobots = (sight).senseNearbyGameObjects(Robot.class);
				int numberNearby = nearbyRobots.length;
				if (numberNearby>0){//Try to build guns!
					//MapLocation target = sight.senseLocationOf(nearbyRobots[0]);
					MapLocation target = myRC.getLocation().add(myRC.getDirection());
					tryToAttach(target,ComponentType.CONSTRUCTOR,constructor);
				}else{
					myRC.setIndicatorString(0,"not trying");
				}
				
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
            	
            	if ((sight) != null){
            		//myRC.setIndicatorString(0," "+c[0].type());
            		//myRC.setIndicatorString(1," "+c[1].type());
            		//myRC.setIndicatorString(2," "+c[2].type());
            		Robot [] nearbyRobots = (sight).senseNearbyGameObjects(Robot.class);
            		int numberNearby = nearbyRobots.length;
            		if (!motor.isActive()){
            			bug(motor, sight,numberNearby);
                	}
            		
            		if (numberNearby>0){//Try to build guns!
            			//tryToAttach(sight.senseLocationOf(nearbyRobots[0]),ComponentType.CONSTRUCTOR,constructor);
            		}else{
            			myRC.setIndicatorString(0,"not trying");
            		}
            	}
            	myRC.yield();

            	
            	
            	
//                while (motor.isActive()) {
//                    myRC.yield();
//                }
                
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

	private void tryToAttach(MapLocation alliedLoc,
			ComponentType comp,BuilderController constructor) throws GameActionException {

		Boolean enoughMoney = myRC.getTeamResources()>=1.1*ComponentType.CONSTRUCTOR.cost;
		//Boolean withinRange = constructor.withinRange(alliedLoc);
		int dist = myRC.getLocation().distanceSquaredTo(alliedLoc);
		Boolean withinRange = dist<=1;
		myRC.setIndicatorString(0,"try constr. "+withinRange+"far "+dist +" "+enoughMoney);
		
		if (withinRange && enoughMoney){
			constructor.build(comp, alliedLoc,RobotLevel.ON_GROUND);
			myRC.setIndicatorString(2,"success!");
		}
		
	}

	private void bug(MovementController motor, SensorController sight, int numberNearby) throws GameActionException {
		myRC.setIndicatorString(1," "+ numberNearby+" robots sensed");
		if (numberNearby==0){
			motor.setDirection(myRC.getDirection().rotateRight());
		}else if (motor.canMove(myRC.getDirection())){
			motor.moveForward();
		}else{
			motor.setDirection(myRC.getDirection().rotateRight());
		}
	}
}
