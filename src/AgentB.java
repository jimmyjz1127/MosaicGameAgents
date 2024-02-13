import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

public class AgentB extends Agent {

	/**
	 * Constructor for Agent B
	 */
    public AgentB(Game game, boolean verbose){
        super(game, verbose);
    }

	/**
	 * Function to execute agent B's functionality (to perform SPS)
	 */
    public int run(){
        sps();
        return getGameState();
    }

	/**
	 * SPS functionality : performs SPS on game board until no more inferences can be made 
	 */
    public boolean sps(){
		boolean is_complete = true;
		boolean move_made = true;

		// While a move was made in the last iteration 
		while (move_made) {
			move_made = false;
			is_complete = true;
			
			for (int[] cell : knowledgeBase.get_to_probe()){
				if (game.state[cell[0]][cell[1]] != 0 ) continue;

				is_complete = false;
				ArrayList<int[]> clue_neighbors = getNeighborsWithClues(cell[0], cell[1], getNeighbors(cell[0],cell[1]));

				for (int[] neighbor : clue_neighbors){
					ArrayList<int[]> neighbors = getNeighbors(neighbor[0], neighbor[1]);
					ArrayList<int[]> painted_neighbors = getPaintedNeighbors(neighbor[0], neighbor[1], neighbors);
					ArrayList<int[]> covered_neighbors = getCoveredNeighbors(neighbor[0], neighbor[1], neighbors);
					int num_painted = painted_neighbors.size();
					int num_covered = covered_neighbors.size();
					int neighbor_clue = game.board[neighbor[0]][neighbor[1]];

					// Check FAN 
					if (neighbor_clue == num_painted){
						move_made = updateState(covered_neighbors, 2);
						if (verbose){
							System.out.println("\nCell to examine : [" + neighbor[0] + ", " + neighbor[1] + "]");
							System.out.print("FAN");
							game.printBoard();
						}
					} 
					// Check MAN
					else if (num_covered == neighbor_clue - num_painted){
						move_made = updateState(covered_neighbors, 1);
						if (verbose){
							System.out.println("\nCell to examine : [" + neighbor[0] + ", " + neighbor[1] + "]");
							System.out.print("MAN");
							game.printBoard();
						}
					}
				} // end iterate neighbors 
			}
		}
		return is_complete;
    }
}