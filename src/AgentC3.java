import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

public class AgentC3 extends AgentB{

    private double prior = 0.25;

    public AgentC3(Game game){
        super(game);
    }

    public int run(){
        if (!sps()){
            satProb();
        }
        return getGameState();
    }

    public void satProb(){

        boolean move_made = true;
        ArrayList<int[]> probe_candidates = getFrontierCandidates();

        while(move_made) {
            move_made = false;

            double highest_prob_paint = 0.0;
            int[] highest_cell_paint = null;
            int highest_index_paint = 0;

            double highest_prob_clear = 0.0;
            int[] highest_cell_clear = null;
            int highest_index_clear = 0;

            List<List<Boolean>> frontiers = getFrontiers(probe_candidates.size());
            ArrayList<List<Boolean>> legal_frontiers = pruneFrontiers(frontiers, probe_candidates);

            for (int i = 0; i < probe_candidates.size(); i++){
                int[] query_cell = probe_candidates.get(i);
                
                ArrayList<List<Boolean>> positives = new ArrayList<List<Boolean>>();
                ArrayList<List<Boolean>> negatives = new ArrayList<List<Boolean>>();
                
                for (List<Boolean> frontier : legal_frontiers){
                    if (frontier.get(i)){ positives.add(frontier);}
                    else{ negatives.add(frontier);}
                }

                double pos_prob = 0.0;
                double neg_prob = 0.0;

                for (List<Boolean> frontier : positives){
                    pos_prob += calcFrontierProbability(frontier);
                } 
                for (List<Boolean> frontier : negatives){
                    neg_prob += calcFrontierProbability(frontier);
                }

                pos_prob = pos_prob * prior;
                neg_prob = neg_prob * (1-prior);

                double norm_const = pos_prob + neg_prob;

                pos_prob = pos_prob/norm_const;
                neg_prob = neg_prob/norm_const;                

                if (pos_prob > highest_prob_paint){
                    highest_cell_paint = query_cell;
                    highest_prob_paint = pos_prob;
                    highest_index_paint = i;
                }

                if (neg_prob > highest_prob_clear){
                    highest_cell_clear = query_cell;
                    highest_prob_clear = neg_prob;
                    highest_index_clear = i;
                }
               
            }

            if (highest_cell_paint != null){
                game.state[highest_cell_paint[0]][highest_cell_paint[1]] = 1;   
                probe_candidates.remove(highest_cell_paint);
                move_made = true;
            } 
            if (highest_cell_clear != null){
                game.state[highest_cell_clear[0]][highest_cell_clear[1]] = 2;   
                probe_candidates.remove(highest_cell_clear);
                move_made = true;
            }         
            if (probe_candidates.size() == 0){
                break;
            }        
        }
    }

    public double calcFrontierProbability(List<Boolean> frontier){
        double prob = 1;
        for (boolean x : frontier){
            if (x){prob = prob * prior;}
            else{prob = prob * (1-prior);}
        }

        return prob;
    }


    public ArrayList<List<Boolean>> pruneFrontiers(List<List<Boolean>> frontiers, ArrayList<int[]> candidates){
        ArrayList<List<Boolean>> pruned_frontiers = new ArrayList<List<Boolean>>();

        for (List<Boolean> frontier : frontiers){
            int[][] state_copy = copy_board(game.state);
            for (int i = 0; i < frontier.size(); i++){
                int[] cell = candidates.get(i);
                if (frontier.get(i)){
                    state_copy[cell[0]][cell[1]] = 1;
                } else {
                    state_copy[cell[0]][cell[1]] = 2;
                }
            }

            if (checkValid(game.board, state_copy)){
                pruned_frontiers.add(frontier);
            }
        }

        return pruned_frontiers;
    }


    /**
     * Returns frontier cells (all cells neighboring a clue cell including clue cells themselves)
     * @return : array list of cells 
     */
    public ArrayList<int[]> getFrontierCandidates(){
        ArrayList<int[]> result = new ArrayList<int[]>();
        ArrayList<int[]> covered_cells = knowledgeBase.get_to_probe();

        for (int[] cell : covered_cells){
            ArrayList<int[]> neighbors = getNeighbors(cell[0], cell[1]);

            if (game.board[cell[0]][cell[1]] != -1){
                result.add(cell);
            } else {
                for (int[] neighbor : neighbors){
                    if (game.board[neighbor[0]][neighbor[1]] != -1){
                        result.add(cell);
                        break;
                    }
                }
            }
        }        
        return result;
    }


    public static List<List<Boolean>> getFrontiers(int k) {
        List<List<Boolean>> combinations = new ArrayList<>();
        generateCombinations(0, k, new Boolean[k], combinations);
        return combinations;
    }

    
    public static void generateCombinations(int current, int k, Boolean[] combination, List<List<Boolean>> combinations) {
        if (current == k) {
            combinations.add(new ArrayList<>(Arrays.asList(combination)));
            return;
        }

        // For the current position, try both true and false, then recurse
        combination[current] = false;
        generateCombinations(current + 1, k, combination, combinations);
        combination[current] = true;
        generateCombinations(current + 1, k, combination, combinations);
    }
    

    public int[][] copy_board(int[][] board){
        int[][] copy = new int[board.length][board[0].length];

        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                copy[i][j] = board[i][j];
            }
        }

        return copy;
    }


}