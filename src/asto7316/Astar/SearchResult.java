package asto7316.Astar;

import java.util.HashSet;
import java.util.Set;

import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.utilities.Vector2D;

// Returned by search method
// Stores 2 nodes so we can set the ideal target velocity
public class SearchResult
{
	public Vector2D targetPos;
	public Vector2D nextTarget;
	public Set<SpacewarGraphics> graphics;
	public double pathLength;
	
	public SearchResult(Vector2D targetPos, Vector2D targetVeloc)
	{
		this.targetPos = targetPos;
		this.nextTarget = targetVeloc;
		this.graphics = new HashSet<SpacewarGraphics>();
	}
}
