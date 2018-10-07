package javacheckers.model;

import java.util.List;

public class Board {

    // The number of tiles on one side of the board
    private static final int BOARD_SIZE = 8;

    private static final int RED = 1;
    private static final int BLACK = 0;

    // 2D array representing board spaces
    private Piece[][] spaces;

    // The users in the game
    private User blackUser;
    private User redUser;
    private User currentUser;


    /**
     * Create the board and initialize the array
     */
    public Board(User redUser, User blackUser){

        this.redUser = redUser;
        this.blackUser = blackUser;
        this.currentUser = blackUser;

        this.spaces = new Piece[BOARD_SIZE][BOARD_SIZE];

        // Place Red Pieces
        // pieces go in first three rows
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < BOARD_SIZE; j++){
                // Even rows
                if(j % 2 == 0){
                    // Odd spaces
                    if(i % 2 == 1){
                        this.spaces[i][j] = new Piece(RED);
                    }
                // Odd rows
                }else{
                    // Even spaces
                    if( i % 2 == 0){
                        this.spaces[i][j] = new Piece(RED);
                    }
                }
            }
        }

        // Place Black Pieces
        // Place Red Pieces
        // pieces go in last three rows
        for(int i = BOARD_SIZE-1; i > BOARD_SIZE - 4; i--){
            for(int j = 0; j < BOARD_SIZE; j++){
                // Even rows
                if(j % 2 == 0){
                    // Odd spaces
                    if(i % 2 == 1){
                        this.spaces[i][j] = new Piece(BLACK);
                    }
                    // Odd rows
                }else{
                    // Even spaces
                    if( i % 2 == 0){
                        this.spaces[i][j] = new Piece(BLACK);
                    }
                }
            }
        }

    }

    /**
     * Move a piece according to the specified move
     * @param move The move containing the coordinates to move the piece
     */
    public boolean movePiece(Move move){

        // make sure this is a valid move
        if(this.isValidMove(move)){
            this.spaces[move.getTo().getX()][move.getTo().getY()] =
                    this.spaces[move.getFrom().getX()][move.getFrom().getY()];

            this.spaces[move.getFrom().getX()][move.getFrom().getY()] = null;

            return true;
        }else{
            return false;
        }


    }

    /**
     * Get the piece at the given board coordinate
     * @param coordinate The (x, y) coordinate of the board to get a piece from
     * @return The piece if there is one, null otherwise
     */
    public Piece selectPiece(Coordinate coordinate) {

        Piece piece;

        try {
            piece = this.spaces[coordinate.getY()][coordinate.getX()];
        }catch (IndexOutOfBoundsException e){
            return null;
        }

        if (piece == null) {
            return null;
        } else if (piece.getColour() == this.currentUser.getColour()) {
            return piece;
        } else {
            return null;
        }
    }

    public List<Move> checkMoves(Piece piece){
        // TODO: Check available moves for this piece
        return null;
    }


    public User getBlackUser(){
        return this.blackUser;
    }

    public User getRedUser(){
        return this.redUser;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public User switchCurrentUser(){
        if(this.currentUser.equals(this.blackUser)){
            this.currentUser = this.redUser;
        }else{
            this.currentUser = this.blackUser;
        }
        return this.currentUser;
    }

    public Piece[][] getCurrentBoardState(){
        return this.spaces;
    }

    /**
     * Check to see if a given move is valid
     * @param move The move to validate
     * @return True if the move is valid, false otherwise
     */
    private boolean isValidMove(Move move){

        try {
            // Cannot move a piece at a null location
            if (this.spaces[move.getFrom().getX()][move.getFrom().getY()] == null) {
                return false;
            }
        }catch (IndexOutOfBoundsException e){
            return false;
        }

        // Move up and right only if black or king

        // Move up and left only if black or king

        // Move down and right only if red or king

        // Move down and left only if red or king

        // Jump over pieces

        return false;

    }

}
