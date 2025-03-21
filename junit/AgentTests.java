import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

import jdk.jfr.Timestamp;

public class AgentTests{
    private static ArrayList<String> small_inputs = new ArrayList<String>();
    private static ArrayList<String> medium_inputs = new ArrayList<String>();
    private static ArrayList<String> incorrect_inputs = new ArrayList<String>();

    @BeforeAll
    public static void setup(){
       
        
        small_inputs.add(".1*,.1_,.-_,.-_,.-*;.1_,.2_,.2_,.5*,.-*;.0_,.-_,.4*,.7_,.-*;.2_,.4_,.5*,.6*,.-*;.-*,.-*,.4_,.-*,.-_");
        small_inputs.add(".-_,.3*,.-*,.-*,.4*;.-_,.-*,.7_,.-*,.5*;.-_,.-_,.-*,.5*,.4_;.4*,.-*,.4_,.-_,.2*;.4*,.-*,.2_,.-_,.-_");
        small_inputs.add(".3*,.-_,.-_,.1_,.-*;.-*,.-*,.2_,.-_,.-_;.6*,.6*,.3_,.2_,.-*;.4*,.5*,.3_,.-_,.3*;.-_,.-_,.-*,.-_,.-*");
        small_inputs.add(".4*,.-*,.-_,.-_,.-_;.-*,.-*,.-_,.0_,.-_;.-_,.-_,.2_,.-_,.-_;.1_,.-*,.-_,.-_,.-_;.-_,.1_,.-_,.-_,.0_");
        small_inputs.add(".-_,.-*,.6*,.-*,.-*;.2_,.-*,.-*,.6*,.-_;.1_,.-_,.4*,.-_,.1_;.2_,.-_,.2_,.-_,.-_;.-*,.-*,.-_,.1_,.-*");
        small_inputs.add(".1_,.3_,.-*,.2_,.-_;.-*,.-_,.-*,.3_,.-_;.3*,.-_,.3_,.5_,.-*;.4*,.-_,.-*,.-*,.-*;.-*,.-*,.-_,.4_,.-*");
        small_inputs.add(".-_,.-_,.-*,.-*,.3*;.1_,.-_,.6*,.-*,.-_;.2*,.4_,.4*,.-*,.4*;.-*,.5_,.3_,.5_,.-*;.-*,.3*,.-_,.2_,.2*");
        small_inputs.add(".3*,.4*,.4_,.2_,.1_;.4_,.6*,.7*,.4*,.2_;.4_,.6*,.8*,.5*,.3_;.4*,.5*,.5_,.4*,.3_;.3*,.3_,.2_,.2_,.2*");
        small_inputs.add(".-*,.-*,.2*,.-_,.-*;.-*,.4_,.-_,.-_,.3*;.-_,.-_,.1_,.2_,.-*;.4*,.-*,.2_,.2_,.-_;.4*,.-*,.-_,.-_,.-*");

        medium_inputs.add(".3*,.-*,.-*,.4*,.-*,.-*,.-*;.-*,.-_,.-*,.4_,.-_,.-*,.6*;.-*,.6_,.3_,.2_,.2_,.-*,.-*;.-*,.-*,.-*,.-_,.2_,.-_,.-*;.3_,.-*,.4_,.-_,.-_,.6*,.5*;.2_,.-_,.3_,.3*,.-*,.-*,.-*;.1*,.-_,.-_,.-*,.4_,.3_,.2_");
        medium_inputs.add(".-_,.-_,.-_,.3*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_");
        medium_inputs.add(".3*,.-*,.-_,.-_,.-*,.-_,.-*;.-_,.-*,.-_,.3*,.4_,.5*,.-*;.1_,.-_,.3_,.2*,.3_,.4_,.4*;.-_,.1_,.1_,.-_,.1_,.-_,.-*;.-*,.-_,.2_,.1_,.-_,.-_,.-_;.-*,.7*,.-*,.2_,.-_,.-*,.2_;.-*,.6*,.-*,.-_,.2_,.-*,.-_");
        medium_inputs.add(".-*,.3*,.-_,.-_,.-_,.-_,.-*;.-*,.-_,.-_,.2*,.2_,.1_,.-_;.-*,.-_,.2_,.-*,.3_,.1_,.0_;.-*,.3_,.-_,.-_,.-*,.-_,.-_;.-*,.3_,.1_,.-*,.-*,.6*,.-*;.-*,.-_,.-_,.-_,.4_,.-*,.-*;.-*,.3*,.-_,.0_,.-_,.-_,.2_");
        medium_inputs.add(".3*,.-*,.-*,.4*,.-*,.-*,.-*;.-*,.-_,.-*,.4_,.-_,.-*,.6*;.-*,.6_,.3_,.2_,.2_,.-*,.-*;.-*,.-*,.-*,.-_,.2_,.-_,.-*;.3_,.-*,.4_,.-_,.-_,.6*,.5*;.-_,.2_,.-_,.3*,.-*,.-*,.-*;.1*,.-_,.-_,.-*,.4_,.3_,.2_");
        medium_inputs.add(".2_,.-_,.-_,.-*,.2_,.-*,.1_;.-*,.-*,.2_,.1_,.-_,.-_,.-_;.-*,.3_,.1_,.0_,.1_,.-_,.3*;.-_,.-_,.2_,.2_,.-_,.5*,.-*;.-_,.3_,.5*,.-*,.-_,.-*,.4*;.3_,.-*,.-*,.7*,.-*,.4_,.-_;.-*,.-*,.-_,.-*,.4*,.2_,.0_");
        medium_inputs.add(".-*,.2*,.-_,.2_,.-*,.-*,.-*;.-_,.-_,.-_,.-_,.-*,.9*,.-*;.2_,.-_,.-_,.3_,.-*,.-*,.-*;.-*,.-*,.-_,.-*,.3_,.-_,.-_;.2_,.-_,.2_,.-_,.-_,.-_,.0_;.-_,.0_,.-_,.-_,.-*,.-_,.-_;.-_,.-_,.-_,.2_,.-*,.4*,.-*");
        medium_inputs.add(".-*,.4*,.-*,.2_,.-_,.-*,.3*;.-_,.-_,.-*,.-_,.-_,.-_,.-*;.1_,.-_,.-*,.-*,.1_,.-_,.-_;.-_,.-*,.-_,.2_,.1_,.1_,.-_;.5*,.-*,.4_,.1_,.1_,.-_,.-*;.-*,.-*,.-*,.-_,.3_,.6*,.-*;.-_,.5*,.4*,.-_,.-*,.-*,.4*");
        medium_inputs.add(".-_,.0_,.1_,.-_,.-_,.0_,.-_;.-_,.-_,.3_,.-*,.-_,.-_,.-_;.0_,.-_,.-*,.-*,.4_,.-*,.-*;.-_,.5_,.-*,.4_,.4_,.-*,.6*;.-*,.-*,.-*,.-_,.2_,.-*,.-*;.-*,.7*,.-_,.-_,.-_,.-_,.2_;.-*,.-*,.-_,.1_,.-*,.1_,.-_");

        incorrect_inputs.add("*3*,*0*,.-*,.4*,.-*,.-*,.-*;.-*,.-_,.-*,.4_,.-_,.-*,.6*;.-*,.6_,.3_,.2_,.2_,.-*,.-*;.-*,.-*,.-*,.-_,.2_,.-_,.-*;.3_,.-*,.4_,.-_,.-_,.6*,.5*;.2_,.-_,.3_,.3*,.-*,.-*,.-*;.1*,.-_,.-_,.-*,.4_,.3_,.2_");
        incorrect_inputs.add(".3_,.-_,.-_,*0*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_");
        incorrect_inputs.add(".3*,.-*,.-_,.-_,*0*,.-_,.-*;.-_,.-*,.-_,.3*,.4_,.5*,.-*;.1_,.-_,.3_,.2*,.3_,.4_,.4*;.-_,.1_,.1_,.-_,.1_,.-_,.-*;.-*,.-_,.2_,.1_,.-_,.-_,.-_;.-*,.7*,.-*,.2_,.-_,.-*,.2_;.-*,.6*,.-*,.-_,.2_,.-*,.-_");
        incorrect_inputs.add(".-*,.3*,.-_,.-_,.-_,.-_,.-*;*0*,.-_,.-_,.2*,.2_,.1_,.-_;.-*,.-_,.2_,.-*,.3_,.1_,.0_;.-*,.3_,.-_,.-_,.-*,.-_,.-_;.-*,.3_,.1_,.-*,.-*,.6*,.-*;.-*,.-_,.-_,.-_,.4_,.-*,.-*;.-*,.3*,.-_,.0_,.-_,.-_,.2_");
        incorrect_inputs.add(".3*,.-*,.-*,_4*,.-*,.-*,*0*;.-*,.-_,.-*,.4_,.-_,.-*,.6*;.-*,.6_,.3_,.2_,.2_,.-*,.-*;.-*,.-*,.-*,.-_,.2_,.-_,.-*;.3_,.-*,.4_,.-_,.-_,.6*,.5*;.-_,.2_,.-_,.3*,.-*,.-*,.-*;.1*,.-_,.-_,.-*,.4_,.3_,.2_");
        incorrect_inputs.add(".2_,.-_,.-_,.-*,.2_,.-*,.1_;*0*,.-*,.2_,.1_,.-_,.-_,.-_;.-*,.3_,.1_,.0_,.1_,.-_,.3*;.-_,.-_,.2_,.2_,.-_,.5*,.-*;.-_,.3_,.5*,.-*,.-_,.-*,.4*;.3_,.-*,.-*,.7*,.-*,.4_,.-_;.-*,.-*,.-_,.-*,.4*,.2_,.0_");
        incorrect_inputs.add("*-*,.2*,.-_,.2_,*0*,.-*,.-*;.-_,.-_,.-_,.-_,.-*,*9*,.-*;.2_,.-_,.-_,.3_,.-*,.-*,.-*;.-*,.-*,.-_,.-*,.3_,.-_,.-_;.2_,.-_,.2_,.-_,.-_,.-_,.0_;.-_,.0_,.-_,.-_,.-*,.-_,.-_;.-_,.-_,.-_,.2_,.-*,.4*,.-*");
        incorrect_inputs.add(".-*,.4*,*0*,.2_,.-_,.-*,.3*;.-_,.-_,.-*,.-_,.-_,.-_,.-*;.1_,.-_,.-*,.-*,.1_,.-_,.-_;.-_,.-*,.-_,.2_,.1_,.1_,.-_;.5*,.-*,.4_,.1_,.1_,.-_,.-*;.-*,.-*,.-*,.-_,.3_,.6*,.-*;.-_,.5*,.4*,.-_,.-*,.-*,.4*");
        incorrect_inputs.add(".-_,.0_,.1_,.-_,.-_,*0_,.-_;.-_,.-_,.3_,.0*,.-_,.-_,.-_;.0_,.-_,.-*,.-*,.4_,.-*,.-*;.-_,.5_,.-*,.4_,.4_,.-*,.6*;.-*,.-*,.-*,.-_,.2_,.-*,.-*;.-*,.7*,.-_,.-_,.-_,.-_,.2_;.-*,.-*,.-_,.1_,.-*,.1_,.-_");   
    }

