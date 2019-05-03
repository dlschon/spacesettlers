package asto7316;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javafx.scene.paint.Color;

import java.util.PriorityQueue;

import asto7316.CustomMove;
import asto7316.Astar.AStarSearch;
import spacesettlers.actions.AbstractAction;
import spacesettlers.actions.DoNothingAction;
import spacesettlers.actions.MoveAction;
import spacesettlers.actions.MoveToObjectAction;
import spacesettlers.actions.PurchaseCosts;
import spacesettlers.actions.PurchaseTypes;
import spacesettlers.clients.TeamClient;
import spacesettlers.graphics.CircleGraphics;
import spacesettlers.graphics.LineGraphics;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractActionableObject;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.AiCore;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Beacon;
import spacesettlers.objects.Ship;
import spacesettlers.objects.powerups.SpaceSettlersPowerupEnum;
import spacesettlers.objects.resources.ResourcePile;
import spacesettlers.objects.weapons.AbstractWeapon;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;
/**
 * The main client for this project
 * It tries to collect asteroids in a smart way.
 * For project 3, we implemented a genetic algorithm to decide which object to target.
 * 
 * @author Daniel Schon, Elizabeth Aston
 *
 */
public class SchonAstonClient extends TeamClient {
	HashSet<SpacewarGraphics> graphics;
	boolean fired = false;
	HashMap<UUID, Set<SpacewarGraphics>> astargraphics;
	HashMap<UUID, AStarSearch> searches = new HashMap<UUID, AStarSearch>();
	HashMap<UUID, Planner> planners = new HashMap<UUID, Planner>();
	
	double weaponsProbability = 1;
	
	double targetSpeed = 100;
	
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		
		// Clear out the astar graphics
		astargraphics.clear();

		Planner planner;
		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				AbstractAction action;

