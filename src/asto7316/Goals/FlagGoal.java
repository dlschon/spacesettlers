package asto7316.Goals;

import asto7316.Planner;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * This goal is to go pick up a flag
 * @author dlsch
 *
 */
public class FlagGoal extends AbstractGoal {

	public FlagGoal() {
	}

	@Override
	public boolean isGoalMet(Toroidal2DPhysics space, Ship ship) {
		
		// Check if the ship is carrying flag
		return ship.isCarryingFlag();
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_FLAG;
	}

}
