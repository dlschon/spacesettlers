package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Represents an action for our planner
 * @author dlsch
 *
 */
public abstract class AbstractPlanAction {
	
	// Fields
	
	Toroidal2DPhysics space;
	Ship ship;
	Planner planner;

	// Methods 
	
	/**
	 * Returns true if all preconditions of the action are met
	 * That is, the game state is such that this option is possible
	 * @return
	 */
	public abstract boolean arePreconditionsMet();
	
	/**
	 * The result of successfully completing this action
	 * Specifically, which goal is accomplished or gets closer to accomplished
	 * @return
	 */
	public abstract int postconditions();
	
	/**
	 * The action part of the action. Which object to move to
	 * @return
	 */
	public abstract AbstractObject getTarget();
	
	/**
	 * Distance to the target object, used to choose more viable actions
	 * @return
	 */
	public double distanceToTarget()
	{
		return space.findShortestDistance(ship.getPosition(), getTarget().getPosition());
	}
	
	
	public AbstractPlanAction(Toroidal2DPhysics space, Ship ship, Planner planner) 
	{
		this.space = space;
		this.ship = ship;
		this.planner = planner;
	}
		
	

}
