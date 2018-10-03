package javacheckers.model;

public class Board {

    // The number of tiles on one side of the board
    private static final int BOARD_SIZE = 8;

    // 2D array representing board spaces
    private Piece[][] spaces;

    /**
     * Create the board and initialize the array
     */
    public Board(){
        this.spaces = new Piece[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Move a piece according to the specified move
     * @param move The move containing the coordinates to move the piece
     */
    public void movePiece(Move move){
        this.spaces[move.getTo().getX()][move.getTo().getY()] =
                this.spaces[move.getFrom().getX()][move.getFrom().getY()];

        this.spaces[move.getFrom().getX()][move.getFrom().getY()] = null;
    }

    /**
     * Get the piece at (x, y) board coordinates
     * @param x The x coordinate on the board to get the piece from
     * @param y The y coordinate on the board to get the piece from
     * @return A piece from the coordinate on the board, null if there is no piece there
     */
    public Piece selectPiece(int x, int y){
        return this.spaces[x][y];
    }


}
