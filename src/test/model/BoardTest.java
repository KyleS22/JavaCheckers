package test.model;

import javacheckers.model.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @org.junit.jupiter.api.Test
    void movePiece() {
        Board board = new Board(new User("RedUser", 1), new User("BlackUser", 0));

        // The empty top left corner
        Coordinate firstRowFirstSpace = new Coordinate(0, 0);

        // Another empty space, down and left
        Coordinate secondRowSecondSpace = new Coordinate(1, 1);

        // Create a move from the coords
        Move move1 = new Move(firstRowFirstSpace, secondRowSecondSpace);

        // Test moving a null piece
        assertFalse(board.movePiece(move1));

        //-------------------------------------------
        // Test moving a piece on top of another piece
        // Top left piece
        Coordinate firstRowSecondSpace = new Coordinate(1, 0);
        // The piece down and right of it
        Coordinate secondRowSecondPiece = new Coordinate(2, 1);

        Move move2 = new Move(firstRowSecondSpace, secondRowSecondPiece);

        assertFalse(board.movePiece(move2));

        //----------------------------------------------

        // Test moving to the same position (SHOULD NOT BE ABLE TO)
        Move move3 = new Move(firstRowSecondSpace, firstRowSecondSpace);
        assertFalse(board.movePiece(move3));

        //------------------------------------------
        // Test moving out of bounds top
        Coordinate outOfBoundsTop = new Coordinate(0, -1);
        Move move4 = new Move(firstRowSecondSpace, outOfBoundsTop);

        assertFalse(board.movePiece(move4));
        //----------------------------------------------------

        // Test moving out of bounds bottom
        Coordinate eighthRowFirstSpace = new Coordinate(0, 7);
        Coordinate outOfBoundsBottom = new Coordinate(1, 8);

        Move move5 = new Move(eighthRowFirstSpace, outOfBoundsBottom);
        assertFalse(board.movePiece(move5));

        //-----------------------------------------------------

        // Test moving out of bounds left
        Coordinate secondRowFirstSpace = new Coordinate(0, 1);
        Coordinate outOfBoundsLeft = new Coordinate(-1, 2);

        Move move6 = new Move(secondRowFirstSpace, outOfBoundsLeft);
        assertFalse(board.movePiece(move6));

        //----------------------------------------------------------
        // Test moving out of bounds right
        Coordinate thirdRowEighthSpace = new Coordinate(7, 3);
        Coordinate outOfBoundsRight = new Coordinate(8, 4);

        Move move7 = new Move(thirdRowEighthSpace, outOfBoundsRight);
        assertFalse(board.movePiece(move7));

        //------------------------------------------------------------

        // Test moving from out of bounds top
        Move move8 = new Move(outOfBoundsTop, firstRowSecondSpace);
        assertFalse(board.movePiece(move8));

        //------------------------------------------------------------
        // Test moving from out of bounds bottom
        Coordinate eighthRowSecondSpace = new Coordinate(1, 7);
        Move move9 = new Move(outOfBoundsBottom, eighthRowSecondSpace);

        assertFalse(board.movePiece(move9));

        // Test moving from out of bounds left
        Move move10 = new Move(outOfBoundsLeft, secondRowFirstSpace);
        assertFalse(board.movePiece(move10));

        //-------------------------------------------------------------

        // Test moving from out of bounds right
        Move move11 = new Move(outOfBoundsRight, thirdRowEighthSpace);
        assertFalse(board.movePiece(move11));

        //--------------------------------------------------------------
        // Test moving up and right for black
        Coordinate sixthRowFirstSpace = new Coordinate(0, 5);
        Coordinate fifthRowSecondSpace = new Coordinate(1, 4);

        Piece p = board.selectPiece(sixthRowFirstSpace);

        Move move12 = new Move(sixthRowFirstSpace, fifthRowSecondSpace);
        assertTrue(board.movePiece(move12));
        Piece[][] boardState = board.getCurrentBoardState();

        assertEquals(boardState[1][4], p);
        assertNull(boardState[0][5]);

        //-----------------------------------------------------------------

        // Test moving up and left for black
        Coordinate seventhRowSecondSpace = new Coordinate(1, 6);

        Move move13 = new Move(seventhRowSecondSpace, sixthRowFirstSpace);

        p = board.selectPiece(seventhRowSecondSpace);

        assertTrue(board.movePiece(move13));
        boardState = board.getCurrentBoardState();

        assertEquals(boardState[0][5], p);
        assertNull(boardState[1][6]);

        // Test moving down and right for black (SHOULD NOT BE ABLE TO)
        Move move14 = new Move(sixthRowFirstSpace, seventhRowSecondSpace);

        p = board.selectPiece(sixthRowFirstSpace);

        assertFalse(board.movePiece(move14));
        boardState = board.getCurrentBoardState();

        assertEquals(boardState[0][5], p);
        assertNull(boardState[1][6]);

        // Test moving down and left for black (SHOULD NOT BE ABLE TO)

        // First have to move the piece up so we can move it back
        Coordinate fouthRowThirdSpace = new Coordinate(2, 3);

        Move move15 = new Move(fifthRowSecondSpace, fouthRowThirdSpace);
        assertTrue(board.movePiece(move15));

        // Now move down and left
        Move move16 = new Move(fouthRowThirdSpace, fifthRowSecondSpace);

        p = board.selectPiece(fouthRowThirdSpace);

        assertFalse(board.movePiece(move16));

        boardState = board.getCurrentBoardState();

        assertEquals(boardState[2][3], p);
        assertNull(boardState[1][4]);

        // TODO: Test moving down and right for red

        // TODO: Test moving down and left for red

        // TODO: Test moving up and right for red (SHOULD NOT BE ABLE TO)

        // TODO: Test moving up and left for red (SHOULD NOT BE ABLE TO)

        // TODO: Test moving more than one space (SHOULD NOT BE ABLE TO, unless jumping)

        // TODO: Test Jumping over a piece

        // TODO: Test jumping over two pieces

        // TODO: Test jumping over three pieces

        // TODO: Test becoming a king

        // TODO: Test moving up and right for red king

        // TODO: Test moving up and left for red king

        // TODO: Test moving down and right for red king

        // TODO: Test moving down and left for red king

        // TODO: Test moving down and right for black king

        // TODO: Test moving down and left for black king

        // TODO: Test moving up and right for black king

        // TODO: Test moving up and left for black king

    }

    @org.junit.jupiter.api.Test
    void selectPiece() {
        // TODO: Test selecting out of bounds

        // TODO: Test selecting null space

        // TODO: Test selecting red when current user is red

        // TODO: Test selecting red when current user is black (FAIL)

        // TODO: Test selecting black when current user is black

        // TODO: Test selecting black when current user is red (FAIL)
        fail();
    }

    @org.junit.jupiter.api.Test
    void checkMoves() {
        fail();
    }

}