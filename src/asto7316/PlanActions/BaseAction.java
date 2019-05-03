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
	
	public BaseAction(Toroidal2DPhysics space, Ship ship, Planner planner, Base base) 
	{
		super(space, ship, planner);
		this.base = base;
	}

	@Override
	public boolean arePreconditionsMet() {
		
		// There's always a base so this is always possible
		return true;
	}

	@Override
	public int postconditions() {
		return Planner.GOAL_ID_BASE;
	}

	@Override
	public AbstractObject getTarget() {
		return base;
	}

}
