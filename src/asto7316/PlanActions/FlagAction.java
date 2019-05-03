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
	
	public FlagAction(Flag flag) {
		this.flag = flag;
	}

	@Override
	public boolean arePreconditionsMet(Toroidal2DPhysics space, Ship ship) {
		
		// Check if flag is being carried
		boolean flagCarried = flag.isBeingCarried();
		
		// Check if the flag is mine
		boolean myFlag = flag.getTeamName() == ship.getTeamName();

		// Check to see if anyone is already targeting the flag
		boolean flagTargeted = Planner.isTargetedByOther(flag, ship);
		
		return !ship.isCarryingFlag() && !flagCarried && !myFlag && !flagTargeted;
	}

	@Override
	public int postconditions() {
		return Planner.GOAL_ID_FLAG;
	}

	public AbstractObject getTarget() {
		return flag;
	}

}
