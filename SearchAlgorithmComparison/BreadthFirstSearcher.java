import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// explored list is a 2D Boolean array that indicates if a state
		// associated with a given position in the maze has already been
		// explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze
				.getNoOfCols()];

		// Queue implementing the Frontier list
		LinkedList<State> queue = new LinkedList<State>();
		State currState;
		currState = new State(maze.getPlayerSquare(), null, 0, 0);
		queue.add(currState);
		// List to store the successors of the current state till they are added
		// to the queue.
		ArrayList<State> successors;
		// Boolean value to store whether a match was found in frontier
		boolean foundMatch;
		while (!queue.isEmpty()) {
			currState = queue.pop();
			explored[currState.getX()][currState.getY()] = true;
			noOfNodesExpanded++;
			if (currState.isGoal(maze)) {
				updateMaze(currState);
				maxDepthSearched = currState.getDepth();
				cost = currState.getDepth();
				return true;
			}
			successors = currState.getSuccessors(explored, maze);
			while (!successors.isEmpty()) {
				currState = successors.remove(0);
				foundMatch = false;
				Iterator<State> iter = queue.iterator();
				// Check whether the given state is in the frontier already
				while (iter.hasNext()) {
					State checkState = iter.next();
					if (checkState.getX() == currState.getX()
							&& checkState.getY() == currState.getY()) {
						foundMatch = true;
					}
				}
				// Only add states that are not already in the frontier.
				if (!foundMatch) {
					queue.add(currState);
				}
			}
			if (maxSizeOfFrontier < queue.size()) {
				maxSizeOfFrontier = queue.size();
			}
		}

		return false;
	}

	public void updateMaze(State goal) {
		State currState = goal.getParent();
		while (currState.getParent() != null) {
			maze.setOneSquare(currState.getSquare(), '.');
			currState = currState.getParent();
		}
	}
}
