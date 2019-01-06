package javacheckers.controller;

import javacheckers.model.Board;
import javacheckers.model.Move;
import javacheckers.model.User;
import javacheckers.networking.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;

public class GameController {


    // TODO: Set up the game interaction to start sending moves


    public static final int HOST_PORT = 8888;

    public static final int SQUARE_SIZE = 60;

    private CheckersClient client;
    private CheckersHost host;
    private Board board;

    private String username;
    private boolean isHost = false;

    @FXML
    GridPane gridPane;



    public void setUsername(String name){
        this.username = name;
    }

    public void startHost(){
        try {
            this.host = new CheckersHost(HOST_PORT);
            this.isHost = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startClient(String hostname){
        try {
            this.client = new CheckersClient(hostname, HOST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startGame(){

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
                gridPane.setHgap(0);
                gridPane.setVgap(0);
                for (int row = 0; row < board.BOARD_SIZE; row++) {
                    for (int col = 0; col < board.BOARD_SIZE; col++) {
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

                }
            }
        });



    }

    private Color determineSquareColour(int row, int col){
        if(row % 2 == 0){
            if(col % 2 == 0){
                return Color.WHITE;
            }else{
                return Color.BLACK;
            }
        }else{
            if(col % 2 == 0){
                return Color.BLACK;
            }else{
                return Color.WHITE;
            }
        }
    }

    public void applyMove(Move move){
        board.movePiece(move);

    }

    public void close(){

        this.client.disconnect();

        try{
            this.host.shudownHost();
        }catch (Exception e){

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
            System.out.println("ALL OK..!");
            //Open another window on clicking the OK button

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
                // TODO: This is not being called
                this.disconnect();
                SwingUtilities.invokeLater(new Runnable() {
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
    }

}
