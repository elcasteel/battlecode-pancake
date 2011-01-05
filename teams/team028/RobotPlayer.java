package team028;

import java.util.ArrayList;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.MovementController;
import battlecode.common.RobotController;
import battlecode.common.SensorController;

public class RobotPlayer implements Runnable{
	private final RobotController myRC;
    boolean typeDiscovered=false;
    Unit me=null;
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
		
		while(!typeDiscovered){
			//make a list of all the types of components I have
		      ComponentController c[]=myRC.components();
		      ArrayList<ComponentType> myComponentTypes=new ArrayList<ComponentType>();
		      for(int i=0;i<c.length;i++){
		    	  myComponentTypes.add(c[i].type());
		      }
		      //make a list of all the components on a ConstructorScout
		      ArrayList<ComponentType> constructorScoutComponents=new ArrayList<ComponentType>();
		      constructorScoutComponents.add(ComponentType.SMALL_MOTOR);
		      constructorScoutComponents.add(ComponentType.SIGHT);
		      constructorScoutComponents.add(ComponentType.CONSTRUCTOR);
		      
              //check whether my components match any of our known robot types
		      
		      if(myComponentTypes.containsAll(constructorScoutComponents)){
		    	  MovementController m=(MovementController)c[myComponentTypes.indexOf(ComponentType.SMALL_MOTOR)];
		    	  SensorController s=(SensorController)c[myComponentTypes.indexOf(ComponentType.SIGHT)];
		    	  BuilderController b=(BuilderController)c[myComponentTypes.indexOf(ComponentType.CONSTRUCTOR)];
		    	  me=new ConstructorScout(myRC,m,s,b);
		    	  typeDiscovered=true;
		      }
             //otherwise, wait until the next turn
		      myRC.yield();
		}
		while(true){
			try{
			me.doStuff();
			}catch(Exception e){}
		}
	}

	
  
}
