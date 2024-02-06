import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.StringJoiner;


interface ClauseFunction{
    String apply(int x, int y);
}

public class KnowledgeBase {

    // setup bijection between cnf literal and dimacs integer 
    private HashMap<String, Integer> str_int = new HashMap<String, Integer>();
    private HashMap<Integer, String> int_str = new HashMap<Integer, String>();

    // private int[][] state;
    // private int[][] board;
    Game game;

    private ArrayList<int[]> to_probe = new ArrayList<int[]>();   //cells to probe 
    private ArrayList<int[]> clue_cells = new ArrayList<int[]>(); //cells with clues 

    private ArrayList<String> kb = new ArrayList<String>(); // clauses  

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
        kb = new ArrayList<String>();
        for (int[] cell : clue_cells){
            String clauses = func.apply(cell[0], cell[1]);
            if (clauses.length() != 0){
                kb.add(clauses);
            }
        }
    }

    public ArrayList<String> getKB(){
        return kb; 
    }

    public String getKBString(String symbol, String prefix, String suffix){
        StringJoiner joiner = new StringJoiner(symbol, prefix, suffix);
        for (String clause : kb){
            joiner.add(clause);
        }
        String result = joiner.toString();
        return result;
    }

    // public ArrayList<int[]> getDimacs(){
        
    // }

    public void printClauses(){
        for (String clause : kb) {
            System.out.println(clause);
        }
    }

}

