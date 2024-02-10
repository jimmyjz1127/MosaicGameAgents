
public class AgentA extends Agent{

    public AgentA(Game game){
        super(game);
    }

    public int run(){
        return getGameState();
    }

}