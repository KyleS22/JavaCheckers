package javacheckers.model;

import javafx.util.Pair;

/**
 * Represents a move of one piece on the board to another space
 */
public class Move {

    private Coordinate from;
    private Coordinate to;

    /**
     * Create a new move
     * @param from The board position to move a piece from
     * @param to The board position to move a piece to
     */
    public Move(Coordinate from, Coordinate to){
        this.from = from;
        this.to = to;
    }

    /**
     * Get the board coordinates that the piece is moving from
     * @return A Coordinate (int, int) representing the board coordinates to get the piece from
     */
    public Coordinate getFrom() {
        return from;
    }

    /**
     * Get the board coordinates to move a piece to
     * @return A Coordinate (int, int) representing the board coordinates to move the piece to
     */
    public Coordinate getTo() {
        return to;
    }

    @Override
    public boolean equals(Object other){

        if(other == null){
            return false;
        }

        if(other == this){
            return true;
        }

        if (!(other instanceof Move)){
            return false;
        }

        Move otherMove = (Move) other;

        return (this.getFrom().getX() == otherMove.getFrom().getX()) && (this.getFrom().getY() == otherMove.getFrom().getY()) &&
                (this.getTo().getX() == otherMove.getTo().getX()) && (this.getTo().getY() == otherMove.getTo().getY());
    }
}
