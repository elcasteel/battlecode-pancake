package team028;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public abstract class Unit {
      protected final RobotController myRC;
      public Unit(RobotController rc)
      {
    	  myRC=rc;
      }
      public abstract void doStuff() throws GameActionException;
      //put other generic code here
}
