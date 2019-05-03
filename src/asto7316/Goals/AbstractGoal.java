package asto7316.Goals;

import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public abstract class AbstractGoal {

	// Fields
	
	Toroidal2DPhysics space;
	Ship ship;

	// Methods 

	public AbstractGoal(Toroidal2DPhysics space, Ship ship)
	{
		this.space = space;
		this.ship = ship;
	}

	public abstract boolean isGoalMet();
	
	public abstract int goalID();
	
	
}


