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
	boolean verbose;

	/**
	 * Agent constructor 
	 * @param game : the game object 
	 * @param verbose : verbosity flag 
	 */
    public Agent(Game game, boolean verbose){
        this.game = game;
        this.knowledgeBase = new KnowledgeBase(game);
		this.verbose = verbose;
    }

	/**
	 * Checks if a given game board and state are consistent/valid 
	 * @param board : the board containing clues of the game 
	 * @param state : the board containing state of cells in the game 
	 * @return : true if game state consistent with clues, false otherwise
	 */
	public boolean checkValid(int[][] board, int[][] state){
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				
				if (board[i][j] != -1){
					int num_clues = board[i][j];
					ArrayList<int[]> neighbors = getNeighbors(i,j);
					int num_neighbors = neighbors.size();
					int num_paint = getPaintedNeighbors(state, i, j, neighbors).size();
					int num_cleared = getClearedNeighbors(state, i, j, neighbors).size();

					if ((num_paint > num_clues) || (num_neighbors - num_cleared < num_clues)){return false;}
				}
			}
		}
		return true;
	}

	/**
	 * Returns integer representing state of game as per specifications of Agent A 
	 */
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
					ArrayList<int[]> neighbors = getNeighbors(i,j);
					int num_neighbors = neighbors.size();
					int num_paint = getPaintedNeighbors(i,j, neighbors).size();
					int num_cleared = getClearedNeighbors(i,j, neighbors).size();

					if ((num_paint > num_clues) || (num_neighbors - num_cleared < num_clues)){is_correct = false;}
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

		neighbors.add(new int[]{x,y});
		neighbors.add(new int[]{x-1, y-1});
		neighbors.add(new int[]{x-1, y});
		neighbors.add(new int[]{x-1, y+1});
		neighbors.add(new int[]{x, y-1});
		neighbors.add(new int[]{x, y+1});
		neighbors.add(new int[]{x+1, y-1});
		neighbors.add(new int[]{x+1, y});
		neighbors.add(new int[]{x+1, y+1});

		neighbors = new ArrayList<>(neighbors.stream().filter(cell -> cell[0] >= 0 && cell[0] < game.size && cell[1] < game.size && cell[1] >= 0).toList());
		return neighbors;
	}

	/**
	 * Returns all neighboring cells that are covered for a given cell
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getCoveredNeighbors(int x, int y, ArrayList<int[]> neighbors){
		ArrayList<int[]> covered_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 0){
				covered_neighbors.add(neighbor);
			}
		}

		return covered_neighbors;
	}

	/**
	 * Returns all neighboring cells that are painted for a given cell
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getPaintedNeighbors(int x, int y, ArrayList<int[]> neighbors) {
		ArrayList<int[]> painted_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 1){
				painted_neighbors.add(neighbor);
			}
		}

		return painted_neighbors;
	}

	/**
	 * Returns all neighboring cells that are painted for a given cell
	 * @param state : use given state board to determine state of neighboring cells 
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getPaintedNeighbors(int[][] state, int x, int y, ArrayList<int[]> neighbors) {
		ArrayList<int[]> painted_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] == 1){
				painted_neighbors.add(neighbor);
			}
		}

		return painted_neighbors;
	}

	/**
	 * Returns all neighboring cells that are clear for a given cell
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getClearedNeighbors(int x, int y, ArrayList<int[]> neighbors) {
		ArrayList<int[]> cleared_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] == 2){
				cleared_neighbors.add(neighbor);
			}
		}

		return cleared_neighbors;
	}

	/**
	 * Returns all neighboring cells that are clear for a given cell
	 * @param state : use given state board to determine state of neighboring cells 
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getClearedNeighbors(int[][] state, int x, int y, ArrayList<int[]> neighbors) {
		ArrayList<int[]> cleared_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] == 2){
				cleared_neighbors.add(neighbor);
			}
		}

		return cleared_neighbors;
	}

	/**
	 * Returns all neighboring cells containing clues for a given cell
	 * @param x : x coordinate of cell
	 * @param y ; y coordinate of cell
	 * @param neighbors : arraylist of all neighbors for given cell
	 * @return : array list of neighbor cells satisfying condition 
	 */
	public ArrayList<int[]> getNeighborsWithClues(int x, int y, ArrayList<int[]> neighbors) {
		ArrayList<int[]> clue_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (game.state[neighbor[0]][neighbor[1]] != -1){
				clue_neighbors.add(neighbor);
			}
		}

		return clue_neighbors;
	} 


	/**
	 * Converts a cell array [x,y] into string encoding "x#y"
	 * @param : cell array containing coordinates [x,y]
	 * @return : string encoding of cell x#y
	 */
    public String encodeCellString(int[] cell){
		return Integer.toString(cell[0]) + '#' + Integer.toString(cell[1]);
	}

	/**
	 * Converts string encoding of cell into array form "x#y" => [x,y]
	 * @param cell : string encoding of cell
	 * @return : array encoding of cell 
	 */
	public int[] encodeCellArray(String cell){
		int[] coord = new int[2];

		coord[0] = Integer.parseInt(cell.split("#")[0]);
		coord[1] = Integer.parseInt(cell.split("#")[1]);

		return coord;
	}

	/**
	 * Updates game state for a list of given cells 
	 * @param coords : coordinates of cells to update 
	 * @param symbol : symbol to update cell state to
	 * @return : true if at least one cell was updated, false otherwise 
	 */
    public boolean updateState(ArrayList<int[]> coords, int symbol){
        boolean move_made = false;
        for (int[] coord : coords) {
            game.state[coord[0]][coord[1]] = symbol;
            move_made = true;
        }
        
        return move_made;
    }

}