import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;


/**
 * Interface for setting up anonymous function (used in generateKB() function below)
 */
interface ClauseFunction{
    ArrayList<List<String>> apply(int x, int y);
}

public class KnowledgeBase {

    Game game;

    // The total number of cells in board 
    private int max_index = 0;

    // setup bijection between cnf literal and dimacs integer 
    private Map<String, Integer> str_int = new HashMap<String, Integer>();
    private Map<Integer, String> int_str = new HashMap<Integer, String>();    

    private ArrayList<int[]> to_probe = new ArrayList<int[]>();   //cells to probe 
    private ArrayList<int[]> clue_cells = new ArrayList<int[]>(); //cells with clues 

    // Contains clauses (either CNF or DNF)
    private ArrayList<ArrayList<List<String>>> kb = new ArrayList<ArrayList<List<String>>>(); // clauses  

    
    /**
     * Constructor 
     * @param game : game objects 
     */
    public KnowledgeBase(Game game){
        this.game = game;
        setupKB();
    } 

    /**
     * Sets up the initial knowledge base configurations: 
     * 1) Adds all covered cells to "to_probe" arraylist indicating these cells must be probed 
     * 2) Adds all cells containing clues ot "clue_cells" arraylist to be used for building logical clauses 
     * 3) Builds bijection between integer index and cell for DIMACS 
     */
    public void setupKB(){
        for (int i = 0; i < game.board.length; i++){
            for (int j = 0; j < game.board[0].length; j++){
                if (game.state[i][j] == 0) to_probe.add(new int[]{i,j});
                if (game.board[i][j] != -1) clue_cells.add(new int[]{i,j});

                String cell_string = encodeCellString(new int[]{i,j});

                // Build bijection between integer index and unique cell on board
                if (!str_int.containsKey(cell_string)){
                    max_index += 1;

                    str_int.put(cell_string, max_index);
                    int_str.put(max_index, cell_string);
                }
            }
        }
    }


    /**
     * Turns a cell into a string encoding 
     * @param cell : int array of form [x,y] where x is row-index and y is column-index
     * @return : for given x & y coodrinate, returns encoding of form x#y
     */
    public String encodeCellString(int[] cell){
		return Integer.toString(cell[0]) + '#' + Integer.toString(cell[1]);
	}

    /**
     * Getter function of to_probe array list 
     */
    public ArrayList<int[]> get_to_probe(){
        return to_probe;
    }

    /**
     * Getter function of clue_cells arraylist
     */
    public ArrayList<int[]> get_clue_cells(){
        return clue_cells;
    }

    /**
     * Convert function for converting cell integer index to string representation
     */
    public String convert(int index){
        return int_str.get(index);
    }

    /**
     * Convert function for converting cell string representation to integer index 
     */
    public int convert(String literal){
        return str_int.get(literal);
    }

    /**
     * Generates formal logic knowledge base
     * @param func : anonymous function which performs search around given cell to build logic clauses 
     */
    public void generateKB(ClauseFunction func){
        kb = new ArrayList<ArrayList<List<String>>>();
        for (int[] cell : clue_cells){
            ArrayList<List<String>> clauses = func.apply(cell[0], cell[1]);

            if (clauses.size() != 0){
                kb.add(clauses);
            }
        }
    }

    public void pruneClueCells(){ 
        ArrayList<int[]> new_clue_cells = new ArrayList<int[]>();
        for (int cell[] : clue_cells){
            if (!isCellComplete(cell[0], cell[1])){
                new_clue_cells.add(cell);
            }
        }
        clue_cells = new_clue_cells;
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

    public boolean isCellComplete(int x, int y){
        for (int[] cell : getNeighbors(x,y)){
            if (game.state[cell[0]][cell[1]] == 0) {return false;}
        }
        return true;
	}


    /**
     * Getter function of knowledge base arraylist 
     */
    public ArrayList<ArrayList<List<String>>> getKB(){
        return kb; 
    }

    /**
     * Converts knowledge base into string suitable for SAT solver 
     * @param delim1 : outer most delimeter - e.g : [(A & B) | (A & B)] <delim1> [(A & C) | (C & B)]
     * @param delim2 : middle delimeter - e.g : [(A & B) <delim2> (A & B)] | [(A & C) <delim2> (C & B)]
     * @param delim3 : inner most delimeter - between logical literals themselves
     */
    public String getKBString(String delim1, String delim2, String delim3, String prefix, String suffix){
        StringJoiner joiner = new StringJoiner(delim1, prefix, suffix);
        for (ArrayList<List<String>> exp : kb){
            ArrayList<String> clauses = new ArrayList<String>();
            for (List<String> clause : exp) {
                clauses.add("(" + String.join(delim3, clause) + ")");
            }
            joiner.add(String.join(delim2, clauses));
        }
        String result = joiner.toString();
        return result;
    }

    /**
     * Converts knowledge base into DIMACS representation 
     * @return : returns arraylist of disjunctive clauses containing integer literals as per DIMACS 
     */
    public ArrayList<int[]> getDimacs(){
        ArrayList<int[]> clauses = new ArrayList<int[]>();
        for (ArrayList<List<String>> exp : kb){
            for (List<String> clause_arr : exp){
                int[] clause = new int[clause_arr.size()]; 

                for (int i = 0; i < clause_arr.size(); i++){
                    if (clause_arr.get(i).substring(0,1).equals("~")){
                        clause[i] = -1 * str_int.get(clause_arr.get(i).substring(1));
                    } else {                        
                        clause[i] = str_int.get(clause_arr.get(i));
                    }
                }
                if (clause.length != 0)
                    clauses.add(clause);
            }
        }
        return clauses;
    }


    public void updateToProbe(){
        ArrayList<int[]> new_probe = new ArrayList<int[]>();
        for (int[] elem : to_probe){
            if (game.state[elem[0]][elem[1]] == 0){
                new_probe.add(elem);
            }
        }

        to_probe = new_probe;
    }

    public void removeFromProbe(ArrayList<int[]> cells){
        for (int[] cell : cells){
            to_probe.remove(cell);
        }
    }


    public void printClauses(){
        for (ArrayList<List<String>> clauses : kb) {
            for (List<String> clause : clauses){
                System.out.println(clause);
            }
        }
    }


    public int getMaxIndex(){
        return max_index;
    }

}

