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
        Move move = new Move(firstRowFirstSpace, secondRowSecondSpace);

        // Test moving a null piece
        assertFalse(board.movePiece(move));

        //-------------------------------------------
        // Test moving a piece on top of another piece
        // Top left piece
        Coordinate firstRowSecondSpace = new Coordinate(1, 0);
        // The piece down and right of it
        Coordinate secondRowSecondPiece = new Coordinate(2, 1);

        move = new Move(firstRowSecondSpace, secondRowSecondPiece);

        assertFalse(board.movePiece(move));

        //----------------------------------------------

        // Test moving to the same position (SHOULD NOT BE ABLE TO)
        move = new Move(firstRowSecondSpace, firstRowSecondSpace);
        assertFalse(board.movePiece(move));

        //------------------------------------------
        // Test moving out of bounds top
        Coordinate outOfBoundsTop = new Coordinate(0, -1);
        move = new Move(firstRowSecondSpace, outOfBoundsTop);

        assertFalse(board.movePiece(move));
        //----------------------------------------------------

        // Test moving out of bounds bottom
        Coordinate eighthRowFirstSpace = new Coordinate(0, 7);
        Coordinate outOfBoundsBottom = new Coordinate(1, 8);

        move = new Move(eighthRowFirstSpace, outOfBoundsBottom);
        assertFalse(board.movePiece(move));

        //-----------------------------------------------------

        // Test moving out of bounds left
        Coordinate secondRowFirstSpace = new Coordinate(0, 1);
        Coordinate outOfBoundsLeft = new Coordinate(-1, 2);

        move = new Move(secondRowFirstSpace, outOfBoundsLeft);
        assertFalse(board.movePiece(move));

        //----------------------------------------------------------
        // Test moving out of bounds right
        Coordinate thirdRowEighthSpace = new Coordinate(7, 3);
        Coordinate outOfBoundsRight = new Coordinate(8, 4);

        move = new Move(thirdRowEighthSpace, outOfBoundsRight);
        assertFalse(board.movePiece(move));

        //------------------------------------------------------------

        // Test moving from out of bounds top
        move = new Move(outOfBoundsTop, firstRowSecondSpace);
        assertFalse(board.movePiece(move));

        //------------------------------------------------------------
        // Test moving from out of bounds bottom
        Coordinate eighthRowSecondSpace = new Coordinate(1, 7);
        move = new Move(outOfBoundsBottom, eighthRowSecondSpace);

        assertFalse(board.movePiece(move));

        // Test moving from out of bounds left
        move = new Move(outOfBoundsLeft, secondRowFirstSpace);
        assertFalse(board.movePiece(move));

        //-------------------------------------------------------------

        // Test moving from out of bounds right
        move = new Move(outOfBoundsRight, thirdRowEighthSpace);
        assertFalse(board.movePiece(move));

        //--------------------------------------------------------------
        // Test moving up and right for black
        Coordinate sixthRowFirstSpace = new Coordinate(0, 5);
        Coordinate fifthRowSecondSpace = new Coordinate(1, 4);


        Piece[][] oldBoardState = board.getCurrentBoardState();

        move = new Move(sixthRowFirstSpace, fifthRowSecondSpace);
        assertTrue(board.movePiece(move));
        Piece[][] newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][1], oldBoardState[5][0]);
        assertNull(newBoardState[5][0]);

        //-----------------------------------------------------------------

        // Test moving up and left for black
        Coordinate seventhRowSecondSpace = new Coordinate(1, 6);

        move = new Move(seventhRowSecondSpace, sixthRowFirstSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));
        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][0], oldBoardState[6][1]);
        assertNull(newBoardState[6][1]);

        // Test moving down and right for black (SHOULD NOT BE ABLE TO)
        move = new Move(sixthRowFirstSpace, seventhRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move));
        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][0], oldBoardState[5][0]);
        assertNull(newBoardState[6][1]);

        // Test moving down and left for black (SHOULD NOT BE ABLE TO)

        // First have to move the piece up so we can move it back
        Coordinate fouthRowThirdSpace = new Coordinate(2, 3);

        move = new Move(fifthRowSecondSpace, fouthRowThirdSpace);
        assertTrue(board.movePiece(move));

        // Now move down and left
        move = new Move(fouthRowThirdSpace, fifthRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[3][2], oldBoardState[3][2]);
        assertNull(newBoardState[4][1]);

        //----------------------------------------------

        // Reset the board
        board = new Board(new User("RedUser", 1), new User("BlackUser", 0));


        // Test moving down and right for red
        Coordinate thirdRowSecondSpace = new Coordinate(1, 2);
        Coordinate fourthRowThirdSpace = new Coordinate(2, 3);

        move = new Move(thirdRowSecondSpace, fourthRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();


        assertEquals(newBoardState[3][2], oldBoardState[2][1]);
        assertNull(newBoardState[2][1]);

        //-------------------------------------------------

        // Test moving down and left for red
        Coordinate secondRowThirdSpace = new Coordinate(2, 1);

        move = new Move(secondRowThirdSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[2][1], oldBoardState[1][2]);
        assertNull(newBoardState[1][2]);

        //-------------------------------------------------

        // Test moving up and right for red (SHOULD NOT BE ABLE TO)
        move = new Move(thirdRowSecondSpace, secondRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move));

        assertEquals(newBoardState[2][1], oldBoardState[2][1]);
        assertNull(newBoardState[1][2]);

        //------------------------------------------------------

        // Test moving up and left for red (SHOULD NOT BE ABLE TO)

        // First move the previous piece out of the way
        Coordinate fourthRowFirstSpace = new Coordinate(0, 3);

        move = new Move(thirdRowSecondSpace, fourthRowFirstSpace);

        assertTrue(board.movePiece(move));

        move = new Move(fourthRowThirdSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[3][2], oldBoardState[3][2]);
        assertNull(newBoardState[2][1]);

        //-------------------------------------------------------------------
        // Test moving more than one space (SHOULD NOT BE ABLE TO, unless jumping)

        Coordinate thirdRowFourthSpace = new Coordinate(3, 2);
        Coordinate fifthRowSixthSpace = new Coordinate(5, 4);

        move = new Move(thirdRowFourthSpace, fifthRowSixthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertFalse(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();



        assertEquals(newBoardState[2][3], oldBoardState[2][3]);
        assertNull(newBoardState[4][5]);


        // Test Jumping over a piece

        // Move a black piece into position to jump over a red piece
        Coordinate sixthRowThirdSpace = new Coordinate(2, 5);
        Coordinate fifthRowFourthSpace = new Coordinate(3, 4);

        move = new Move(sixthRowThirdSpace, fifthRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][3], oldBoardState[5][2]);
        assertNull(newBoardState[5][2]);

        // Now jump over a red piece

        move = new Move(fifthRowFourthSpace, thirdRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[2][1], oldBoardState[4][3]);
        assertNull(newBoardState[4][3]);
        // Check that the red piece was removed
        assertNull(newBoardState[3][2]);

        // Test becoming a king

        // Move the red piece so we can jump over it with the black piece
        Coordinate firstRowFourthSpace = new Coordinate(3, 0);

        move = new Move(firstRowFourthSpace, secondRowThirdSpace);

        assertTrue(board.movePiece(move));

        // Jump the black piece over the red piece and it will become a king
        move = new Move(thirdRowSecondSpace, firstRowFourthSpace);

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertTrue(newBoardState[0][3].isKing());

        // Test moving down and left for black king

        move = new Move(firstRowFourthSpace, secondRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[1][2], oldBoardState[0][3]);
        assertNull(newBoardState[0][3]);

        // Test moving down and right for black king
        Coordinate fourthRowFifthSpace = new Coordinate(4, 3);
        move = new Move(secondRowThirdSpace, fourthRowFifthSpace);

        oldBoardState = board.getCurrentBoardState();


        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[3][4], oldBoardState[1][2]);
        assertNull(newBoardState[1][2]);
        assertNull(newBoardState[2][3]);



        // Test moving up and left for black king
        move = new Move(fourthRowFifthSpace, thirdRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[2][3], oldBoardState[3][4]);
        assertNull(newBoardState[3][4]);


        //  Test moving up and right for black king


        // Move out of the way of the red piece
        move = new Move(thirdRowFourthSpace, secondRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[1][2], oldBoardState[2][3]);
        assertNull(newBoardState[2][3]);


        // Now move up and right
        move = new Move(secondRowThirdSpace, firstRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[0][3], oldBoardState[1][2]);
        assertNull(newBoardState[1][2]);



        // Make a red king

        // Need to move black pieces out of the way

        // move 7th row 4th space up and left twice

        Coordinate seventhRowFourthSpace = new Coordinate(3, 6);

        move = new Move(seventhRowFourthSpace, sixthRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][2], oldBoardState[6][3]);
        assertNull(newBoardState[6][3]);

        move = new Move(sixthRowThirdSpace, fifthRowSecondSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][1], oldBoardState[5][2]);
        assertNull(newBoardState[5][2]);


        // move 8th row 5th space up and left once
        Coordinate eighthRowFifthSpace = new Coordinate(4, 7);

        move = new Move(eighthRowFifthSpace, seventhRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[6][3], oldBoardState[7][4]);
        assertNull(newBoardState[7][4]);


        // move 4th row 1st space down and right (jump over the first black piece)

        move = new Move(fourthRowFirstSpace, fifthRowSecondSpace);

        // An extra chance to test an illegal move
        assertFalse(board.movePiece(move));

        move = new Move(fourthRowFirstSpace, sixthRowThirdSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][2], oldBoardState[3][0]);
        assertNull(newBoardState[3][0]);
        assertNull(newBoardState[4][1]);


        // move it again (jump over the second black piece)
        move = new Move(sixthRowThirdSpace, eighthRowFifthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[7][4], oldBoardState[5][2]);
        assertNull(newBoardState[5][2]);
        assertNull(newBoardState[6][3]);

        // Now it should be a king
        assertTrue(newBoardState[7][4].isKing());


        // Test moving up and left for red king

        move = new Move(eighthRowFifthSpace, seventhRowFourthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[6][3], oldBoardState[7][4]);
        assertNull(newBoardState[7][4]);


        // Test moving up and right for red king
        move = new Move(seventhRowFourthSpace, fifthRowSixthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][5], oldBoardState[6][3]);
        assertNull(newBoardState[6][3]);
        assertNull(newBoardState[5][2]);


        // Test moving down and right for red king

        // Move the black piece out of the way
        Coordinate sixthRowSeventhSpace = new Coordinate(6, 5);
        Coordinate fifthRowEighthSpace = new Coordinate(7, 4);

        move = new Move(sixthRowSeventhSpace, fifthRowEighthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[4][7], oldBoardState[5][6]);
        assertNull(newBoardState[5][6]);

        // Move the RED king
        move = new Move(fifthRowSixthSpace, sixthRowSeventhSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[5][6], oldBoardState[4][5]);
        assertNull(newBoardState[4][5]);

        //  Test moving down and left for red king
        move = new Move(sixthRowSeventhSpace, eighthRowFifthSpace);

        oldBoardState = board.getCurrentBoardState();

        assertTrue(board.movePiece(move));

        newBoardState = board.getCurrentBoardState();

        assertEquals(newBoardState[7][4], oldBoardState[5][6]);
        assertNull(newBoardState[5][6]);

        assertNull(newBoardState[6][5]);

//        for(int i = 0; i < 8; i++){
//            System.out.println();
//            for(int j = 0; j < 8; j++){
//                System.out.print(newBoardState[i][j] + " ");
//            }
//        }

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

        // TODO: Write tests
        fail();
    }

    @org.junit.jupiter.api.Test
    void checkWinCon() {

        // TODO: Write tests
        fail();
    }



}