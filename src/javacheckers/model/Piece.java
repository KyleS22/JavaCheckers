package javacheckers.model;

import java.util.List;

/**
 * Represents a checker piece on the board
 */
public class Piece {

    // Whether this piece is a king
    private boolean king;

    public Piece(){}

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
     * Check the possible moves this piece can make
     * @return A list of Move objects representing the possible moves this piece can make
     */
    public List<Move> checkMoves(){
        // TODO: Check available moves for this piece
        return null;
    }

}
