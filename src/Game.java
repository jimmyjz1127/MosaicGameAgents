/*
 * Starter code
 * Game class: used to hold the board, parse the input, print the output
 * CS5011 - P1
 * 
 * author: a.toniolo
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.List;

public class Game {
	int[][] board;
	int[][] state;
	int[][] game;
	int size;

	/*
	 * board is the one the agent will play with 
	 * state indicates the status of the cells
	 * game is the hidden board, the one the agent has to be able to find
	 */


	public Game() {
		size=2;
		board=new int[size][size];
		state=new int[size][size];
		game=new int[size][size];
	}


	public boolean setGame(String map) {
		try {
			String[] lines=map.split(";"); //rows
			size=lines.length;
			board=new int[size][size];
			state=new int[size][size];
			game=new int[size][size];
			for(int r=0; r<size; r++) { // iterate each row 
				String line=lines[r];
				String[] set=line.split(","); // columns
				int[] bl=new int[size];
				int[] sl=new int[size];
				int[] gl=new int[size];
				for(int c=0;c<size;c++) { 
					String ch=set[c];
					char i=ch.charAt(0); // status
					char e=ch.charAt(ch.length()-1); //paint
					String m=ch.substring(1,ch.length()-1); //clue
					int state=0;
					int paint=0;
					int clue=0;
					switch(i) {
					case '*':
						state=1;
						break;
					case '_':
						state=2;
						break;
					case '.':
						state=0;
					}
					if(m.equals("-")) {
						clue=-1;
					}else {
						clue=Integer.parseInt(m);
					}
					switch(e) {
					case '*':
						paint=1;
						break;
					case '_':
						paint=2;
						break;
					}
					bl[c]=clue;
					sl[c]=state;
					gl[c]=paint;
				}
				board[r]=bl;
				state[r]=sl;
				game[r]=gl;
				
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		

	}
	public void printGame() {
		//this method is used to print the initial game view

		System.out.println();
		// first line
		System.out.format("%4s","   ");
		for (int c = 0; c < board[0].length; c++) {
			System.out.format("%4s",c);
		}
		System.out.println();
		// second line
		System.out.format("%5s","   ");
		for (int c = 0; c < board[0].length; c++) {
			System.out.print("--- ");// separator
		}
		System.out.println();
		// the board 
		for (int r = 0; r < board.length; r++) {
			System.out.print(" "+ r + "| ");// index+separator
			for (int c = 0; c < board[0].length; c++) {
				String code="";
				//print clues
				if(board[r][c]==-1) {
					code+=" ";
				}else {
					code+=board[r][c];
				}
				//print paint
				switch(game[r][c]) {
				case 1:
					code+="*";
					break;
				case 2:
					code+="_";
					break;
				}
				System.out.format("%4s",code);
			}
			System.out.println();
		}
		System.out.println();
	}


	public void printBoard() {
		//this is used to print the agent view

		System.out.println();
		// first line
		System.out.format("%4s","   ");
		for (int c = 0; c < board[0].length; c++) {
			System.out.format("%4s",c);
		}
		System.out.println();
		// second line
		System.out.format("%5s","   ");
		for (int c = 0; c < board[0].length; c++) {
			System.out.print("--- ");// separator
		}
		System.out.println();
		// the board 
		for (int r = 0; r < board.length; r++) {
			System.out.print(" "+ r + "| ");// index+separator
			for (int c = 0; c < board[0].length; c++) {
				String code="";
				//print state
				switch(state[r][c]) {
				case 0:
					code+=".";
					break;
				case 1:
					code+="*";
					break;
				case 2:
					code+="_";
					break;
				}
				//print clues
				if(board[r][c]==-1) {
					code+="-";
				} else {
					code+=board[r][c];
				}
				System.out.format("%4s",code);
			}
			System.out.println();
		}
		System.out.println();

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

		neighbors = new ArrayList<>(neighbors.stream().filter(cell -> cell[0] >= 0 && cell[0] < size && cell[1] < size && cell[1] >= 0).toList());

		return neighbors;
	}

	public ArrayList<int[]> getCoveredNeighbors(int x, int y){
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> covered_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] == 0){
				covered_neighbors.add(neighbor);
			}
		}

		return covered_neighbors;
	}

	public ArrayList<int[]> getPaintedNeighbors(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> painted_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] == 1){
				painted_neighbors.add(neighbor);
			}
		}

		return painted_neighbors;
	}

	public ArrayList<int[]> getClearedNeighbors(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> cleared_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] == 2){
				cleared_neighbors.add(neighbor);
			}
		}

		return cleared_neighbors;
	}

	public ArrayList<int[]> getNeighborsWithClues(int x, int y) {
		ArrayList<int[]> neighbors = getNeighbors(x,y);

		ArrayList<int[]> clue_neighbors = new ArrayList<>();

		for (int[] neighbor : neighbors){
			if (state[neighbor[0]][neighbor[1]] != -1){
				clue_neighbors.add(neighbor);
			}
		}

		return clue_neighbors;
	} 

	public boolean updateState(ArrayList<int[]> coords, int symbol){
		boolean move_made = false;
		for (int[] coord : coords) {
			state[coord[0]][coord[1]] = symbol;
			move_made = true;
		}
		
		return move_made;
	}

	public String getClausesDNF(int x, int y){
		// the clue
		int clue = board[x][y];

		ArrayList<int[]> painted_neighbors = getPaintedNeighbors(x,y);
		ArrayList<int[]> covered_neighbors = getCoveredNeighbors(x,y);
		ArrayList<int[]> cleared_neighbors = getClearedNeighbors(x,y);

		// encode that there are exactly k neighbors that are painted 
		int k = clue - painted_neighbors.size();

		List<List<String>> combinations = new ArrayList<>();
        combinationsDNF(covered_neighbors, k, 0, new ArrayList<>(), combinations);

		ArrayList<String> clauses = new ArrayList<String>();

		for (List<String> clause_arr : combinations){
			clauses.add("(" + String.join(" & ", clause_arr) + ")"); 
		}

		return String.join(" | ", clauses);
	}
	

	public void combinationsDNF(List<int[]> elems, int k, int start, List<String> currentCombination, List<List<String>> combinations) {
        // When the combination is complete
        if (k == 0) {
			ArrayList<String> x = new ArrayList<>(currentCombination);
			for (int[] elem : elems) {
				String elem_string = encodeCellString(elem);

				if (!currentCombination.contains(elem_string))
					x.add("~" + elem_string);
			}
			combinations.add(new ArrayList<>(x));
            return;
        }

        for (int i = start; i < elems.size(); i++) {
            // Include the current element with a "~" appended
            currentCombination.add(encodeCellString(elems.get(i)));

			combinationsDNF(elems, k - 1, i + 1, currentCombination, combinations);
			
            // Remove the current element to try the next possibility
            currentCombination.remove(currentCombination.size() - 1);
        }
    } 

	public String getClausesCNF(int x, int y){
		// the clue
		int clue = board[x][y];

		ArrayList<int[]> painted_neighbors = getPaintedNeighbors(x,y);
		ArrayList<int[]> covered_neighbors = getCoveredNeighbors(x,y);
		ArrayList<int[]> cleared_neighbors = getClearedNeighbors(x,y);

		ArrayList<String> clauses = new ArrayList<String>();

		// encode that there are exactly k neighbors that are painted 
		int k = clue - painted_neighbors.size();

		// "at least k neighbors are paint"
		int c = covered_neighbors.size() - k + 1;
		List<List<String>> combinations = new ArrayList<>();
        combinationsCNF(covered_neighbors, c, 0, new ArrayList<>(), combinations, "");

		for (List<String> clause_arr : combinations){
			clauses.add("(" + String.join(" | ", clause_arr) + ")"); 
		}


		// "at most k neighbors are paint"
		c = covered_neighbors.size() - k;
		combinations = new ArrayList<>();
        combinationsCNF(covered_neighbors, c, 0, new ArrayList<>(), combinations, "~");

		for (List<String> clause_arr : combinations){
			clauses.add("(" + String.join(" | ", clause_arr) + ")"); 
		}

		return String.join(" & ", clauses);
	}


	public void combinationsCNF(List<int[]> elems, int k, int start, List<String> currentCombination, List<List<String>> combinations, String symbol) {
        // When the combination is complete
        if (k == 0) {
			combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int i = start; i < elems.size(); i++) {
            // Include the current element with a "~" appended
            currentCombination.add(symbol + encodeCellString(elems.get(i)));

			combinationsCNF(elems, k - 1, i + 1, currentCombination, combinations, symbol);
			
            // Remove the current element to try the next possibility
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

	public String encodeCellString(int[] cell){
		return Integer.toString(cell[0]) + '#' + Integer.toString(cell[1]);
	}


}
