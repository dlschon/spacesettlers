package asto7316.Goals;

import asto7316.Planner;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class BaseGoal extends AbstractGoal {

	public BaseGoal(Toroidal2DPhysics space, Ship ship) {
		super(space, ship);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isGoalMet(Ship ship, Toroidal2DPhysics space) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_BASE;
	}

}
