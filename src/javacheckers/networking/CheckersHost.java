package javacheckers.networking;

import javacheckers.model.Board;

import java.io.IOException;

public class CheckersHost extends Host {


    /**
     * Create a host listening on the specified port, and start the message processing thread.
     *
     * @param port The port to listen on
     * @throws IOException if it is not possible to create a listening socket on the specified port
     */
    public CheckersHost(int port) throws IOException {
        super(port);
    }

    /**
     * Responds when a message is received from a client.  The message will be a move object to be applied to each
     * client's board state
     * @param playerID
     * @param message The actual message that was sent
     */
    protected void messageReceived(int playerID, Object message){
        // Send the move so that the players can apply the move
        sendToAll(message);
    }

    /**
     * Called when a new player is connected.  If the new player is the second player, we close the server's listening
     * socket and start the game.
     * @param playerID The ID of the newly connected player
     */
    protected void playerConnected(int playerID){
        if(getClientList().length == 2){
            shutdownServerSocket();

            // TODO: send a game start message
            // sendToAll(gameStartMessage);
        }
    }

    /**
     * Called when a player disconnects.  This will end the game and cause the other player to disconnect as well.
     * @param playerID The ID of the disconnected player.
     */
    protected void playerDisconnected(int playerID){
        // TODO: Send a disconnect message
        // sendToAll(playerForfeitMessage);
    }
}
