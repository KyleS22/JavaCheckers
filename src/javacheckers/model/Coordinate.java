package javacheckers.model;

import java.io.Serializable;

/**
 * Represents x and y board coordinates
 */
public class Coordinate implements Serializable {

    // X and Y coords
    private int x;
    private int y;

    /**
     * Create a new coordinate
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate
     * @return The x coordinate
     */
    public int getX(){
        return this.x;
    }

    /**
     * Get the y coordinate
     * @return The y coordinate
     */
    public int getY(){
        return this.y;
    }

}
