package javacheckers.controller;

import javacheckers.model.Board;
import javacheckers.model.Move;
import javacheckers.model.User;
import javacheckers.networking.*;

import javax.swing.*;
import java.io.IOException;

public class GameController {


    // TODO: Set up the game interaction to start sending moves


    public static final int HOST_PORT = 8888;

    private CheckersClient client;
    private CheckersHost host;
    private Board board;

    private String username;
    private boolean isHost = false;



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


    }

    public void applyMove(Move move){
        board.movePiece(move);

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
            }else if(message instanceof PlayerForfeitMessage) {
                this.disconnect();
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
