package asto7316;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import asto7316.Goals.AbstractGoal;
import asto7316.Goals.BaseGoal;
import asto7316.Goals.EnergyGoal;
import asto7316.Goals.FlagGoal;
import asto7316.Goals.ResourcesGoal;
import asto7316.PlanActions.AbstractPlanAction;
import asto7316.PlanActions.AsteroidAction;
import asto7316.PlanActions.BaseAction;
import asto7316.PlanActions.BeaconAction;
import asto7316.PlanActions.FlagAction;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Flag;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;

/**
 * The central planning agent that monitors the game state and commands the clients to perform actions
 * @author dlsch
 *
 */
public class Planner {
	// Named constants representing each kind of goal
	public static final int NUM_GOALS = 4;
	public static final int GOAL_ID_BASE = 0;
	public static final int GOAL_ID_ENERGY = 1;
	public static final int GOAL_ID_FLAG = 2;
	public static final int GOAL_ID_RESOURCES = 3;
	
	// Replan target every x steps
	public static final int REPLAN_STEPS = 20;
	
	// General planner fields
	public UUID id;
	int currentGoal = -1;
	int stepCount = -1;
	AbstractGoal[] subgoals;
	AbstractObject target = null;

	// The map of all ship to target relationships
	// This is static so everyone knows which objects are being targeted
	static HashMap<UUID, AbstractObject> targets = new HashMap<UUID, AbstractObject>();
	static HashMap<UUID, Integer> shipGoals = new HashMap<UUID, Integer>();

	public Planner()
	{
		subgoals = new AbstractGoal[NUM_GOALS];
		subgoals[GOAL_ID_ENERGY] = new EnergyGoal();
		subgoals[GOAL_ID_BASE] = new BaseGoal();
		subgoals[GOAL_ID_FLAG] = new FlagGoal();
		subgoals[GOAL_ID_RESOURCES] = new ResourcesGoal();
		this.id = UUID.randomUUID();
	}

	/**
	 * Choose a subgoal for this ship to pursue
	 * Goals are pursued sequentially in order of importance
	 * @return
	 */
	int getCurrentGoal(Toroidal2DPhysics space, Ship ship)
	{
		// Energy goal is top priority cause it lets us live
		if (!goalMet(space, ship, GOAL_ID_ENERGY))
			return GOAL_ID_ENERGY;
		
		// Next subgoal: go to base if we have something to bring back
		if (goalMet(space, ship, GOAL_ID_FLAG) || goalMet(space, ship, GOAL_ID_RESOURCES))
			return GOAL_ID_BASE;
		
		// Check if we should get a flag?
		if (!goalMet(space, ship, GOAL_ID_FLAG))
		{
			// Check if the flag is already being targeted
			boolean flagTargeted = false;
			for (Flag f : space.getFlags())
				if (f.getTeamName() != ship.getTeamName())
					flagTargeted = isTargetedByOther(f, ship);
				
			if (currentGoal != GOAL_ID_FLAG && !flagTargeted)
				return GOAL_ID_FLAG;
		}
		
		// When all else fails, mine some asteroids
		return GOAL_ID_RESOURCES;
	}
	
	/**
	 * Get the list of possible actions that have met their preconditions and could 
	 * help accomplish the current goal
	 * @return
	 */
	AbstractObject plan(Toroidal2DPhysics space, Ship ship)
	{
		stepCount = (stepCount + 1) % REPLAN_STEPS;
		
		if (!subgoals[GOAL_ID_ENERGY].isGoalMet(space, ship) || currentGoal == -1 || target == null || goalMet(space, ship, currentGoal))
		{
			// Replan if no goal or target set, if a goal was met, or if we're low on energy
			currentGoal = getCurrentGoal(space, ship);
			Planner.shipGoals.put(ship.getId(), currentGoal);
		}


		// Create list of all available actions
		ArrayList<AbstractPlanAction> available = new ArrayList<AbstractPlanAction>();

		// create BeaconActions for every available beacon
		for (Beacon b : space.getBeacons())
		{
			BeaconAction ba = new BeaconAction(b);
			
			if (ba.postconditions() != currentGoal)
				break;
			
			if (ba.arePreconditionsMet(space, ship))
				available.add(ba);
		}
				
		// create BaseActions for every available base
		for (Base b : space.getBases())
		{
			BaseAction ba = new BaseAction(b);
			
			if (ba.postconditions() != currentGoal)
				break;
			
			if (ba.arePreconditionsMet(space, ship))
				available.add(ba);
		}

		// create FlagAction for every available flag
		for (Flag f : space.getFlags())
		{
			FlagAction fa = new FlagAction(f);
			
			if (fa.postconditions() != currentGoal)
				break;

			if (fa.arePreconditionsMet(space, ship))
				available.add(fa);
		}

		// create ResourceAction for every available asteroid
		for (Asteroid a : space.getAsteroids())
		{
			AsteroidAction aa = new AsteroidAction(a);

			if (aa.postconditions() != currentGoal)
				break;

			if (aa.arePreconditionsMet(space, ship))
				available.add(aa);
		}
			
		// Find the viable action with the lowest distance
		double shortest = Double.MAX_VALUE;
		double distance;
		target = null;
		for (AbstractPlanAction apa : available)
		{
			distance = apa.distanceToTarget(space, ship);
			if (distance < shortest)
			{
				shortest = distance;
				target = apa.getTarget();
			}
		}
		
		System.out.println("P: " + id + "d: " + shortest);
		targets.put(ship.getId(), target);
		return target;

	}
	
	
	/** 
	 * Check if an object is currently being targeted by a ship
	 * @param obj
	 * @return
	 */
	public static boolean isTargeted(AbstractObject obj)
	{
		return targets.values().contains(obj);
	}

	/** 
	 * Check if an object is currently being targeted by a ship besides a given ship
	 * @param obj
	 * @return
	 */
	public static boolean isTargetedByOther(AbstractObject obj, Ship ship)
	{
		for (UUID s_id : targets.keySet())
		{
			if (s_id != ship.getId() && targets.get(s_id) == obj)
				return true;
		}
		return false;
	}
	
	/**
	 * Check if a goal has been met
	 * @param goalID
	 * @return
	 */
	public boolean goalMet(Toroidal2DPhysics space, Ship ship, int goalID)
	{
		return subgoals[goalID].isGoalMet(space, ship);
	}

}
