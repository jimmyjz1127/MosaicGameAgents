/**
 * Cell Object
 */

import java.util.ArrayList;
import java.util.LinkedList;


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


    public int[][] getNeighborsCoords(){
        int[][] neighbors = {{x-1, y-1}, {x-1, y}, {x-1, y+1},{x, y-1},{x, y+1},{x+1, y-1},{x+1, y},{x+1, y+1}};

        return neighbors;
    }

    /**
     * Setter for state 
     */
    public void setState(char state){
        this.state = state;
    }


    
}