package asto7316.PlanActions;

import java.util.HashMap;
import java.util.UUID;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class FlagAction extends AbstractPlanAction {

	Flag flag;
	HashMap<UUID, AbstractObject> targets;
	
	public FlagAction(Toroidal2DPhysics space, Ship ship, Flag flag, HashMap<UUID, AbstractObject> targets) {
		super(space, ship);
		this.flag = flag;
		this.targets = targets;
	}

	@Override
	public boolean arePreconditionsMet() {
		
		// Check if any opposing team flags exist
		// Assume false until proven true
		boolean flagExists = false;
		for (Flag f : space.getFlags())
		{
			if (f.getTeamName() != ship.getTeamName())
				flagExists = true;
		}
		
		// Check to see if anyone is already targeting a flag
		// Assume false until proven true
		boolean flagTargeted = false;
		for (AbstractObject target : targets.values())
		{
			if (target instanceof Flag && ((Flag) target).getTeamName() != ship.getTeamName())
				flagTargeted = true;
		}
		
		return !ship.isCarryingFlag() && flagExists && !flagTargeted;
	}

	@Override
	public int satisfies() {
		return Planner.GOAL_ID_FLAG;
	}

	@Override
	public double distanceToTarget() {
		return space.findShortestDistance(ship.getPosition(), flag.getPosition());
	}

	@Override
	public AbstractObject getTarget() {
		return flag;
	}

}
