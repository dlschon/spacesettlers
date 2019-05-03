package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class BeaconAction extends AbstractPlanAction {

	Beacon beacon;
	
	public BeaconAction(Beacon beacon) {
		this.beacon = beacon;
	}

	@Override
	public boolean arePreconditionsMet(Toroidal2DPhysics space, Ship ship) {
		// Check if beacon is untargeted
		return !Planner.isTargetedByOther(beacon, ship);
	}

	@Override
	public int postconditions() {
		return Planner.GOAL_ID_ENERGY;
	}

	@Override
	public AbstractObject getTarget() {
		return beacon;
	}

}
