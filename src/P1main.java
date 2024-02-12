import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.sat4j.specs.TimeoutException;
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
			AgentA agentA = new AgentA(board);
			output = agentA.run();
			break;

		case "B":
			AgentB agentB = new AgentB(board);
			output = agentB.run();
			break;

		case "C1":
			AgentC1 agentC1 = new AgentC1(board);
			output = agentC1.run();
			break;

		case "C2":
			AgentC2 agentC2 = new AgentC2(board);
			output = agentC2.run();
			
			break;

		case "C3":
			AgentC3 agentC3 = new AgentC3(board);
			output = agentC3.run();
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

}
