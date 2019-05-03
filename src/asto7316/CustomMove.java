package asto7316;

import spacesettlers.actions.MoveAction;
import spacesettlers.objects.AbstractObject;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

/**
 * Calls MoveAction for the actual movements but allows you to aim for a spacewar object
 * and to stop when the object dies (e.g. someone (maybe you) reached it)
 * 
 * @author amy
 */
public class CustomMove extends MoveAction {
	protected Position originalGoalLocation;
	AbstractObject goalObject;
	/**
	 * Initialize with your location and the goal object 
	 * 
	 * @param space
	 * @param currentLocation
	 * @param goalObject
	 */
	public CustomMove(Toroidal2DPhysics space, Position currentLocation, Position goalPos, AbstractObject goalObj) {
		super(space, currentLocation, goalPos);
		this.originalGoalLocation = goalPos.deepCopy();
		this.goalObject = goalObj;
		setKpRotational(0);
		setKvRotational(0);
		setKvTranslational(2);
		setKpTranslational(1);
	}

	/**
	 * Initialize with your location and the goal object 
	 * 
	 * @param space
	 * @param currentLocation
	 * @param goalObject
	 */
	public CustomMove(Toroidal2DPhysics space, Position currentLocation, Position goalPos, AbstractObject goalObj,
			Vector2D goalVelocity) {
		super(space, currentLocation, goalPos, goalVelocity);
		this.originalGoalLocation = goalPos.deepCopy();
		this.goalObject = goalObj;
		setKpRotational(0);
		setKvRotational(0);
		setKvTranslational(2);
		setKpTranslational(1);
	}

	/**
	 * Return the goal object (and remember it is a clone so use its UUID!)
	 * @return
	 */
	public AbstractObject getGoalObject() {
		return goalObject;
	}




	/**
	 * Returns true if the movement finished or the goal object died or moved
	 * 
	 */
	public boolean isMovementFinished(Toroidal2DPhysics space) {
		if (super.isMovementFinished(space)) {
			//System.out.println("Super movement finished");
			return true;
		}
		
		AbstractObject newGoalObj = space.getObjectById(goalObject.getId());
		
		// goal object disappeared
		if (newGoalObj == null) {
			//System.out.println("Goal object disappeared");
			return true;
		}
		
		// goal object died
		if (!newGoalObj.isAlive()) {
			//System.out.println("Goal object dead");
			return true;
		} 

		// goal object moved
		if (!newGoalObj.getPosition().equalsLocationOnly(originalGoalLocation)) {
			//System.out.println("Goal object moved");
			return true;
		}
		
		return false;
	}
	

}
