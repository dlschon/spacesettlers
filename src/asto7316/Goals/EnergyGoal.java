package asto7316.Goals;

import asto7316.Planner;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * Subgoal for maintaining a healthy amount of energy
 * @author dlsch
 *
 */
public class EnergyGoal extends AbstractGoal {

	// The point at which we should start prioritizing beacons
	public static final int LOW_BATTERY = 2000;

	public EnergyGoal() {
	}

	@Override
	public boolean isGoalMet(Toroidal2DPhysics space, Ship ship) {
		return ship.getEnergy() > LOW_BATTERY;
	}

	@Override
	public int goalID() {
		return Planner.GOAL_ID_ENERGY;
	}

}
