package javacheckers.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    // The number of tiles on one side of the board
    public static final int BOARD_SIZE = 8;

    public static final int RED = 1;
    public static final int BLACK = 0;

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

            this.checkJump(move, true);

            this.spaces[move.getTo().getY()][move.getTo().getX()] =
                    this.spaces[move.getFrom().getY()][move.getFrom().getX()];

            this.spaces[move.getFrom().getY()][move.getFrom().getX()] = null;

            // Check to see if a piece was made a king
            if(spaces[move.getTo().getY()][move.getTo().getX()].getColour() == BLACK){
                if(move.getTo().getY() == 0){
                    spaces[move.getTo().getY()][move.getTo().getX()].upgradeToKing();
                }
            }else{
                if(move.getTo().getY() == BOARD_SIZE - 1){
                    spaces[move.getTo().getY()][move.getTo().getX()].upgradeToKing();
                }
            }
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

    /**
     * Get all valid moves for a position
     * @param coordinate The coordinate of the piece to check moves for
     * @return A list of Move objects representing all possible moves for the piece at the given coordinate
     */
    public List<Move> checkMoves(Coordinate coordinate){


        Piece piece = this.spaces[coordinate.getY()][coordinate.getX()];
        List<Move> moves = new ArrayList<>();


        if(piece == null){
            return moves;
        }

        // If it is a red piece
        if(piece.getColour() == this.getRedUser().getColour() || piece.isKing()){

            // Check down and right, down and left, and jumps
            Move downAndRight = new Move(coordinate, new Coordinate(coordinate.getX() + 1, coordinate.getY() + 1));
            Move downAndLeft = new Move(coordinate, new Coordinate(coordinate.getX() - 1, coordinate.getY() + 1));
            Move jumpDownAndRight = new Move(coordinate, new Coordinate(coordinate.getX() + 2, coordinate.getY() + 2));
            Move jumpDownAndLeft = new Move(coordinate, new Coordinate(coordinate.getX() - 2, coordinate.getY() + 2));

            if(isValidMove(downAndRight)){
                moves.add(downAndRight);
            }

            if(isValidMove(downAndLeft)){
                moves.add(downAndLeft);
            }

            if(isValidMove(jumpDownAndRight)){
                moves.add(jumpDownAndRight);
            }

            if(isValidMove(jumpDownAndLeft)){
                moves.add(jumpDownAndLeft);
            }



        }

        // If it is a black piece
        if(piece.getColour() == this.getBlackUser().getColour() || piece.isKing()){



            // Check up and right, up and left, and jumps
            Move upAndRight = new Move(coordinate, new Coordinate(coordinate.getX() + 1, coordinate.getY() - 1));
            Move upAndLeft = new Move(coordinate, new Coordinate(coordinate.getX() - 1, coordinate.getY() - 1));
            Move jumpUpAndRight = new Move(coordinate, new Coordinate(coordinate.getX() + 2, coordinate.getY() - 2));
            Move jumpUpAndLeft = new Move(coordinate, new Coordinate(coordinate.getX() - 2, coordinate.getY() - 2));

            if(isValidMove(upAndRight)){
                moves.add(upAndRight);
            }

            if(isValidMove(upAndLeft)){
                moves.add(upAndLeft);
            }

            if(isValidMove(jumpUpAndLeft)){
                moves.add(jumpUpAndLeft);
            }

            if(isValidMove(jumpUpAndRight)){
                moves.add(jumpUpAndRight);
            }

        }

        return moves;
    }


    /**
     * Get the black user
     * @return The black user
     */
    public User getBlackUser(){
        return this.blackUser;
    }

    /**
     * Get the red user
     * @return The red user
     */
    public User getRedUser(){
        return this.redUser;
    }

    /**
     * Get the user whose turn it is
     * @return The user whose turn it is
     */
    public User getCurrentUser(){
        return this.currentUser;
    }

    /**
     * Switch the current user to the opposing user
     * @return The user whose turn it is after the switch
     */
    public User switchCurrentUser(){
        if(this.currentUser.equals(this.blackUser)){
            this.currentUser = this.redUser;
        }else{
            this.currentUser = this.blackUser;
        }
        return this.currentUser;
    }

    /**
     * Get the current array representing the board state
     * @return A 2D arraie of Piece objects representing the current state of the game
     */
    public Piece[][] getCurrentBoardState(){
        final Piece[][] copy = new Piece[this.spaces.length][];

        for(int i = 0; i < this.spaces.length; i++){
            copy[i] = Arrays.copyOf(this.spaces[i], this.spaces[i].length);
        }

        return copy;
    }

    /**
     * Check to see if there is a winner
     * @return The user who won the game, Null if nobody has won yet
     */
    public User checkWinCon(){

        if(checkLossForPlayer(RED)){
            return this.getBlackUser();
        }

        if(checkLossForPlayer(BLACK)){
            return this.getRedUser();
        }

        return null;
    }

    /**
     * Check if the current game state is a loss for the given player colour
     * @param colour The colour (BLACK or RED) to check for loss
     * @return True if the given colour has lost the game
     */
    private boolean checkLossForPlayer(int colour){


        for(int i = 0; i < BOARD_SIZE; i++){
            for(int j = 0; j < BOARD_SIZE; j++){

                Piece piece = this.spaces[i][j];

                if(piece != null && piece.getColour() == colour) {
                    List<Move> moves = checkMoves(new Coordinate(j, i));

                    if(moves.size() != 0){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Check to see if a given move is valid
     * @param move The move to validate
     * @return True if the move is valid, false otherwise
     */
    private boolean isValidMove(Move move){

        try {
            // Cannot move a piece at a null location
            if (this.spaces[move.getFrom().getY()][move.getFrom().getX()] == null) {
                return false;
            }

            Piece piece = this.spaces[move.getFrom().getY()][move.getFrom().getX()];
            Piece landingSpace = this.spaces[move.getTo().getY()][move.getTo().getX()];

            // Can't move on top of another piece
            if(landingSpace != null){
                return false;
            }

            // If the piece is a king, it can move any direction
            if(piece.isKing()){
                if(!movingOneSpace(move)){
                    return checkJump(move, false);
                }else{
                    return true;
                }

                // Otherwise, if it is red it can only move down
            }else if(piece.getColour() == RED){

                // Piece must go down
                if(move.getTo().getY() <= move.getFrom().getY()){
                    return false;
                }

                if(!movingOneSpace(move)){
                    return checkJump(move, false);
                }else{
                    return true;
                }

                // Otherwise, if it is black it can only move up
            }else{

                // Piece must go up
                if(move.getTo().getY() >= move.getFrom().getY()){
                    return false;
                }

                if(!movingOneSpace(move)){
                    return checkJump(move, false);
                }else{
                    return true;
                }

            }
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }




    }

    /**
     * Check if a move is only moving a piece by one space
     * @param move The move to check
     * @return True if the move moves a piece one space only, false otherwise
     */
    private boolean movingOneSpace(Move move){

        Coordinate to = move.getTo();
        Coordinate from = move.getFrom();

        if(to.getY() > from.getY() + 1 || to.getY() < from.getY() - 1 || to.getX() > from.getX() + 1 || to.getX() < from.getX() - 1){
            return false;
        }else{
            return true;
        }


    }

    // NOTE CAN ONLY HANDLE ONE JUMP, MULTIPLE JUMPS WILL NEED TO BE SPECIFIED SEPERATELY AS A USER, BY ALLOWING THEM TO MOVE THE
    // SAME PIECE AGAIN AFTER THEY SUBMIT THEIR MOVE

    /**
     * Check of the current move is a valid jump and optionally perform the jump
     * @param move The move to check for jumps
     * @param performJump If true, the jumped piece will be removed and the jumping piece will be moved to the new space
     * @return True if the move is a jump
     */
    private boolean checkJump(Move move, boolean performJump){
        Coordinate to = move.getTo();
        Coordinate from = move.getFrom();

        Piece landingPosition = this.spaces[to.getY()][to.getX()];
        Piece startingPosition = this.spaces[from.getY()][from.getX()];

        if(startingPosition == null){
            return false;
        }

        // Can't land on an existing piece
        if(landingPosition != null){
            return false;
        }

        int jumpedY = -1;
        int jumpedX = -1;

        // if to is up and to the right
        if(to.getX() > from.getX() && to.getY() < from.getY()){
            jumpedY = from.getY() - 1;
            jumpedX = from.getX() + 1;

        // If up and to the left
        }else if(to.getX() < from.getX() && to.getY() < from.getY()){
           jumpedY = from.getY() - 1;
           jumpedX = from.getX() - 1;
        // down and right
        }else if(to.getX() > from.getX() && to.getY() > from .getY()){
            jumpedY = from.getY() + 1;
            jumpedX = from.getX() + 1;
        }else{
            jumpedY = from.getY() + 1;
            jumpedX = from.getX() - 1;
        }

        try {
            Piece jumpedPiece = this.spaces[jumpedY][jumpedX];

            if(jumpedPiece == null){
                return false;
            }

            if(jumpedPiece.getColour() == startingPosition.getColour()){
                return false;
            }else{
                if(performJump){
                    removePiece(new Coordinate(jumpedX, jumpedY));
                }
                return true;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }

    }

    /**
     * Remove the piece at the given coordinate from the board
     * @param coordinate The coordinate to remove the piece from
     */
    private void removePiece(Coordinate coordinate){
        this.spaces[coordinate.getY()][coordinate.getX()] = null;
    }

}
