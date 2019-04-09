package javacheckers.networking;

import javacheckers.model.Board;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CheckersHost extends Host {

    private volatile boolean broadcastShutdown;
    private String username;

    /**
     * Create a host listening on the specified port, and start the message processing thread.
     *
     * @param port The port to listen on
     * @throws IOException if it is not possible to create a listening socket on the specified port
     */
    public CheckersHost(int port, String username) throws IOException {
        super(port);
        this.username = username;

        BroadcastThread broadcastThread = new BroadcastThread();
        broadcastThread.setDaemon(true);
        broadcastThread.start();
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
    protected void clientConnected(int playerID){
        System.out.println("Player connected");
        if(getClientList().length == 2){
            shutdownServerSocket();
            System.out.println("Sending Game Start Message");
            sendToAll(new GameStartMessage());
            broadcastShutdown = true;
        }
    }

    /**
     * Called when a player disconnects.  This will end the game and cause the other player to disconnect as well.
     * @param playerID The ID of the disconnected player.
     */
    protected void clientDisconnected(int playerID){

        System.out.println("Client disconnected");
        sendToAll(new PlayerForfeitMessage());
    }

    private class BroadcastThread extends Thread{
        public void run(){
            DatagramSocket socket = null;
            try{

                socket = new DatagramSocket();
                socket.setBroadcast(true);
                while(!broadcastShutdown){
                    byte[] buffer = username.getBytes();

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 8889);

                    socket.send(packet);
                    Thread.sleep(1000);
                }
                socket.close();
            }catch (Exception e){
                if(broadcastShutdown){
                    System.out.println("Broadcast Socket has shut down.");
                }else{
                    System.out.println("Broadcast Socket has shut down by error: " + e);
                }
                socket.close();
            }
        }
    }
}
