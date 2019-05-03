package asto7316.Goals;

import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public abstract class AbstractGoal {

	// Methods 

	public AbstractGoal(Toroidal2DPhysics space, Ship ship)
	{
		this.space = space;
		this.ship = ship;
	}

	public abstract boolean isGoalMet(Ship ship, Toroidal2DPhysics space);
	
	public abstract int goalID();
	
	// Fields
	
	Toroidal2DPhysics space;
	Ship ship;
	
}


