package asto7316.PlanActions;

import asto7316.Planner;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

public class BeaconAction extends AbstractPlanAction {

	Beacon beacon;
	
	public BeaconAction(Toroidal2DPhysics space, Ship ship, Beacon beacon) {
		super(space, ship);
		this.beacon = beacon;
	}

	@Override
	public boolean arePreconditionsMet() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int satisfies() {
		return Planner.GOAL_ID_ENERGY;
	}

	@Override
	public double distanceToTarget() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AbstractObject getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
