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
	HashMap <UUID, Ship> asteroidToShipMap;
	HashMap <UUID, Boolean> aimingForBase;
	HashMap <UUID, Boolean> goingForCore;
	HashMap <UUID, Boolean> justHitBase;
	HashSet<SpacewarGraphics> graphics;
	boolean fired = false;
	HashMap<UUID, Set<SpacewarGraphics>> astargraphics;
	HashMap<UUID, AStarSearch> searches;
	
	double weaponsProbability = 1;
	
	double targetSpeed = 100;
	
	public Map<UUID, AbstractAction> getMovementStart(Toroidal2DPhysics space,
			Set<AbstractActionableObject> actionableObjects) {
		HashMap<UUID, AbstractAction> actions = new HashMap<UUID, AbstractAction>();
		
		// Clear out the astar graphics
		astargraphics.clear();

		// loop through each ship
		for (AbstractObject actionable :  actionableObjects) {
			if (actionable instanceof Ship) {
				Ship ship = (Ship) actionable;

				AbstractAction action;

				// get the asteroids
				action = getAsteroidCollectorAction(ship.getId(), space, ship);
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
	private AbstractAction getAsteroidCollectorAction (UUID shipID, Toroidal2DPhysics space,
			Ship ship) {
		AbstractAction current = ship.getCurrentAction();
		Position currentPosition = ship.getPosition();
		AbstractAction newAction = null;
		AbstractObject target = null;
		
		// Decide which target to pursue using planning
		int decision = 0;
		// aim for a beacon
		if (decision == 0) {
			Beacon beacon = pickNearestBeacon(space, ship);
			// if there is no beacon, then just skip a turn
			if (beacon == null) {
				newAction = new DoNothingAction();
				return newAction;
			} else {
				target = beacon;
			}
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
		}

		// Take resources back to base
		else if (decision == 1) {
			Base base = findNearestBase(space, ship);
			target = base;
			aimingForBase.put(ship.getId(), true);
			goingForCore.put(ship.getId(), false);
		}

		// otherwise aim for the asteroid
		else if (decision == 2) {
			aimingForBase.put(ship.getId(), false);
			goingForCore.put(ship.getId(), false);
			justHitBase.put(ship.getId(), false);			
			Asteroid asteroid = pickHighestValueNearestFreeAsteroid(space, ship);

			if (asteroid == null) {
				// there is no asteroid available so collect a core
				AiCore nearbyCore = pickNearestCore(space, ship, 200);
				if (nearbyCore != null)
				{
					Position newGoal = nearbyCore.getPosition();
					target = nearbyCore;
					aimingForBase.put(ship.getId(), false);
					goingForCore.put(ship.getId(), true);
				}
				else
				{
					// No available cores so do nothing
					newAction = new DoNothingAction();
					return newAction;
				}
			} else {
				asteroidToShipMap.put(asteroid.getId(), ship);
				
				target = asteroid;
				
			}
			
			
		} else {
			return ship.getCurrentAction();
		}
		
		// Create new search object if it doesn't exist yet
		if (!searches.containsKey(shipID))
			searches.put(shipID, new AStarSearch());
		AStarSearch.SearchResult results = searches.get(shipID).search(space, ship, target);
		
		if (results != null)
		{
			astargraphics.put(shipID, results.graphics);
			
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
		ArrayList<Asteroid> finishedAsteroids = new ArrayList<Asteroid>();

		for (UUID asteroidId : asteroidToShipMap.keySet()) {
			Asteroid asteroid = (Asteroid) space.getObjectById(asteroidId);
			if (asteroid == null || !asteroid.isAlive() || asteroid.isMoveable()) {
				finishedAsteroids.add(asteroid);
			}
		}

		for (Asteroid asteroid : finishedAsteroids) {
			if (asteroidToShipMap != null && asteroid != null && asteroidToShipMap.containsKey(asteroid.getId()))
			asteroidToShipMap.remove(asteroid.getId());
		}
		
		// check to see who bounced off bases
		for (UUID shipId : aimingForBase.keySet()) {
			if (aimingForBase.get(shipId)) {
				Ship ship = (Ship) space.getObjectById(shipId);
				if (ship.getResources().getTotal() == 0 ) {
					aimingForBase.put(shipId, false);
					justHitBase.put(shipId, true);
					goingForCore.put(ship.getId(), false);
				}
			}
		}
		


	}

	@Override
	public void initialize(Toroidal2DPhysics space) {
		asteroidToShipMap = new HashMap<UUID, Ship>();
		aimingForBase = new HashMap<UUID, Boolean>();
		goingForCore = new HashMap<UUID, Boolean>();
		justHitBase = new HashMap<UUID, Boolean>();
		astargraphics = new HashMap<UUID, Set<SpacewarGraphics>>();
		searches = new HashMap<UUID, AStarSearch>();
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
			
			Boolean gettingCore = false;
			if (goingForCore.containsKey(actionableObject.getId())) {
				gettingCore = goingForCore.get(actionableObject.getId());
			}
		}
		
		return powerUps;
	}
}
