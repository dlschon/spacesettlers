package asto7316.Astar;

import spacesettlers.graphics.LineGraphics;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;


// A node in the A* search tree
class Node extends Position
{
	final int GRID_SIZE = 16;
	public double pathLength;
	public Node parent;
	public boolean isGoal = false;
	public boolean explored = false;
	Toroidal2DPhysics space;
	
	public Node(double x, double y, double pathLength, Node parent, Toroidal2DPhysics space)
	{
		super(x,y);
		this.pathLength = pathLength;
		this.parent = parent;
		this.space = space;
	}
	
	public Node(Position pos, double pathLength, Node parent, Toroidal2DPhysics space)
	{
		super(pos.getX(), pos.getY());
		this.pathLength = pathLength;
		this.parent = parent;
		this.space = space;
	}
	
	// Trace the path back to its root
	// Returns the result object for the search
	public SearchResult traceBack()
	{
		SearchResult res = new SearchResult(new Vector2D(this), new Vector2D(this));
		if (this.parent  == null)
		{
			return new SearchResult(new Vector2D(this), new Vector2D(this));
		}
		
		// Remember two nodes in the past because Search Result object need a target and next target
		Node current = this;
		Node previous = null;
		Node prev2 = null;
		double len = 0;
		
		// Will loop until it gets to the root node, which has parent=null
		while(current != null)
		{
			if (current.parent == null)
			{
				// Current is the root node
				res.targetPos = new Vector2D(previous);
				res.nextTarget = prev2 == null ? null : new Vector2D(prev2);
				return res;
			}
			else
			{
				// Add line to graphical path
				if (previous != null)	// Not a leaf node 
				{
					// Don't draw if the line would wrap the screen
					if (!(Math.abs((previous.getX() - current.getX())/GRID_SIZE) > 1)
							&& !(Math.abs((previous.getY() - current.getY())/GRID_SIZE) > 1))
					{
						// Draw from parent to child node
						res.graphics.add(new LineGraphics(previous, current, new Vector2D(current).subtract(new Vector2D(previous))));
						
						// Add to path length
						len += space.findShortestDistance(previous, current);
					}
				}
				// Trace back a level in the tree
				prev2 = previous;
				previous = current;
				current = current.parent;
			}
		}
		
		res.pathLength = len;
		return res;
	}
	
}
