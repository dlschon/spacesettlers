package asto7316.Goals;

import asto7316.Planner;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * This goal is to collect an adequate amount of resources
 * @author dlsch
 *
 */
public class ResourcesGoal extends AbstractGoal {

	public static final int ADEQUATE_RESOURCES = 2500;

	public ResourcesGoal() {
	}

	@Override
	public boolean isGoalMet(Toroidal2DPhysics space, Ship ship) {
		return ship.getResources().getTotal() >= ADEQUATE_RESOURCES;
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_RESOURCES;
	}

}
