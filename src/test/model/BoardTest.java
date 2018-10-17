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


        Piece[][] oldBoardState = board.getCurrentBoardState();

        Move move12 = new Move(sixthRowFirstSpace, fifthRowSecondSpace);
        assertTrue(board.movePiece(move12));
        Piece[][] newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][1], oldBoardState[5][0]);
        assertNull(newBoardState[5][0]);

        //-----------------------------------------------------------------

        // Test moving up and left for black
        Coordinate seventhRowSecondSpace = new Coordinate(1, 6);

        Move move13 = new Move(seventhRowSecondSpace, sixthRowFirstSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move13));
        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][0], oldBoardState[6][1]);
        assertNull(newBoardState[6][1]);

        // Test moving down and right for black (SHOULD NOT BE ABLE TO)
        Move move14 = new Move(sixthRowFirstSpace, seventhRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move14));
        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][0], oldBoardState[5][0]);
        assertNull(newBoardState[6][1]);

        // Test moving down and left for black (SHOULD NOT BE ABLE TO)

        // First have to move the piece up so we can move it back
        Coordinate fouthRowThirdSpace = new Coordinate(2, 3);

        Move move15 = new Move(fifthRowSecondSpace, fouthRowThirdSpace);
        assertTrue(board.movePiece(move15));

        // Now move down and left
        Move move16 = new Move(fouthRowThirdSpace, fifthRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move16));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[3][2], oldBoardState[3][2]);
        assertNull(newBoardState[4][1]);

        //----------------------------------------------

        // Reset the board
        board = new Board(new User("RedUser", 1), new User("BlackUser", 0));


        // Test moving down and right for red
        Coordinate thirdRowSecondSpace = new Coordinate(1, 2);
        Coordinate fourthRowThirdSpace = new Coordinate(2, 3);

        Move move17 = new Move(thirdRowSecondSpace, fourthRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move17));

        newBoardState = board.getCurrentBoardState();


        assertEquals(newBoardState[3][2], oldBoardState[2][1]);
        assertNull(newBoardState[2][1]);

        //-------------------------------------------------

        // Test moving down and left for red
        Coordinate secondRowThirdSpace = new Coordinate(2, 1);

        Move move18 = new Move(secondRowThirdSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move18));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[2][1], oldBoardState[1][2]);
        assertNull(newBoardState[1][2]);

        //-------------------------------------------------

        // Test moving up and right for red (SHOULD NOT BE ABLE TO)
        Move move19 = new Move(thirdRowSecondSpace, secondRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move19));

        assertEquals(newBoardState[2][1], oldBoardState[2][1]);
        assertNull(newBoardState[1][2]);

        //------------------------------------------------------

        // Test moving up and left for red (SHOULD NOT BE ABLE TO)

        // First move the previous piece out of the way
        Coordinate fourthRowFirstSpace = new Coordinate(0, 3);

        Move move20 = new Move(thirdRowSecondSpace, fourthRowFirstSpace);

        assertTrue(board.movePiece(move20));

        Move move21 = new Move(fourthRowThirdSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move21));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[3][2], oldBoardState[3][2]);
        assertNull(newBoardState[2][1]);

        //-------------------------------------------------------------------
        // Test moving more than one space (SHOULD NOT BE ABLE TO, unless jumping)

        Coordinate thirdRowFourthSpace = new Coordinate(3, 2);
        Coordinate fifthRowSixthSpace = new Coordinate(5, 4);

        Move move22 = new Move(thirdRowFourthSpace, fifthRowSixthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move22));

        newBoardState = board.getCurrentBoardState();



        assertEquals(newBoardState[2][3], oldBoardState[2][3]);
        assertNull(newBoardState[4][5]);


        // Test Jumping over a piece

        // Move a black piece into position to jump over a red piece
        Coordinate sixthRowThirdSpace = new Coordinate(2, 5);
        Coordinate fifthRowFourthSpace = new Coordinate(3, 4);

        Move move23 = new Move(sixthRowThirdSpace, fifthRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move23));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][3], oldBoardState[5][2]);
        assertNull(newBoardState[5][2]);

        // Now jump over a red piece

        Move move24 = new Move(fifthRowFourthSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move24));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[2][1], oldBoardState[4][3]);
        assertNull(newBoardState[4][3]);
        // Check that the red piece was removed
        assertNull(newBoardState[3][2]);

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
        Board board = new Board(new User("RedUser", 1), new User("BlackUser", 0));

        // Test selecting out of bounds
        Coordinate outOfBoundsTop = new Coordinate(0, -1);
        Coordinate outOfBoundsBottom = new Coordinate(0, 10);
        Coordinate outOfBoundsLeft = new Coordinate(-1, 0);
        Coordinate outOfBoundsRight = new Coordinate(10, 0);

        assertNull(board.selectPiece(outOfBoundsTop));
        assertNull(board.selectPiece(outOfBoundsBottom));
        assertNull(board.selectPiece(outOfBoundsLeft));
        assertNull(board.selectPiece(outOfBoundsRight));

        // Test selecting null space
        Coordinate nullSpace = new Coordinate(0, 0);

        assertNull(board.selectPiece(nullSpace));

        // Test selecting red when current user is red

        // If the current user is the black user, switch to the red user
        if(board.getCurrentUser().equals(board.getBlackUser())){
            board.switchCurrentUser();
        }

        Coordinate redPiece = new Coordinate(1, 0);

        Piece[][] boardState = board.getCurrentBoardState();

        assertEquals(board.selectPiece(redPiece), boardState[0][1]);

        // Test selecting red when current user is black (FAIL)
        if(board.getCurrentUser().equals(board.getRedUser())){
            board.switchCurrentUser();
        }

        assertNull(board.selectPiece(redPiece));

        // Test selecting black when current user is black
        if(board.getCurrentUser().equals(board.getRedUser())){
            board.switchCurrentUser();
        }

        for(int i = 0; i < 8; i++){
            System.out.print("\n");
            for(int j = 0; j < 8; j++){
                System.out.print(boardState[i][j] + " ");
            }
        };



        Coordinate blackPiece = new Coordinate(0, 7);

        assertEquals(board.selectPiece(blackPiece), boardState[7][0]);


        // Test selecting black when current user is red (FAIL)
        if(board.getCurrentUser().equals(board.getBlackUser())){
            board.switchCurrentUser();
        }

        assertNull(board.selectPiece(blackPiece));
    }

    @org.junit.jupiter.api.Test
    void checkMoves() {
        fail();
    }

}