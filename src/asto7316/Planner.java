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
import spacesettlers.objects.AbstractObject;
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
	Ship ship;
	Toroidal2DPhysics space;
	AbstractGoal currentGoal = null;
	int stepCount = -1;
	AbstractGoal[] subgoals;

	// The map of all ship to target relationships
	// This is static so everyone knows which objects are being targeted
	static HashMap<UUID, AbstractObject> targets;

	public Planner(Toroidal2DPhysics space, Ship ship)
	{
		this.space = space;
		this.ship = ship;
		targets = new HashMap<UUID, AbstractObject>();
		subgoals = new AbstractGoal[NUM_GOALS];
		subgoals[GOAL_ID_ENERGY] = new EnergyGoal(space, ship);
		subgoals[GOAL_ID_BASE] = new BaseGoal(space, ship);
		subgoals[GOAL_ID_FLAG] = new FlagGoal(space, ship);
		subgoals[GOAL_ID_RESOURCES] = new ResourcesGoal(space, ship);
	}

	/**
	 * Choose a subgoal for this ship to pursue
	 * Goals are pursued sequentially in order of importance
	 * @return
	 */
	int getCurrentGoal()
	{
		// Energy goal is top priority cause it lets us live
		if (!goalMet(GOAL_ID_ENERGY))
			return GOAL_ID_ENERGY;
		
		// Next subgoal: go to base if we have something to bring back
		if (goalMet(GOAL_ID_FLAG) || goalMet(GOAL_ID_RESOURCES))
			return GOAL_ID_BASE;
		
		// Check if we should get a flag?
		if (!goalMet(GOAL_ID_FLAG))
		{
			// Check if the flag is already being targeted
			boolean flagTargeted = false;
			for (AbstractObject obj : targets.values())
				if (obj instanceof Flag)
					flagTargeted = true;
				
			if (!flagTargeted)
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
	ArrayList<AbstractPlanAction> getAvailableActions()
	{

	}
	
	
	/** 
	 * Check if an object is currently being targeted by a ship
	 * @param obj
	 * @return
	 */
	public boolean isTargeted(AbstractObject obj)
	{
		return targets.values().contains(obj);
	}
	
	/**
	 * Check if a goal has been met
	 * @param goalID
	 * @return
	 */
	public boolean goalMet(int goalID)
	{
		return subgoals[goalID].isGoalMet();
	}

}
