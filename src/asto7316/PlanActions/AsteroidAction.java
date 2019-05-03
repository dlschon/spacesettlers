package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class AsteroidAction extends AbstractPlanAction {

	Asteroid asteroid;

	public AsteroidAction(Toroidal2DPhysics space, Ship ship, Asteroid asteroid) 
	{
		super(space, ship);
		this.asteroid = asteroid;
	}

	@Override
	public boolean arePreconditionsMet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int satisfies() {
		return Planner.GOAL_ID_RESOURCES;
	}

	@Override
	public double distanceToTarget() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AbstractObject getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
