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
	
	public FlagAction(Toroidal2DPhysics space, Ship ship, Planner planner, Flag flag) {
		super(space, ship, planner);
		this.flag = flag;
	}

	@Override
	public boolean arePreconditionsMet() {
		
		// Check if any opposing team flags exist and is not being carried
		// Assume false until proven true
		boolean flagExists = false;
		for (Flag f : space.getFlags())
			if (f.getTeamName() != ship.getTeamName() && !f.isBeingCarried())
				flagExists = true;
		
		// Check to see if anyone is already targeting a flag
		boolean flagTargeted = planner.isTargeted(flag);
		
		return !ship.isCarryingFlag() && flagExists && !flagTargeted;
	}

	@Override
	public int postconditions() {
		return Planner.GOAL_ID_FLAG;
	}

	public AbstractObject getTarget() {
		return flag;
	}

}
