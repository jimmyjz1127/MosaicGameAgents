import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

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
			output = partC1(board);
			break;

		case "C2":
			output = partC2(board);
			
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
	


	public static int partC1(Game game){
		KnowledgeBase kb = new KnowledgeBase(game);

		ArrayList<int[]> to_probe = kb.get_to_probe();

		boolean move_made = true;

		try{
			while (move_made) {
				move_made=false;

				kb.generateKB((x,y) -> game.getClausesDNF(x,y));
				String kb_str = kb.getKBString(") & (", "(", ")");
				ArrayList<int[]> to_remove = new ArrayList<int[]>();

				for (int[] cell : to_probe){
					String paint_query = kb_str + " & " + "~" + game.encodeCellString(cell);
					String clear_query = kb_str + " & " + game.encodeCellString(cell);

					boolean result1 = isSatisfiableDNF(paint_query);
					boolean result2 = isSatisfiableDNF(clear_query);
					
					if (result1){
						game.state[cell[0]][cell[1]] = 1;
						move_made = true;
						to_remove.add(cell);
					}
					else if (result2) {
						game.state[cell[0]][cell[1]] = 2;
						move_made = true;
						to_remove.add(cell);
					} 
				}

				for (int[] cell : to_remove){to_probe.remove(cell);}
			}
		} catch (ParserException e) {e.printStackTrace();}
		return partA(game);	
 	}

	public static int partC2(Game game){
		KnowledgeBase kb = new KnowledgeBase(game);

		ArrayList<int[]> to_probe = kb.get_to_probe();

		boolean move_made = true;

		// try{
			while (move_made) {
				move_made=false;

				kb.generateKB((x,y) -> game.getClausesCNF(x,y));
				String kb_str = kb.getKBString(" & ", "", "");
				kb.printClauses();
				return 1;
				// ArrayList<int[]> to_remove = new ArrayList<int[]>();

				// for (int[] cell : to_probe){
				// 	String paint_query = kb_str + " & " + "~" + game.encodeCellString(cell);
				// 	String clear_query = kb_str + " & " + game.encodeCellString(cell);

				// 	boolean result1 = isSatisfiableCNF(paint_query);
				// 	boolean result2 = isSatisfiableCNF(clear_query);
					
				// 	if (result1){
				// 		game.state[cell[0]][cell[1]] = 1;
				// 		move_made = true;
				// 		to_remove.add(cell);
				// 	}
				// 	else if (result2) {
				// 		game.state[cell[0]][cell[1]] = 2;
				// 		move_made = true;
				// 		to_remove.add(cell);
				// 	} 
				// }

				// for (int[] cell : to_remove){to_probe.remove(cell);}
			}
		// } catch (ParserException e) {e.printStackTrace();}
		return partA(game);	
 	}

	public static boolean isSatisfiableDNF(String dnf) throws ParserException{
		FormulaFactory f = new FormulaFactory();
		PropositionalParser p = new PropositionalParser(f);
		Formula formula = p.parse(dnf);

		// Formula cnf = formula.cnf();

		SATSolver miniSat = MiniSat.miniSat(f);
		miniSat.add(formula);
		Tristate result = miniSat.sat();

		return result.toString().equals("FALSE");
	}


	// public static boolean isSatisfiableCNF(String cnf) throws ParserException{
	// 	HashMap<String, int> str_int = new HashMap<String, int>();
	// 	HashMap<int, String> int_str = new HashMap<int, String>();

	// 	ISolver solver = SolverFactory.newDefault();

	// 	solver.newVar(countVars(cnf));
	// 	solver.setExpectedNumberOfClauses(countClause(cnf));

	// 	int index = 0;
	// 	for ()
	// }

	public static int countVars(String exp){
		int count = 0;
		for (char c : exp.toCharArray()){
			if (c == '#') count++;
		}
		return count;
	}


	public static int countClause(String clauses, String symbol){
		int count = 0;
		
		return clauses.split(symbol).length;
	}


}
