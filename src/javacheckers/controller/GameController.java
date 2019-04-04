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
    public void startHost(){
        try {
            this.host = new CheckersHost(HOST_PORT);
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

    public void startGame(){
        // TODO: Figure out how to get the other players username here
        System.out.println("Starting Game");
        User self;
        User other;
        if(this.isHost){
            self = new User(this.username, 0);
            other = new User("Opponenet", 1);
            this.board = new Board(other, self);
        }else{
            self = new User(this.username, 1);
            other = new User("Opponent", 0);
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

    private void drawBoardState(){
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        Piece state[][] = board.getCurrentBoardState();


        for (int row = 0; row < board.BOARD_SIZE; row++) {
            for (int col = 0; col < board.BOARD_SIZE; col++) {
                drawRectangle(row, col);
                if(state[row][col] != null){
                    drawPiece(row, col, state[row][col]);
                }
            }

        }
    }

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
                    Piece p = board.selectPiece(coords);
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
                    Piece p = board.selectPiece(coords);
                    if(p != null && p.getColour() == getColour()) {
                        showPossibleMoves(coords);
                    }
                }
            };

            c.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler);
        }


    }

    private void showPossibleMoves(Coordinate coord){
        List<Move> moves = board.checkMoves(coord);

        // Remove any previous outlines
        drawBoardState();

        for(Move move : moves){
            drawMoveOutline(move);
        }

    }

    private void drawMoveOutline(Move move){
        Coordinate coord = move.getTo();

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

    public void sendMove(Move move){
        this.client.send(move);
    }

    private Color getPieceColour(int i){
        if( i == Board.RED) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

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

    public void showGameEndDialog(){
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
    }

    public void showOpponentDisconnectDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Opponent Has Disonnected");
        alert.setHeaderText(null);
        alert.setContentText("Your opponent has been disconnected.");

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

    }

    private class CheckersClient extends Client {

        public CheckersClient(String hostname, int port) throws IOException {
            super(hostname, port);
        }

        @Override
        protected void messageReceived(Object message) {

            if(message instanceof GameStartMessage){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                });
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
