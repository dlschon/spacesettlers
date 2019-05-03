package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class AsteroidAction extends AbstractPlanAction {

	Asteroid asteroid;

	public AsteroidAction(Asteroid asteroid) 
	{
		this.asteroid = asteroid;
	}

	@Override
	public boolean arePreconditionsMet(Toroidal2DPhysics space, Ship ship) {
		
		// Check if asteroid is mineable and untargeted
		return asteroid.isMineable() && !Planner.isTargetedByOther(asteroid, ship);
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
