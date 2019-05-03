package asto7316;

import spacesettlers.objects.AbstractObject;

public abstract class AbstractPlanAction {
	
	public abstract boolean arePreconditionsMet();
	
	public abstract boolean satisfied();
	
	public abstract AbstractObject getTarget();
	
	public abstract double distanceToTarget();

}