    public boolean containsCovered(int[][] board){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[0].length; j++){
                if (board[i][j] == 0) return true;
            }
        }
        return false;
    }

    @Test 
    public void testAgentNotNull(){
        Game game = new Game();
        Agent agent = new Agent(game, true);
        assertNotNull(agent);
    }

    @Test
    public void testAgentCheckGameStateSmall(){
        for (String input : small_inputs){
            Game game = new Game();
            game.setGame(input);

            Agent agent = new Agent(game, true);
            int int_result = agent.getGameState();
            boolean result = (int_result == 3) | (int_result == 1);
            assertEquals(containsCovered(game.state),  !result );
        }
    }

    @Test
    public void testAgentCheckGameStateMedium(){
        for (String input : medium_inputs){
            Game game = new Game();
            game.setGame(input);

            Agent agent = new Agent(game, false);
            int int_result = agent.getGameState();
            boolean result = (int_result == 3) | (int_result == 1);
            assertEquals(containsCovered(game.state),  !result );
        }
    }

    @Test 
    public void testInconsistentInputs(){
        for (String input : incorrect_inputs){
            Game game = new Game();
            game.setGame(input);

            Agent agent = new Agent(game, false);
            int int_result = agent.getGameState();
            boolean result = (int_result == 1) | (int_result == 0);
            assertEquals(true, result);
        }
    }

    @Test
    public void testAgentCheckValid(){
        for (String input : incorrect_inputs){
            Game game = new Game();
            game.setGame(input);

            Agent agent = new Agent(game, false);
            assertEquals(false, agent.checkValid(game.board, game.state));
        }
    }

    @Test
    public void testGetCoveredNeighbors(){
        for (String input : medium_inputs){
            Game game = new Game();
            boolean parse = game.setGame(input);

            Agent agent = new Agent(game, false);
            int result = agent.getCoveredNeighbors(1,1, agent.getNeighbors(1,1)).size();
            assertEquals(9, result);
        }
    }


    
}
