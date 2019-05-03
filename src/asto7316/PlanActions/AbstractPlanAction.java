package asto7316.PlanActions;

import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public abstract class AbstractPlanAction {
	
	// Methods 
	
	public abstract boolean arePreconditionsMet();
	
	public abstract int satisfies();
	
	public abstract AbstractObject getTarget();
	
	public abstract double distanceToTarget();
	
	// Fields
	
	Toroidal2DPhysics space;
	Ship ship;
	
	public AbstractPlanAction(Toroidal2DPhysics space, Ship ship) 
	{
		this.space = space;
		this.ship = ship;
	}
		
	

}
