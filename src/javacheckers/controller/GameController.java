package javacheckers.controller;

//import com.sun.glass.ui.Menu;
import javacheckers.model.*;
import javacheckers.networking.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

public class GameController {


    public static final int HOST_PORT = 8888;

    public static final int SQUARE_SIZE = 60;

    private CheckersClient client;
    private CheckersHost host;
    private Board board;

    private String username;
    private boolean isHost = false;

    private boolean gameOver = false;

    @FXML
    GridPane gridPane;

    /**
     * Get the colour for this player
     * @return The colour of the local player
     */
    private int getColour(){
        if (this.isHost){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * Set the local player's username
     * @param name
     */
    public void setUsername(String name){
        this.username = name;
    }

    /**
     * Start the host for the game
     */
    public void startHost(String username){
        try {
            this.host = new CheckersHost(HOST_PORT, username);
            this.isHost = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the client connection to the host
     * @param hostname The hostname to connect to
     */
    public void startClient(String hostname){
        try {
            this.client = new CheckersClient(hostname, HOST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the game
     * @param opponentUsername The username of the opponent
     */
    public void startGame(String opponentUsername){
        System.out.println("Starting Game");
        User self;
        User other;
        if(this.isHost){
            self = new User(this.username, 0);
            other = new User(opponentUsername, 1);
            this.board = new Board(other, self);
        }else{
            self = new User(this.username, 1);
            other = new User(opponentUsername, 0);
            this.board = new Board(self, other);
        }

        // Draw the checker board
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                drawBoardState();
            }
        });



    }

    /**
     * Draw the current state of the checkers board
     */
    private void drawBoardState(){
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        Stage stage = (Stage) gridPane.getScene().getWindow();

        String currentPlayer = board.getCurrentUser().getUserName();

        try {
            if (currentPlayer.equals(this.username)) {
                currentPlayer = "Your";
            } else {
                currentPlayer += "'s";
            }
        }catch (Exception e){

        }

        stage.setTitle(currentPlayer + " turn");

        Piece state[][] = board.getCurrentBoardState();



        for (int row = 0; row < board.BOARD_SIZE; row++) {
            for (int col = 0; col < board.BOARD_SIZE; col++) {
                drawRectangle(row, col);
                if(state[orientNumber(row)][orientNumber(col)] != null){
                    drawPiece(row, col, state[orientNumber(row)][orientNumber(col)]);
                }
            }

        }
    }

    /**
     * Draw one checker on the board
     * @param row The row to draw the piece in
     * @param col The column to draw the piece in
     * @param p The piece to draw
     */
    private void drawPiece(int row, int col, Piece p) {
        if(p.isKing()){
            Polygon polly = new Polygon();
            polly.getPoints().addAll(new Double[]{
                    0.0, 0.0,
                    Double.valueOf(SQUARE_SIZE/2), Double.valueOf(SQUARE_SIZE),
                    Double.valueOf(SQUARE_SIZE), 0.0
            });
            polly.setStroke(Color.BLACK);
            polly.setStrokeType(StrokeType.INSIDE);
            polly.setFill(getPieceColour(p.getColour()));

            polly.setUserData(new Coordinate(col, row));

            GridPane.setRowIndex(polly, row);
            GridPane.setColumnIndex(polly, col);
            gridPane.getChildren().addAll(polly);

            EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Coordinate coords = (Coordinate) polly.getUserData();
                    Piece p = board.selectPiece(orientCoordinate(coords));
                    if(p != null && p.getColour() == getColour()) {
                        showPossibleMoves(coords);
                    }
                }
            };

            polly.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);

        } else {
            Circle c = new Circle();
            c.setRadius(SQUARE_SIZE/2);
            c.setStroke(Color.BLACK);
            c.setStrokeType(StrokeType.INSIDE);
            c.setFill(getPieceColour(p.getColour()));

            c.setUserData(new Coordinate(col, row));
            GridPane.setRowIndex(c, row);
            GridPane.setColumnIndex(c, col);
            gridPane.getChildren().addAll(c);

            EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Coordinate coords = (Coordinate) c.getUserData();
                    Piece p = board.selectPiece(orientCoordinate(coords));
                    if(p != null && p.getColour() == getColour()) {
                        showPossibleMoves(coords);
                    }
                }
            };

            c.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        }


    }

    /**
     * Display all possible moves for a given space on the board to the user
     * @param coord A coordinate of a space to display the moves for
     */
    private void showPossibleMoves(Coordinate coord){
        List<Move> moves = board.checkMoves(orientCoordinate(coord));

        // Remove any previous outlines
        drawBoardState();

        for(Move move : moves){
            drawMoveOutline(move);
        }

    }

    /**
     * Draw a yellow outline around the space that is being moved to in the given move
     * @param move A move to execute
     */
    private void drawMoveOutline(Move move){
        Coordinate coord = orientCoordinate(move.getTo());

        Rectangle rec = new Rectangle();
        rec.setWidth(SQUARE_SIZE);
        rec.setHeight(SQUARE_SIZE);
        rec.setStroke(Color.YELLOW);
        rec.setStrokeWidth(2.0);
        rec.setStrokeType(StrokeType.INSIDE);
        rec.setFill(determineSquareColour(coord.getX(), coord.getY()));

        rec.setUserData(move);

        GridPane.setRowIndex(rec, coord.getY());
        GridPane.setColumnIndex(rec, coord.getX());
        gridPane.getChildren().addAll(rec);


        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Move move = (Move) rec.getUserData();

                sendMove(move);;
            }
        };

        rec.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    /**
     * Orient the given number for drawing the board.  This will convert parts of coordinates to the correct perspective
     * for each player, because we need to draw the board for the red player upside down
     * @param num The number to orient
     * @return The number oriented to the local player's colour
     */
    private int orientNumber(int num){
        if(this.getColour() == board.RED){
            return board.BOARD_SIZE - num - 1;
        }else{
            return num;
        }
    }

    /**
     * Orient the given coordinate for drawing the board.  This will convert parts of coordinates to the correct perspective
     * for each player, because we need to draw the board for the red player upside down
     * @param coord The number to orient
     * @return The coordinate oriented to the local player's colour
     */
    private Coordinate orientCoordinate(Coordinate coord){
        return new Coordinate(orientNumber(coord.getX()), orientNumber(coord.getY()));
    }



    /**
     * Send the given move to the server
     * @param move The move to send
     */
    public void sendMove(Move move){
        this.client.send(move);
    }


    /**
     * Get the colour that corresponds to the integer used to represent it by the board state
     * @param i The integer to get the colour for
     * @return A Colour object representing the colour associated with the given integer
     */
    private Color getPieceColour(int i){
        if( i == Board.RED) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * Draw a rectangle at the given row and column
     * @param row The row to draw the rectangle in
     * @param col The column to draw the rectangle in
     */
    private void drawRectangle(int row, int col) {
        Rectangle rec = new Rectangle();
        rec.setWidth(SQUARE_SIZE);
        rec.setHeight(SQUARE_SIZE);
        rec.setStroke(Color.BLACK);
        rec.setStrokeType(StrokeType.INSIDE);
        rec.setFill(determineSquareColour(row, col));
        GridPane.setRowIndex(rec, row);
        GridPane.setColumnIndex(rec, col);
        gridPane.getChildren().addAll(rec);
    }

    /**
     * Determines what colour the square should be based on its position, for drawing alternating colours for the
     * checkerboard
     * @param row The row the square is in
     * @param col The column the square is in
     * @return The colour that the square should be
     */
    private Color determineSquareColour(int row, int col){
        if(row % 2 == 0){
            if(col % 2 == 0){
                return Color.WHITE;
            }else{
                return Color.TEAL;
            }
        }else{
            if(col % 2 == 0){
                return Color.TEAL;
            }else{
                return Color.WHITE;
            }
        }
    }

    /**
     * Apply the given move to the board state
     * @param move The move to apply to the board state
     */
    public void applyMove(Move move){
        board.movePiece(move);

        if(board.checkWinCon() != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showGameEndDialog();
                }
            });
        }

        board.switchCurrentUser();

        // Draw the checker board
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                drawBoardState();
            }
        });
    }

    /**
     * Close the game
     */
    public void close(){
        System.out.println("CLosing");
        if(this.host != null){
            this.client.send(new PlayerForfeitMessage());
        }

        this.client.disconnect();

        try{
            this.host.shudownHost();
        }catch (Exception e){

        }


    }

    /**
     * Show a dialog box displaying the winner of the game
     */
    public void showGameEndDialog(){
        gameOver = true;
        try {
            this.host.shudownHost();
        }catch (NullPointerException e){
            // The host is null because we are not a host
        }

        User winner = board.checkWinCon();

        String winnerName = winner.getUserName();

        if(winnerName.equals(this.username)){
            winnerName = "You";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(winnerName + " won the game!");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + " won the game!");

        //alert.show();
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK))
        {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/main_menu.fxml"));
                Parent root1 = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root1, 300, 275));
                stage.show();

                Stage oldStage = (Stage) gridPane.getScene().getWindow();
                oldStage.close();


            } catch(Exception e) {
                e.printStackTrace();
            }

        }

        try {
            this.host.shudownHost();
        }catch (Exception e){

        }
    }

    /**
     * Show a dialog displaying that the opponent has been disconnected from the server
     */
    public void showOpponentDisconnectDialog(){
        if(!gameOver) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Opponent Has Disonnected");
            alert.setHeaderText(null);
            alert.setContentText("Your opponent has been disconnected.");
            this.host.shudownHost();
            //alert.show();

            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/main_menu.fxml"));
                    Parent root1 = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root1, 300, 275));
                    stage.show();

                    Stage oldStage = (Stage) gridPane.getScene().getWindow();
                    oldStage.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private class CheckersClient extends Client {


        public CheckersClient(String hostname, int port) throws IOException {
            super(hostname, port);
        }

        @Override
        protected void messageReceived(Object message) {

            if(message instanceof GameStartMessage) {
                System.out.println(username);
                this.send(new PlayerIntroMessage(username, this.getID()));

            }else if(message instanceof PlayerIntroMessage){

                PlayerIntroMessage msg = (PlayerIntroMessage) message;

                if(msg.ID != this.getID()) {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            startGame(msg.username);
                        }
                    });
                }

            }else if(message instanceof PlayerForfeitMessage | message instanceof DisconnectMessage) {

                this.disconnect();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showOpponentDisconnectDialog();
                    }
                });
            }else if(message instanceof Move){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        applyMove((Move) message);
                    }
                });
            }


        }

        @Override
        protected void serverShutdown(String message) {
            super.serverShutdown(message);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    showOpponentDisconnectDialog();
                }
            });
        }


    }

}
