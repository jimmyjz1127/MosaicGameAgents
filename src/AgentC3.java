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

    public AgentC3(Game game, boolean verbose){
        super(game, verbose);
    }

    public int run(){
        if (!sps()){
			knowledgeBase.updateToProbe();
        	satProb();
	    }
		else if (verbose){System.out.println("Game complete with SPS! No need for formal logic strategy.");}
        return getGameState();
    }

    /**
     * Executes the probability sat strategy routine to play and solve game 
     */
    public void satProb(){
        boolean move_made = true;
        ArrayList<int[]> probe_candidates = knowledgeBase.get_to_probe();


        while(move_made) {
            move_made = false;

            double highest_prob_paint = 0.0;
            double highest_prob_clear = 0.0;
            int[] highest_cell_paint = null;
            int[] highest_cell_clear = null;           

            // For tracking distribution for verbosity
            HashMap<int[], Double[]> distribution = new HashMap<int[], Double[]>();

            for (int i = 0; i < probe_candidates.size(); i++){
                // cell to test 
                int[] query_cell = probe_candidates.get(i);

                
                ArrayList<int[]> frontier_candidates = getFrontierCandidates(query_cell[0], query_cell[1]); // All frontier candidate cells 
                List<List<Boolean>> frontiers = getFrontiers(frontier_candidates.size()); // All possible frontiers that can be formed with candidate cells 
                ArrayList<List<Boolean>> legal_frontiers = pruneFrontiers(frontiers, frontier_candidates); // get all legal frontiers 
                
                ArrayList<List<Boolean>> positives = new ArrayList<List<Boolean>>(); // list of configurations where query cell is paint 
                ArrayList<List<Boolean>> negatives = new ArrayList<List<Boolean>>(); // list of configurations where query cell is clear 

                for (List<Boolean> frontier : legal_frontiers){
                    if (frontier.get(0)){ positives.add(frontier);}
                    else{ negatives.add(frontier);}
                }

                double pos_prob = 0.0, neg_prob = 0.0;

                // sum probability of frontiers (for both cases where query cell is paint and clear)
                for (List<Boolean> frontier : positives){pos_prob += calcFrontierProbability(frontier);} 
                for (List<Boolean> frontier : negatives){neg_prob += calcFrontierProbability(frontier);}

                // Calculate our unnormalized probabilities for paint and clear 
                pos_prob = pos_prob * prior;
                neg_prob = neg_prob * (1.0 - prior);

                double norm_const = pos_prob + neg_prob; // calculate normalizing constant 

                // Normalize probabilities 
                pos_prob = pos_prob/norm_const;
                neg_prob = neg_prob/norm_const;     

                if (verbose){distribution.put(query_cell, new Double[]{pos_prob, neg_prob});}      

                // If current "query" cell has higher probability of being paint than previous highest probability cell 
                if (pos_prob > highest_prob_paint){
                    highest_cell_paint = query_cell;
                    highest_prob_paint = pos_prob;
                }
                // If current "query" cell has higher probability of being clear than previous highest probability cell 
                if (neg_prob > highest_prob_clear){
                    highest_cell_clear = query_cell;
                    highest_prob_clear = neg_prob;
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

            if (verbose){
                System.out.println("===================================");
                printDistribution(distribution);
                if (highest_cell_paint != null) System.out.println("Most probable cell to paint: [" + highest_cell_paint[0] + ", " + highest_cell_paint[1] + "]");
                if (highest_cell_clear != null) System.out.println("Most probable cell to clear: [" + highest_cell_clear[0] + ", " + highest_cell_clear[1] + "]");
                game.printBoard();
                System.out.println("===================================");
            }

            if (probe_candidates.size() == 0){break;}        
        }
    }

    /**
     * Prints probability distribution for a move 
     * @param distirbution : hashmap representing distribution where keys are cells and values are probabilities 
     */
    public void printDistribution(HashMap<int[], Double[]> distribution){
        System.out.println("Distribution:");
        distribution.forEach((key, value) -> {
            System.out.println("[" + key[0] + ", " + key[1] + "] : " + "<" + value[0] + ", " + value[1] + ">");
        });
    }

    /**
     * Returns frontier cells (all cells neighboring a clue cell including clue cells themselves)
     * @return : array list of cells 
     */
    public ArrayList<int[]> getFrontierCandidates(int x, int y){
        ArrayList<int[]> result = new ArrayList<int[]>();
        ArrayList<int[]> neighbors = getNeighbors(x,y);
        ArrayList<int[]> covered_neighbors = getCoveredNeighbors(x,y, neighbors);

        if (game.board[x][y] != -1){
            return covered_neighbors;
        }

        for (int[] cell : covered_neighbors){
            ArrayList<int[]> clue_neighbors = getNeighborsWithClues(cell[0], cell[1], getNeighbors(cell[0], cell[1]));

            if (clue_neighbors.size() != 0){
                result.add(cell);
            }
        }
        return result;
    }

    /**
     * Creates all combiantions of worlds for the frontier 
     * @param k : the number of cells in the frontier 
     * @return : List of boolean lists (each list of length of # of cells in frontier)
     */
    public static List<List<Boolean>> getFrontiers(int k) {
        List<List<Boolean>> combinations = new ArrayList<>();
        generateCombinations(0, k, new Boolean[k], combinations);
        return combinations;
    }

    /**
     * Helper function for getFrontiers (recursively generates combinations of possible frontier worlds)
     * @param current 
     * @param k
     * @param combination 
     * @param combinations 
     */
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

    /**
     * Takes a list of frontiers and returns subset of legal frontiers (consistent with clues)
     * @param frontiers : list of total possible frontiers (both legal and illegal ones)
     * @param candidates : list of cells in the frontiers
     * @return : an arraylist of legal frontiers 
     */
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
            if (checkValid(game.board, state_copy)){pruned_frontiers.add(frontier); } // check legality of frontier
        }
        return pruned_frontiers;
    }


    /**
     * Calculates the un-normalized marginal probability of a frontier 
     * @param frontier : the frontier to calculate probability of 
     * @return : double probability
     */
    public double calcFrontierProbability(List<Boolean> frontier){
        double prob = 1.0;
        for (boolean x : frontier){
            if (x){prob = prob * prior;}
            else{prob = prob * (1.0 -prior);}
        }
        return prob;
    }

    /**
     * Creates a deep copy of a game board 2D array
     * @param original : 2d array to copy
     * @return : a deep copy of given 2d array 
     */
    public int[][] copy_board(int[][] original) {
        if (original == null) {return null;}

        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            // Allocate space for the sub-array
            copy[i] = new int[original[i].length];
            // Copy the sub-array
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }

        return copy;
    }

}