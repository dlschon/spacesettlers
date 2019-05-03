package asto7316.Goals;

import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public abstract class AbstractGoal {

	// Fields
	

	// Methods 

	public AbstractGoal()
	{
	}

	public abstract boolean isGoalMet(Toroidal2DPhysics space, Ship ship);
	
	public abstract int goalID();
	
	
}


