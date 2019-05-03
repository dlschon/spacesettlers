package asto7316;

import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public abstract class AbstractGoal {

	public abstract boolean isGoalMet(Ship ship, Toroidal2DPhysics space);
	
	public abstract int goalID();
}


