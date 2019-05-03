package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class AsteroidAction extends AbstractPlanAction {

	Asteroid asteroid;

	public AsteroidAction(Toroidal2DPhysics space, Ship ship, Planner planner, Asteroid asteroid) 
	{
		super(space, ship, planner);
		this.asteroid = asteroid;
	}

	@Override
	public boolean arePreconditionsMet() {
		
		// Check if there are any mineable, untargetted asteroids
		for (Asteroid a : space.getAsteroids())
			if (a.isMineable() && !planner.isTargeted(a))
				return true;
		
		// Didn't find one
		return false;
	}

	@Override
	public int postconditions() {
		return Planner.GOAL_ID_RESOURCES;
	}

	@Override
	public AbstractObject getTarget() {
		return asteroid;
	}

}
