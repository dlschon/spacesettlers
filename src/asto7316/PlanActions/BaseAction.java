package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;

import spacesettlers.objects.Base;
import spacesettlers.objects.Ship;
import spacesettlers.objects.Flag;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.objects.AbstractObject;

public class BaseAction extends AbstractPlanAction {

	Base base;
	
	public BaseAction(Toroidal2DPhysics space, Ship ship, Base base) 
	{
		super(space, ship);
		this.base = base;
	}

	@Override
	public boolean arePreconditionsMet() {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public int satisfies() {
		return Planner.GOAL_ID_BASE;
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
