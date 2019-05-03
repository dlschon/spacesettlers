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

	public FlagGoal(Toroidal2DPhysics space, Ship ship) {
		super(space, ship);
	}

	@Override
	public boolean isGoalMet() {
		
		// Check if someone is carrying the opponent's flag
		for (Flag f : space.getFlags())
			if (f.getTeamName() != ship.getTeamName() && f.isBeingCarried())
				return true;

		// They're not
		return false;
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_FLAG;
	}

}
