import java.util.Scanner;

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
			//TODO: Part C1
			
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
		// neighbor coordinates to [i,j]
		int[][] neighbors = {{-1, -1}, {-1, 0}, {-1, 1},{0, -1},{0, 1},{1, -1}, {1, 0}, {1, 1}};

		int[][] board = game.board;
		int[][] state = game.state;

		boolean is_complete = true;
		boolean is_correct = true;

		// iterate through all cells 
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){

				if (!is_complete && !is_correct){return 0;}

				if (state[i][j] == 0) {is_complete = false;}

				if (board[i][j] != -1) { // if cell has a clue 
					int num_clues = board[i][j];
					int num_paint = (state[i][j] == 1) ? 1 : 0;
					int num_cleared = (state[i][j] == 2) ? 1 : 0;

					

					for (int[] neighbor : neighbors) {
						int newRow = i + neighbor[0];
						int newCol = j + neighbor[1];

						// Check if the new coordinates are within the grid boundaries
						if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
							if (state[newRow][newCol] == 1) {num_paint += 1;}
							else if (state[newRow][newCol] == 2){num_cleared += 1;}
							else if (state[newRow][newCol] == 0){is_complete = false;}
						}
					}

					if ((num_paint > num_clues) || (9 - num_cleared < num_clues)){is_correct = false;}
					
				}
			}// end inner for 
		}// end outer for 

		if (is_complete && is_correct) {
			return 3;
		} else if (!is_complete && is_correct) {
			return 2;
		} else if (is_complete && !is_correct) {
			return 1;
		} else {
			return 0;
		}

	}// end partA()



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

					if (current_state == 0){
						
						is_complete = false;
	
						for (int[] x : neighbors) {
							int newRow = i + x[0];
							int newCol = j + x[1];

							if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
								int neighbor_clue = board[newRow][newCol];
								int neighbor_state = state[newRow][newCol];

								if (neighbor_clue != -1){
									int num_covered = 0;
									int num_painted = 0;

									for (int[] y : neighbors){
										int newNewRow = newRow + y[0];
										int newNewCol = newCol + y[1];

										if (newNewRow >= 0 && newNewRow < board.length && newNewCol >= 0 && newNewCol < board[0].length) {
											if (state[newNewRow][newNewCol] == 1) {num_painted += 1;} 
											else if (state[newNewRow][newNewCol] == 0){num_covered += 1;}
										}
									}

									// Check FAN
									if (neighbor_clue == num_painted) {
										for (int[] y : neighbors) {
											int newNewRow = newRow + y[0];
											int newNewCol = newCol + y[1];
											if (newNewRow >= 0 && newNewRow < board.length && newNewCol >= 0 && newNewCol < board[0].length){
												if (state[newNewRow][newNewCol] == 0){
													state[newNewRow][newNewCol] = 2;
													move_made = true;
												}
											}
										}
									} 
									// Check MAN
									else if (num_covered == neighbor_clue - num_painted) {
										for (int[] y : neighbors) {
											int newNewRow = newRow + y[0];
											int newNewCol = newCol + y[1];
											if (newNewRow >= 0 && newNewRow < board.length && newNewCol >= 0 && newNewCol < board[0].length){
												if (state[newNewRow][newNewCol] == 0){
													state[newNewRow][newNewCol] = 1;
													move_made = true;
												}
											}
										}
									}
								} // end if neighbor has clue 
							} // end if neighbor in bounds 
						} // end iterate neighbors 
					} // end if current cell is covered 
				} // end inner loop
			} // end outer loop
		} // while loop

		if (is_complete) {return 3;} 
		else {return 2;}
	}
	

	public static int partC1(Game game) {
		int[][] board = game.board;
		int[][] state = game.state;

		
	}
}
