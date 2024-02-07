import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;


interface ClauseFunction{
    ArrayList<List<String>> apply(int x, int y);
}

public class KnowledgeBase {

    // setup bijection between cnf literal and dimacs integer 
    private Map<String, Integer> str_int = new HashMap<String, Integer>();
    private Map<Integer, String> int_str = new HashMap<Integer, String>();

    Game game;

    private ArrayList<int[]> to_probe = new ArrayList<int[]>();   //cells to probe 
    private ArrayList<int[]> clue_cells = new ArrayList<int[]>(); //cells with clues 

    private ArrayList<ArrayList<List<String>>> kb = new ArrayList<ArrayList<List<String>>>(); // clauses  

    private int max_index = 0;

    public KnowledgeBase(Game game){
        this.game = game;

        setupKB();
    } 

    public void setupKB(){
        for (int i = 0; i < game.board.length; i++){
            for (int j = 0; j < game.board[0].length; j++){
                if (game.state[i][j] == 0) to_probe.add(new int[]{i,j});
                if (game.board[i][j] != -1) clue_cells.add(new int[]{i,j});

                String cell_string = encodeCellString(new int[]{i,j});

                if (!str_int.containsKey(cell_string)){
                    max_index += 1;

                    str_int.put(cell_string, max_index);
                    int_str.put(max_index, cell_string);
                }
            }
        }
    }

    public String encodeCellString(int[] cell){
		return Integer.toString(cell[0]) + '#' + Integer.toString(cell[1]);
	}


    public ArrayList<int[]> get_to_probe(){
        return to_probe;
    }

    public ArrayList<int[]> get_clue_cells(){
        return clue_cells;
    }

    public String convert(int index){
        return int_str.get(index);
    }

    public int convert(String literal){
        return str_int.get(literal);
    }


    public void generateKB(ClauseFunction func){
        kb =new ArrayList<ArrayList<List<String>>>();
        for (int[] cell : clue_cells){
            ArrayList<List<String>> clauses = func.apply(cell[0], cell[1]);

            if (clauses.size() != 0){
                kb.add(clauses);
            }
        }
    }

    public ArrayList<ArrayList<List<String>>> getKB(){
        return kb; 
    }

    public String getKBString(String symbol1, String symbol2, String symbol3, String prefix, String suffix){

        StringJoiner joiner = new StringJoiner(symbol1, prefix, suffix);
        for (ArrayList<List<String>> exp : kb){
            ArrayList<String> clauses = new ArrayList<String>();
            for (List<String> clause : exp) {
                clauses.add("(" + String.join(symbol3, clause) + ")");
            }
            joiner.add(String.join(symbol2, clauses));
        }
        String result = joiner.toString();
        return result;
    }

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

    public void printClauses(){
        for (ArrayList<List<String>> clauses : kb) {
            for (List<String> clause : clauses){
                System.out.println(clause);
            }
        }
    }


    public void printdicts(){
        int_str.forEach((key,value) -> {
            System.out.println(key + " : " + value);
        });
    }

    public int getMaxIndex(){
        return max_index;
    }

}

