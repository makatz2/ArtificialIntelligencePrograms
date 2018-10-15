import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze
				.getNoOfCols()];
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		State start = new State(maze.getPlayerSquare(), null, 0, 0);
		frontier.add(new StateFValuePair(start, fValue(start)));
		State currState;
		// List to store the successors of the current state till they are added
		// to the queue.
		ArrayList<State> successors;
		// Boolean value to store whether a match was found in frontier
		Boolean foundMatch = false;
		// StateFValuePair used to hold the current state
		StateFValuePair currPair;		
		while (!frontier.isEmpty()) {
			currState = frontier.poll().getState();
			noOfNodesExpanded++;
			if (currState.isGoal(maze)) {
				updateMaze(currState);
				maxDepthSearched = currState.getDepth();
				cost = currState.getDepth();
				return true;
			}
			explored[currState.getX()][currState.getY()] = true;
			successors = currState.getSuccessors(explored, maze);
			while (!successors.isEmpty()) {
				currState = successors.remove(0);
				currPair = new StateFValuePair(currState,
						fValue(currState));
				Iterator<StateFValuePair> iter = frontier.iterator();
				foundMatch = false;
				// Search frontier for a match to the currState.
				while(iter.hasNext()){
					StateFValuePair checkState = iter.next();
					if(checkState.getState().getX() == currState.getX() && checkState.getState().getY() == currState.getY()){
						foundMatch = true;
						// If a match is found only add the state to the 
						// frontier if it has a shorter path. Remove matching 
						// State with the longer path.
						if(checkState.getState().getGValue() > currPair.getState().getGValue()){
							iter.remove();
							frontier.add(currPair);
						}
					}
				}
				// Only add to the frontier if no match is found.
				if(!foundMatch){
				frontier.add(currPair);
				}
			}
			if (maxSizeOfFrontier <= frontier.size()) {
				maxSizeOfFrontier = frontier.size();
			}
		}
		return false;
	}

	public double fValue(State curr) {
		double x = java.lang.Math.pow(maze.getGoalSquare().X - curr.getX(), 2);
		double y = java.lang.Math.pow(maze.getGoalSquare().Y - curr.getY(), 2);
		// fValue = the distance from goal + the length of path.
		return java.lang.Math.sqrt(x + y) + (double)curr.getGValue();
	}
	public void updateMaze(State goal){
		State currState = goal.getParent();
		while(currState.getParent() != null){
			maze.setOneSquare(currState.getSquare(), '.');
			currState = currState.getParent();
		}
	}

}