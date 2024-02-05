import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;

/*
 * Starter code
 * Example of Main Class 
 * CS5011 - P1
 * 
 * author: a.toniolo
 */

public class P1main {
	// neighbor coordinates to [i,j]
	public static int[][] neighbors1 = {
					{-1, -1}, {-1, 0}, {-1, 1},
					{0, -1},           {0, 1},
					{1, -1}, {1, 0}, {1, 1}
				};

	public static int[][] neighbors = {
					{-1, -1}, {-1, 0}, {-1, 1},
					{0, -1}, {0, 0},   {0, 1},
					{1, -1}, {1, 0}, {1, 1}
				};


	public static void main(String[] args) {
 
		//check inputs
 
		boolean verbose=false;

		if(args.length<1) {
			System.out.println("usage: ./playSweeper.sh <A|B|C1|C2|C3|D> [verbose] [<any other param>]");
			System.exit(1);
		}
		if (args.length>1 && args[1].equals("verbose") ){
			verbose=true; //prints additional details if true
		}
		//get specific game
		System.out.println("Please enter the game spec:");
		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine(); //requires a single line as specified in the .txt examples 
		System.out.println(line);
		Game board=new Game();
		boolean parse=board.setGame(line);
		if(!parse) {
			System.out.println("Something went wrong with your game spec, please try again");
			System.exit(1);
		}

		//start
		System.out.println("Agent " + args[0] + " plays  \n");
		System.out.println("Game:");
		board.printGame();
		System.out.println("Intitial view:");
		board.printBoard();

		System.out.println("Start!");

		int output=0;

		/*
			board : clues 
			state : whether currently paint or clear or covered 
			game  : should be painted or cleared 
		*/

		switch (args[0]) {
		case "A":
			output = partA(board);
			break;

		case "B":
			output = partB(board);
			break;

		case "C1":
			output = 1;
			String dnf = board.getClauses(0,0);
			System.out.println(dnf);
			break;

		case "C2":
			//TODO: Part C2
			
			break;

		case "C3":
			//TODO: Part C3
			
			break;

		case "D":
			break;


		}

		board.printBoard();
		switch(output) {

		/* output options:
		 * 0=!complete && !correct
		 * 1=complete && !correct
		 * 2=!complete && correct
		 * 3=complete && correct
		 */


		case 0:
			System.out.println("\nResult: Game not terminated and incorrect\n");
			break;

		case 1:
			System.out.println("\nResult: Agent loses: Game terminated but incorrect \n");
			break;

		case 2:
			System.out.println("\nResult: Game not terminated but correct \n");
			break;

		case 3:
			System.out.println("\nResult: Agent wins: Game terminated and correct \n");
			break;

		default:
			System.out.println("\nResult: Unknown\n");

		}

	}


	/*
		State
			0 : covered
			1 : paint
			2 : clear 

		Clue 
			-1 : no clue 
		
		Game 
			1 : paint 
			2 : clear 
	*/


	
	

	/**
	 * Function to implement the functionality of part A 
	 * @param game   the game board object 
	 * @return 	     0 : !complete && !correct, 
	 * 				 1 : complete && !correct, 
	 * 				 2 : !complete && correct,
	 * 				 3 : complete && correct 
	 */
	public static int partA(Game game){
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
					int num_paint = game.getPaintedNeighbors(i,j).size();
					int num_cleared = game.getClearedNeighbors(i,j).size();

					if ((num_paint > num_clues) || (9 - num_cleared < num_clues)){is_correct = false;}
				}			
			}
		}

		if (is_complete && is_correct) {return 3;}
		else if (!is_complete && is_correct) {return 2;}
		else if (is_complete && !is_correct) {return 1;} 

		return 0;
		
	}

	/*
		State
			0 : covered
			1 : paint
			2 : clear 

		Clue 
			-1 : no clue 
		
		Game 
			1 : paint 
			2 : clear 
	*/

	public static int partB(Game game) {
		int[][] board = game.board;
		int[][] state = game.state;

		boolean is_complete = true;
		boolean move_made = true;

		while (move_made) {
			move_made = false;
			is_complete = true;
			
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					int current_clue = board[i][j];
					int current_state = state[i][j];

					if (current_state == 0) {
						is_complete = false; 

						ArrayList<int[]> clue_neighbors = game.getNeighborsWithClues(i,j);

						for (int[] neighbor : clue_neighbors){
							ArrayList<int[]> painted_neighbors = game.getPaintedNeighbors(neighbor[0], neighbor[1]);
							ArrayList<int[]> covered_neighbors = game.getCoveredNeighbors(neighbor[0], neighbor[1]);
							int num_painted = painted_neighbors.size();
							int num_covered = covered_neighbors.size();
							int neighbor_clue = board[neighbor[0]][neighbor[1]];

							// Check FAN 
							if (neighbor_clue == num_painted){
								move_made = game.updateState(covered_neighbors, 2);
							} 
							// Check MAN
							else if (num_covered == neighbor_clue - num_painted){
								move_made = game.updateState(covered_neighbors, 1);
							}
							
						} // end iterate neighbors 
					} // end check current_state == 0
				} // end inner for 
			} // end outer for 
		}

		if (is_complete) {return 3;} 
		return 2;
	}
	

	// public static int partC1(Game game) {
	// 	int[][] board = game.board;
	// 	int[][] state = game.state;


	// }
}
