import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;

public class AgentC2 extends AgentB{

    public AgentC2(Game game, boolean verbose){
        super(game, verbose);
    }

    public int run(){
        if (!sps()){
			knowledgeBase.updateToProbe();
        	satCNF();
	    }
			
		else if (verbose){System.out.println("Game complete with SPS! No need for formal logic strategy.");}
        return getGameState();
    }

    /**
     * Runs the CNF based SAT solver strategy 
     */
    public void satCNF(){
        ArrayList<int[]> to_probe = knowledgeBase.get_to_probe();

		boolean move_made = true;

		try{
			while (move_made) {
				move_made=false;

				ArrayList<int[]> to_remove = new ArrayList<int[]>();

				knowledgeBase.generateKB((x,y) -> getClausesCNF(x,y));

				if (knowledgeBase.getKB().size() == 0) {break;}
				String kb_str = knowledgeBase.getKBString(" & ", " & ", " | ", "", "");
				ArrayList<int[]> clauses = knowledgeBase.getDimacs();

				for (int[] cell : to_probe){

					int[] paint_query = new int[]{-1 * knowledgeBase.convert(knowledgeBase.encodeCellString(cell))};
					int[] clear_query = new int[]{knowledgeBase.convert(knowledgeBase.encodeCellString(cell))};

					boolean result1 = isSatisfiableCNF(clauses, paint_query, knowledgeBase.getMaxIndex());
					boolean result2 = isSatisfiableCNF(clauses, clear_query, knowledgeBase.getMaxIndex());

					if (!result1){
						game.state[cell[0]][cell[1]] = 1;
						move_made = true;
						to_remove.add(cell);

						if (verbose){
							System.out.println("Query : is cell [" + cell[0] + ", " + cell[1] + "] paint");
							System.out.println("Clause & Query : \n" + kb_str + " & ~" + encodeCellString(cell));
							game.printBoard();
						}
					}  else if (!result2) {
						game.state[cell[0]][cell[1]] = 2;
						move_made = true;
						to_remove.add(cell);

						if (verbose){
							System.out.println("Query : is cell [" + cell[0] + ", " + cell[1] + "] clear");
							System.out.println("Clause & Query : \n" + kb_str + " & " + encodeCellString(cell));
							game.printBoard();
						}
					}
				}
				for (int[] cell : to_remove){to_probe.remove(cell);}
			}
		} catch (TimeoutException e) {e.printStackTrace();}
    }

	/**
	 * Determines if a logical sentence (in CNF) is satisfiable or not 
	 * @param clauses : clauses of knowledge base 
	 * @param query : query 
	 * @param max_var : the number of literals in clauses and query combined 
	 * @return : true if satisfiable, false otherwise
	 */
    public  boolean isSatisfiableCNF(ArrayList<int[]> clauses, int[] query, int max_var) throws TimeoutException{
		ISolver solver = SolverFactory.newDefault();
		solver.newVar(max_var);
		solver.setExpectedNumberOfClauses(clauses.size() + 1);

		int index = 0;
		try{
			for (int[] clause : clauses){
				if (clause.length != 0){
					solver.addClause(new VecInt(clause));
				} 		
				index++;			
			}
			solver.addClause(new VecInt(query));
		} catch (ContradictionException e) {
			return false;
		}

		IProblem problem = solver;
		return problem.isSatisfiable();
	}

    public ArrayList<List<String>> getClausesCNF(int x, int y){
		// the clue
		int clue = game.board[x][y];

		ArrayList<int[]> neighbors = getNeighbors(x,y);
		ArrayList<int[]> painted_neighbors = getPaintedNeighbors(x,y, neighbors);
		ArrayList<int[]> covered_neighbors = getCoveredNeighbors(x,y, neighbors);
		ArrayList<int[]> cleared_neighbors = getClearedNeighbors(x,y, neighbors);

		ArrayList<List<String>> clauses = new ArrayList<List<String>>();

		// encode that there are exactly k neighbors that are painted 
		int k = clue - painted_neighbors.size();

        if (k == 0) return clauses;

		// "at most k paint"
		int c = k + 1;
		List<List<String>> combinations = new ArrayList<>();
        combinationsCNF(covered_neighbors, c, 0, new ArrayList<>(), combinations, "~");

		for (List<String> clause_arr : combinations){
			clauses.add(clause_arr);
		}

		// "at most n-k non-paint"
		c = covered_neighbors.size() - k + 1;
		combinations = new ArrayList<>();
        combinationsCNF(covered_neighbors, c, 0, new ArrayList<>(), combinations, "");

		for (List<String> clause_arr : combinations){
			clauses.add(clause_arr);
		}
		return clauses;
	}

    public void combinationsCNF(List<int[]> elems, int k, int start, List<String> currentCombination, List<List<String>> combinations, String symbol) {
        // When the combination is complete
        if (k == 0) {
			if (currentCombination.size() != 0)
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
}