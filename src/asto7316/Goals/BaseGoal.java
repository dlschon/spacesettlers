package asto7316.Goals;

import asto7316.Planner;
import spacesettlers.objects.Base;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * This goal is to return to base
 * @author dlsch
 *
 */
public class BaseGoal extends AbstractGoal {

	public BaseGoal(Toroidal2DPhysics space, Ship ship) {
		super(space, ship);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isGoalMet() {
		
		// Check all our bases for close proximity
		for (Base b : space.getBases())
			if (b.getTeamName() == ship.getTeamName() && space.findShortestDistance(b.getPosition(), ship.getPosition()) < Ship.SHIP_RADIUS*2)
				return true;
		
		// Didn't find one
		return false;
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_BASE;
	}

}
