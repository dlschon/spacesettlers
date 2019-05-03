package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class BeaconAction extends AbstractPlanAction {

	Beacon beacon;
	
	public BeaconAction(Toroidal2DPhysics space, Ship ship, Planner planner, Beacon beacon) {
		super(space, ship, planner);
		this.beacon = beacon;
	}

	@Override
	public boolean arePreconditionsMet() {
		// Check if there are any untargeted beacons
		for (Beacon b : space.getBeacons())
			if (!planner.isTargeted(b))
				return true;
		
		// Didn't find one
		return false;
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
