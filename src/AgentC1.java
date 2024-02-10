import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;


public class AgentC1 extends AgentB{


    public AgentC1(Game game){
        super(game);
    }


    public int run(){
        if (!sps())
        	satDNF();
        return getGameState();
    }

	/**
	 * Iteratively performs DNF-based SAT solver until no more inferences can be made 
	 */
    public void satDNF(){
		// Retrieve the list of cells to prove (covered cells)
        ArrayList<int[]> to_probe = knowledgeBase.get_to_probe();

		boolean move_made = true;

		try{
			// while a move was made in the last iteration of the board
			while (move_made) {
				move_made=false;

				knowledgeBase.generateKB((x,y) -> getClausesDNF(x,y));

				if (knowledgeBase.getKB().size() == 0){break;}

				String kb_str = knowledgeBase.getKBString(") & (", "|", "&", "(", ")");
				ArrayList<int[]> to_remove = new ArrayList<int[]>();

				for (int[] cell : to_probe){
					String paint_query = kb_str + " & " + "~" + encodeCellString(cell);
					String clear_query = kb_str + " & " + encodeCellString(cell);

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
				// remove probed cells from to_probe list 
				for (int[] cell : to_remove){to_probe.remove(cell);}
			}
		} catch (ParserException e) {e.printStackTrace();}
    }

	/**
	 * Checks if formal logic expression is satisfiable or not
	 * @param dnf : Formal logic expression in pseudo-dnf form
	 * @return : true if dnf is satisfiable, false if otherwise
	 */
	public boolean isSatisfiableDNF(String dnf) throws ParserException{
		FormulaFactory f = new FormulaFactory();
		PropositionalParser p = new PropositionalParser(f);
		Formula formula = p.parse(dnf);

		// Formula cnf = formula.cnf();

		SATSolver miniSat = MiniSat.miniSat(f);
		miniSat.add(formula);
		Tristate result = miniSat.sat();

		return result.toString().equals("FALSE");
	}

	/**
	 * Builds DNF clauses for a particular cell based on its clue and immediate neighbors
	 * @param x : x coordinate of cell
	 * @param y : y coordinate of cell 
	 * @return : arraylist of clauses 
	 */
	public ArrayList<List<String>> getClausesDNF(int x, int y){
		// the clue
		int clue = game.board[x][y];

		ArrayList<int[]> painted_neighbors = getPaintedNeighbors(x,y);
		ArrayList<int[]> covered_neighbors = getCoveredNeighbors(x,y);
		ArrayList<int[]> cleared_neighbors = getClearedNeighbors(x,y);

		ArrayList<List<String>> clauses = new ArrayList<List<String>>();

		// encode that there are exactly k neighbors that are painted 
		int k = clue - painted_neighbors.size();

		if (k == 0) return clauses;

		List<List<String>> combinations = new ArrayList<>();
        combinationsDNF(covered_neighbors, k, 0, new ArrayList<>(), combinations);
		
		for (List<String> clause_arr : combinations){
			// clauses.add("(" + String.join(" & ", clause_arr) + ")"); 
			clauses.add(clause_arr);
		}

		return clauses;
	}

	/**
	 * Builds DNF clauses based on logical statement "exactly k neighbors are painted"
	 * Essentially a combinatorial "choose" function to determine all combinations of k painted neighbors
	 * @param elems : List of covered neighbor cells which could be painted 
	 * @param k : the number of neighbor cells that must be painted
	 * @param currentCombination : State of a combination (is built recursively until k cells have been chosen to be painted)
	 * @param combinations : all combinations of possible k painted neighbors 
	 */
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
}