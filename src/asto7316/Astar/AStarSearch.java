package asto7316.Astar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import spacesettlers.graphics.CircleGraphics;
import spacesettlers.graphics.LineGraphics;
import spacesettlers.graphics.SpacewarGraphics;
import spacesettlers.objects.AbstractObject;
import spacesettlers.objects.Asteroid;
import spacesettlers.objects.Base;
import spacesettlers.objects.Ship;
import spacesettlers.simulator.Toroidal2DPhysics;
import spacesettlers.utilities.Position;
import spacesettlers.utilities.Vector2D;

public class AStarSearch 
{
	// Constants for taking sample points
	final int GRID_SIZE = 16;
	// Step interval to replan A*
	final int REPLAN_STEPS = 5;
	int stepCount = -1;
	SearchResult result;
	
	// Roadmap A* search algorithm
	public SearchResult search(Toroidal2DPhysics space, Ship ship, AbstractObject target)
	{
		// Increment and check stepcount
		stepCount = (stepCount + 1) % REPLAN_STEPS;

		if (stepCount != 0)
		{
			// Return existing result
			return result;
		}
		
		// List of explored positions
		ArrayList<Node> explored = new ArrayList<Node>();
		
		Set<AbstractObject> obstructions = getObstructions(space, ship);
				
		// Create grid of nodes
		int x = 0;
		int y = 0;
		int w = 1600/GRID_SIZE;
		int h = 1080/GRID_SIZE;
		
		Node[][] grid = new Node[w][h];
		Node closestToShip = null;
		double smallestShipDistance = Double.MAX_VALUE;
		while (x < w)
		{
			grid[x] = new Node[h];
			while (y < h)
			{
				grid[x][y] = new Node(x*GRID_SIZE, y*GRID_SIZE, 0, null, space);
				double dist = space.findShortestDistance(grid[x][y], ship.getPosition());
				if (dist < smallestShipDistance)
				{
					closestToShip = grid[x][y];
					smallestShipDistance = dist;
				}
				
				if (space.findShortestDistance(grid[x][y], target.getPosition()) < GRID_SIZE)
					grid[x][y].isGoal = true;

				y += 1;
			}
			y = 0;
			x += 1;
		}
		
		// Comparator for scoring nodes
		NodeComparator myComp = new NodeComparator();
		myComp.setSpace(space);
		myComp.setTarget(target);
        PriorityQueue<Node> queue = new PriorityQueue<Node>(2000, myComp);
        
        boolean found = false;

        // Add ship position as root node
        closestToShip.pathLength = 0;
        queue.add(closestToShip);
        
        Node current = null;
        
        while (!queue.isEmpty() && !found)
        {
        	// Get current node
        	current = queue.poll();
        	explored.add(current);
        	
        	// Check if we've found our goal
        	if (current.isGoal)
        	{
        		result = current.traceBack();
        		return result;
        	}
        	else
        	{
        		for (Node neighbor : getNeighbors(grid, current, space, obstructions, ship, target))
        		{
        			if (neighbor.isGoal)
        			{
        				neighbor.parent = current;
        				result = neighbor.traceBack();
        				return result;
        			}
        			
        			// If the neighbor is already in the queue, ignore it
        			if (queue.contains(neighbor))
        				continue;
        			
        			// If its already been explored, check if the new astar score is better
        			if (explored.contains(neighbor))
        			{
        				// Calculate new potential path length
        				double newLength = current.pathLength + space.findShortestDistance(current, neighbor);
        				
        				if (newLength < neighbor.pathLength)
        				{
        					// The new route will have a lower f score (h hasn't changed)
        					// Update connection in search tree and update path length
        					neighbor.pathLength = newLength;
        					neighbor.parent = current;
        				}
        			}
        			else
        			{
        				neighbor.pathLength = current.pathLength + space.findShortestDistance(current, neighbor);
        				neighbor.parent = current;
        				queue.add(neighbor);
        			}
        		}
        	}
        }
        
        // Didn't find a path. Shouldn't ever reach this
        return null;
	}
	
	// Get the neighboring nodes on the grid
	Set<Node> getNeighbors(Node[][] grid, Node current, Toroidal2DPhysics space, Set<AbstractObject> obstructions, Ship ship, AbstractObject target)
	{
		Set<Node> neighbors = new HashSet<Node>();
		int gridx = (int) (current.getX()/GRID_SIZE);
		int gridy = (int) (current.getY()/GRID_SIZE);
		int w = 1600/GRID_SIZE;
		int h = 1080/GRID_SIZE;
		Node node;
		
		for (int relx = -1; relx <= 1; relx++)
		{
			for (int rely = -1; rely <= 1; rely++)
			{
				if (!(relx == 0 && rely == 0))
				{
					int absx = gridx + relx;
					if (absx < 0) absx = w - 1;
					if (absx > w - 1) absx = 0;
					int absy = gridy + rely;
					if (absy < 0) absy = h - 1;
					if (absy > h - 1) absy = 0;
					node = grid[absx][absy];
					
					boolean obstructed = false;
					// If node is close to beginning or end of path, don't check for obstructions
					if (!(space.findShortestDistance(node, target.getPosition()) < Ship.SHIP_RADIUS*3 || space.findShortestDistance(node, ship.getPosition()) < Ship.SHIP_RADIUS*3))
					{
						// Check if it is obstructed
						for (AbstractObject o : obstructions)
						{
							if (space.findShortestDistance(node, o.getPosition()) < Ship.SHIP_RADIUS*3)
								obstructed = true;
						}
					}
					if (!obstructed)
						neighbors.add(node);
				}
			}
		}
			
		return neighbors;
	}
	
	// Return a list of anything that can get in the way
	Set<AbstractObject> getObstructions(Toroidal2DPhysics space, Ship ship)
	{
		Set<AbstractObject> obstructions = new HashSet<AbstractObject>();
		// Add unmineable asteroids
		for (Asteroid a : space.getAsteroids())
		{
			if (!a.isMineable())
				obstructions.add((AbstractObject)a);
		}
		// Add non-player ships
		for (Ship s : space.getShips())
		{
			if (s.getTeamName() != "Schon Aston")
				obstructions.add((AbstractObject)s);
		}
		// Add bases
		for (Base b : space.getBases())
		{
			if (b.getTeamName() != "Schon Aston")
				obstructions.add((AbstractObject)b);
		}
		return obstructions;
	}
	
	
	// Heuristic function to score a node
	static double aStarHeuristic(Toroidal2DPhysics space, Position pos, AbstractObject target)
	{
		// Return euclidean distance between point and target position
		return space.findShortestDistance(pos, target.getPosition());
	}
	
	// Compares two nodes by astar score
	static class NodeComparator implements Comparator<Node>{ 
        
		AbstractObject target;
		Toroidal2DPhysics space;
		
		public void setTarget(AbstractObject target)
		{
			this.target = target;
		}
		
		public void setSpace(Toroidal2DPhysics space)
		{
			this.space = space;
		}
		
        // Overriding compare()method of Comparator  
        public int compare(Node p1, Node p2) 
        { 
        	// Calculate heuristic values
        	double hVal1 = AStarSearch.aStarHeuristic(space, p1, target);
        	double hVal2 = AStarSearch.aStarHeuristic(space, p2, target);
        	
        	// Compare by astar score
            if (hVal1 + p1.pathLength < hVal2 + p2.pathLength) 
                return -1; 
            else if (hVal1 + p1.pathLength > hVal2 + p2.pathLength) 
                return 1; 
            return 0; 
        } 
    } 
}
