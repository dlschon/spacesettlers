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
	
	public BaseAction(Base base) 
	{
		super();
		this.base = base;
	}

	@Override
	public boolean arePreconditionsMet(Toroidal2DPhysics space, Ship ship) {
		
		// this base must belong to the ship
		return base.getTeamName() == ship.getTeamName();
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