				// get the asteroids
				action = getAsteroidCollectorAction(space, ship);
				actions.put(ship.getId(), action);
				
				
			} else {
				// it is a base.  Heuristically decide when to use the shield (TODO)
				actions.put(actionable.getId(), new DoNothingAction());
			}
		} 
		return actions;
	}
	
	/**
	 * Gets the action for collecting asteroids
	 * @param space
	 * @param ship
	 * @return
	 */
	private AbstractAction getAsteroidCollectorAction (Toroidal2DPhysics space,
			Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();
		AbstractAction newAction = null;
		AbstractObject target = null;
		
		Planner planner;
		if (!planners.containsKey(ship.getId()))
		{
			// Create the Planner if it doesn't yet exist
			planner = new Planner();
			planners.put(ship.getId(), planner);
		}
		else
			// Get existing planner for ship
			planner = planners.get(ship.getId());

		// Get target from planner
		target = planner.plan(space, ship);
		
		if (target == null)
			return new DoNothingAction();
		
		// Create new search object if it doesn't exist yet
		AStarSearch search;
		if (!searches.containsKey(ship.getId()))
		{
			search = new AStarSearch();
			searches.put(ship.getId(), new AStarSearch());
		}
		else
			search = searches.get(ship.getId());

		AStarSearch.SearchResult results = search.search(space, ship, target);
		
		if (results != null)
		{
			astargraphics.put(ship.getId(), results.graphics);
			
			if (results.nextTarget != null)
				newAction = new CustomMove(space, currentPosition, new Position(results.targetPos), target, 
					target.getPosition().getTranslationalVelocity().add(getIdealEndVelocity(space, new Position(results.nextTarget), ship)));
			else
				newAction = new CustomMove(space, currentPosition, new Position(results.targetPos), target);

			return newAction;
		}
		else
		{
			return ship.getCurrentAction();
		}
	}

	/**
	 * Find the nearest core to this ship that falls within the specified minimum distance
	 * @param space
	 * @param ship
	 * @return
	 */
	private AiCore pickNearestCore(Toroidal2DPhysics space, Ship ship, int minimumDistance) {
		Set<AiCore> cores = space.getCores();

		AiCore closestCore = null;
		double bestDistance = minimumDistance;

		for (AiCore core : cores) {
			double dist = space.findShortestDistance(ship.getPosition(), core.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestCore = core;
			}
		}

		return closestCore;
	}	
	

	/**
	 * Find the base for this team nearest to this ship
	 * 
	 * @param space
	 * @param ship
	 * @return
	 */
	private Base findNearestBase(Toroidal2DPhysics space, Ship ship) {
		double minDistance = Double.MAX_VALUE;
		Base nearestBase = null;

		for (Base base : space.getBases()) {
			if (base.getTeamName().equalsIgnoreCase(ship.getTeamName())) {
				double dist = space.findShortestDistance(ship.getPosition(), base.getPosition());
				if (dist < minDistance) {
					minDistance = dist;
					nearestBase = base;
				}
			}
		}
		return nearestBase;
	}

	/**
	 * Returns the asteroid of highest value that isn't already being chased by this team
	 * 
	 * @return
	 */
	private Asteroid pickHighestValueNearestFreeAsteroid(Toroidal2DPhysics space, Ship ship) {
		Set<Asteroid> asteroids = space.getAsteroids();
		double bestScore = Integer.MIN_VALUE;
		Asteroid bestAsteroid = null;

		for (Asteroid asteroid : asteroids) {
			double value = asteroid.getResources().getTotal();
			double dist = space.findShortestDistance(asteroid.getPosition(), ship.getPosition());
			if (asteroid.isMineable() && value / dist > bestScore) {
				bestAsteroid = asteroid;
				bestScore = value / dist;
			}
		}
		return bestAsteroid;
	}
	

	/**
	 * Find the nearest beacon to this ship
	 * @param space
	 * @param ship
	 * @return
	 */
	private Beacon pickNearestBeacon(Toroidal2DPhysics space, Ship ship) {
		// get the current beacons
		Set<Beacon> beacons = space.getBeacons();

		Beacon closestBeacon = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (Beacon beacon : beacons) {
			double dist = space.findShortestDistance(ship.getPosition(), beacon.getPosition());
			if (dist < bestDistance) {
				bestDistance = dist;
				closestBeacon = beacon;
			}
		}

		return closestBeacon;
	}



	@Override
	public void getMovementEnd(Toroidal2DPhysics space, Set<AbstractActionableObject> actionableObjects) {


	}

	@Override
	public void initialize(Toroidal2DPhysics space) {
		astargraphics = new HashMap<UUID, Set<SpacewarGraphics>>();
	}

	@Override
	public void shutDown(Toroidal2DPhysics space) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<SpacewarGraphics> getGraphics() {
		Set<SpacewarGraphics> gs = new HashSet<SpacewarGraphics>();
		
		// Combine the graphics sets for all ships on the team
		for (Set<SpacewarGraphics> shipGraphics : astargraphics.values())
		{
			gs.addAll(shipGraphics);
		}

		return gs;
	}
	
	// Direction from the ship to a position
	private double directionTo(Toroidal2DPhysics space, Position p, Ship ship)
	{
		Vector2D dist = space.findShortestDistanceVector(ship.getPosition(), p);
		return dist.getAngle();
	}
	
	private Vector2D getIdealEndVelocity(Toroidal2DPhysics space, Position p, Ship ship)
	{
		double dir = directionTo(space, p, ship);
		Vector2D endVeloc = Vector2D.fromAngle(dir, targetSpeed);
		return endVeloc;
	}

	@Override
	/**
	 * If there is enough resourcesAvailable, buy a base.  Place it by finding a ship that is sufficiently
	 * far away from the existing bases
	 */
	public Map<UUID, PurchaseTypes> getTeamPurchases(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects, 
			ResourcePile resourcesAvailable, 
			PurchaseCosts purchaseCosts) {

		HashMap<UUID, PurchaseTypes> purchases = new HashMap<UUID, PurchaseTypes>();
		double BASE_BUYING_DISTANCE =600;
		boolean bought_base = false;

		if (purchaseCosts.canAfford(PurchaseTypes.BASE, resourcesAvailable)) {
			for (AbstractActionableObject actionableObject : actionableObjects) {
				if (actionableObject instanceof Ship) {
					Ship ship = (Ship) actionableObject;
					Set<Base> bases = space.getBases();
					boolean shouldBuyBase = true;

					// how far away is this ship to a base of my team?
					double maxDistance = Double.MIN_VALUE;
					for (Base base : bases) {
						if (base.getTeamName().equalsIgnoreCase(getTeamName())) {
							double distance = space.findShortestDistance(ship.getPosition(), base.getPosition());
							if (distance > maxDistance) {
								maxDistance = distance;
							}
							else {
								shouldBuyBase = false;
							}
						}
					}

					if (maxDistance > BASE_BUYING_DISTANCE && shouldBuyBase) {
						purchases.put(ship.getId(), PurchaseTypes.BASE);
						bought_base = true;
						break;
					}
				}
			}		
		} 
		
		return purchases;
	}

	/**
	 * The asteroid collector doesn't use power ups but the weapons one does (at random)
	 * @param space
	 * @param actionableObjects
	 * @return
	 */
	@Override
	public Map<UUID, SpaceSettlersPowerupEnum> getPowerups(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, SpaceSettlersPowerupEnum> powerUps = new HashMap<UUID, SpaceSettlersPowerupEnum>();

		for (AbstractActionableObject actionableObject : actionableObjects){
			SpaceSettlersPowerupEnum powerup = SpaceSettlersPowerupEnum.values()[random.nextInt(SpaceSettlersPowerupEnum.values().length)];
			
		}
		
		return powerUps;
	}
}
