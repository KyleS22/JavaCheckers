package javacheckers.model;

import java.util.List;

/**
 * Represents a checker piece on the board
 */
public class Piece{

    // Whether this piece is a king
    private boolean king;

    // Colour of the piece
    private int colour;

    public Piece(int colour){
        this.colour = colour;
    }

    /**
     * Determine if this piece is currently a king
     * @return True if this piece is a king, false otherwise
     */
    public boolean isKing(){
        return this.king;
    }

    /**
     * Upgrade this piece to a king
     */
    public void upgradeToKing(){
        this.king = true;
    }

    /**
     * Get the colour of this piece
     * @return 0 if black, 1 if red
     */
    public int getColour(){
        return this.colour;
    }

    public String toString(){
        if(this.colour == 0){
            return "BLAK";
        }else{
            return "RED ";
        }
    }
}
