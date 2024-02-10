import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;



public class Agent{
    KnowledgeBase knowledgeBase;
    Game game;


    public Agent(Game game){
        this.game = game;
        this.knowledgeBase = new KnowledgeBase(game);
    }


    public int getGameState(){
        int[][] board = game.board;
		int[][] state = game.state;

		boolean is_complete = true;
		boolean is_correct = true;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (!is_complete && !is_correct){return 0;} // if game not complete nor correct
				if (state[i][j] == 0) {is_complete = false;} // if encounter covered cell, set game as incomplete

				if (board[i][j] != -1){
					int num_clues = board[i][j];
					int num_paint = getPaintedNeighbors(i,j).size();
					int num_cleared = getClearedNeighbors(i,j).size();

					if ((num_paint > num_clues) || (9 - num_cleared < num_clues)){is_correct = false;}
				}			
			}
		}

		if (is_complete && is_correct) {return 3;}
		else if (!is_complete && is_correct) {return 2;}
		else if (is_complete && !is_correct) {return 1;} 
		return 0;
    }

    /**
	 * Returns all valid neighbor cells for given cell coordinates
	 * @param x : x coordinate 
	 * @param y : y coordinate 
	 * @return : array list of int[] coordinates  
	 */
	public ArrayList<int[]> getNeighbors(int x, int y){
		ArrayList<int[]> neighbors = new ArrayList<int[]>();

		neighbors.add(new int[]{x-1, y-1});
		neighbors.add(new int[]{x-1, y});
		neighbors.add(new int[]{x-1, y+1});
		neighbors.add(new int[]{x, y-1});
		neighbors.add(new int[]{x, y+1});
		neighbors.add(new int[]{x+1, y-1});
		neighbors.add(new int[]{x+1, y});
		neighbors.add(new int[]{x+1, y+1});
		neighbors.add(new int[]{x,y});

		neighbors = new ArrayList<>(neighbors.stream().filter(cell -> cell[0] >= 0 && cell[0] < game.size && cell[1] < game.size && cell[1] >= 0).toList());

		return neighbors;
	}

	public ArrayList<int[]> getCoveredNeighbors(int x, int y){
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> covered_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 0){
				covered_neighbors.add(neighbor);
			}
		}

		return covered_neighbors;
	}

	public ArrayList<int[]> getPaintedNeighbors(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> painted_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 1){
				painted_neighbors.add(neighbor);
			}
		}

		return painted_neighbors;
	}

	public ArrayList<int[]> getClearedNeighbors(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> cleared_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 2){
				cleared_neighbors.add(neighbor);
			}
		}

		return cleared_neighbors;
	}

    public ArrayList<int[]> getNeighborsWithClues(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> clue_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] != -1){
				clue_neighbors.add(neighbor);
			}
		}

		return clue_neighbors;
	} 


    public String encodeCellString(int[] cell){
		return Integer.toString(cell[0]) + '#' + Integer.toString(cell[1]);
	}

    public boolean updateState(ArrayList<int[]> coords, int symbol){
        boolean move_made = false;
        for (int[] coord : coords) {
            game.state[coord[0]][coord[1]] = symbol;
            move_made = true;
        }
        
        return move_made;
    }

}