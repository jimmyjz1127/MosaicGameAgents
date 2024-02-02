/**
 * Cell Object
 */


public class Cell {
    private int x;
    private int y;
    private char state;
    private char clue;

    public Cell(int x, int y, char state, char clue){
        this.x = x;
        this.y = y;
        this.state = state;
        this.clue = clue;
    }

    /**
     * Getter function of x-coordinate 
     */
    public int getX(){
        return x;
    }

    /**
     * Getter function for y-coordinate 
     */
    public int getY(){
        return y;
    }

    /**
     * Getter function for state of cell 
     */
    public char getState(){
        return state;
    }

    /**
     * Getter function for cell clue 
     */
    public char getClue(){
        return clue;
    }

    /**
     * Setter for state 
     */
    public void setState(char state){
        this.state = state;
    }
}