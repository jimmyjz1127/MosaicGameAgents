import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.*;

import jdk.jfr.Timestamp;

public class KnowledgeBaseTests{

    @Test
    public void testKnowledgeBaseNotNull(){
        Game game = new Game();
        KnowledgeBase kb = new KnowledgeBase(game);
        assertNotNull(kb);
    }

    @Test 
    public void testKnowledgeBaseNotNullWithInput(){
        String game_input = ".-_,.-_,.-_,.3*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_";
        Game game = new Game();
        game.setGame(game_input);
        KnowledgeBase kb = new KnowledgeBase(game);
        assertNotNull(kb);
    }

    @Test 
    public void testKBGetClues(){
        String game_input = ".-_,.-_,.-_,.3*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_";
        Game game = new Game();
        game.setGame(game_input);
        KnowledgeBase kb = new KnowledgeBase(game);
        assertEquals(21, kb.get_clue_cells().size());
    }

    @Test 
    public void testKBGetProbe(){
        String game_input = ".-_,.-_,.-_,.3*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_";
        Game game = new Game();
        game.setGame(game_input);
        KnowledgeBase kb = new KnowledgeBase(game);
        assertEquals(49, kb.get_to_probe().size());
    }

    @Test 
    public void testCellStringEncoding(){
        Game game = new Game();
        KnowledgeBase kb = new KnowledgeBase(game);
        int[] cell = new int[]{1,2};

        assertEquals("1#2", kb.encodeCellString(cell));
    }

    @Test
    public void testDimacsVocab(){
        String game_input = ".-_,.-_,.-_,.3*,.4_,.2_,.-_;.1_,.-*,.-_,.6*,.-*,.-*,.-_;.2_,.-_,.7*,.-*,.9*,.-*,.3_;.-_,.-*,.7*,.-*,.-*,.-*,.-_;.4*,.-*,.6*,.6_,.6_,.-*,.5*;.-*,.6_,.-*,.5_,.-*,.-*,.-*;.-_,.-_,.2*,.-_,.3*,.4_,.2_";
        Game game = new Game();
        game.setGame(game_input);
        KnowledgeBase kb = new KnowledgeBase(game);

        assertEquals(49, kb.getMaxIndex());
    }
}