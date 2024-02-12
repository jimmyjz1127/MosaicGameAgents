import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringJoiner;

public class AgentC4 extends AgentC3{

    private double prior = 0.25;

    public AgentC4(Game game, boolean verbose){
        super(game, verbose);
    }

    public int run(){
        if (!sps()){
            satProb();
        }
        return getGameState();
    }


    /**
     * Executes the probability sat strategy routine to play and solve game 
     */
    public void satProb(){
        boolean move_made = true;
        ArrayList<int[]> probe_candidates = getFrontierCandidates();

        while(move_made) {
            move_made = false;

            List<List<Boolean>> frontiers = getFrontiers(probe_candidates.size()); // get all possible frontiers 
            ArrayList<List<Boolean>> legal_frontiers = pruneFrontiers(frontiers, probe_candidates); // get all legal frontiers 

            HashMap<int[], Double> pos_distribution = new HashMap<>();
            HashMap<int[], Double> neg_distribution = new HashMap<>();

            for (int i = 0; i < probe_candidates.size(); i++){
                int[] query_cell = probe_candidates.get(i);
                
                ArrayList<List<Boolean>> positives = new ArrayList<List<Boolean>>(); // list of configurations where query cell is paint 
                ArrayList<List<Boolean>> negatives = new ArrayList<List<Boolean>>(); // list of configurations where query cell is clear 
                
                for (List<Boolean> frontier : legal_frontiers){
                    if (frontier.get(i)){ positives.add(frontier);}
                    else{ negatives.add(frontier);}
                }

                double pos_prob = 0.0, neg_prob = 0.0;

                // sum probability of frontiers (for both cases where query cell is paint and clear)
                for (List<Boolean> frontier : positives){pos_prob += calcFrontierProbability(frontier);} 
                for (List<Boolean> frontier : negatives){neg_prob += calcFrontierProbability(frontier);}

                // Calculate our unnormalized probabilities for paint and clear 
                pos_prob = pos_prob * prior;
                neg_prob = neg_prob * (1.0 -prior);
                
                double norm_const = pos_prob + neg_prob; // calculate normalizing constant 

                // Normalize probabilities 
                pos_prob = pos_prob/norm_const;
                neg_prob = neg_prob/norm_const;        
                
                pos_distribution.put(query_cell, pos_prob);
                neg_distribution.put(query_cell, neg_prob);
                
            }
            for (Map.Entry<int[], Double> entry : pos_distribution.entrySet()){
                int[] cell = entry.getKey();
                Double value = entry.getValue();
                if (value > 0){
                    game.state[cell[0]][cell[1]] = 1;
                    move_made = true;
                    removeFromArrayList(cell, probe_candidates);
                }
            }

            for (Map.Entry<int[], Double> entry : neg_distribution.entrySet()){
                int[] cell = entry.getKey();
                Double value = entry.getValue();
                if (value > 0){
                    game.state[cell[0]][cell[1]] = 2;
                    move_made = true;
                    removeFromArrayList(cell, probe_candidates);
                } 
            }
            if (probe_candidates.size() == 0){break;}        
        }
    }

    public boolean removeFromArrayList(int[] elem, ArrayList<int[]> list) {
        for (int i = 0; i < list.size(); i++ ){
            if (list.get(i)[0] == elem[0] && list.get(i)[1] == elem[1]){
                list.remove(i);
                return true;
            }
        }
        return false;
    }


}