
public class AgentA extends Agent{

    public AgentA(Game game, boolean verbose){
        super(game, verbose);
    }

    public int run(){
        return getGameState();
    }

}