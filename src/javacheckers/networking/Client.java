package javacheckers.networking;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a client that can connnect to a Host
 */
abstract public class Client {

    /**
     * A list of the IDs of all clients who are currently connected to the host.
     */
    protected int[] connectedClientIDs = new int[0];

    /**
     * Determines if the outputstreams should be reset after each transmission
     */
    private volatile boolean autoreset;

    /**
     * The connection to the host
     */
    private final ConnectionToHost connection;

    /**
     * Constructor that creates a connection to the host.
     * @param hostName The ip address of the host
     * @param hostPort The port the host is listening to for connection requests
     * @throws IOException if any problems occur when trying to connect
     */
    public Client(String hostName, int hostPort) throws IOException {
        connection = new ConnectionToHost(hostName, hostPort);
    }

    /**
     * Called when a message is received from the host.  Override this to determine how to respond to messages
     * @param message The received message
     */
    abstract protected void messageReceived(Object message);

    /**
     * Called when this client is notified that a client has connected to the host.
     * @param newPlayerID The ID of the newly connected client
     */
    protected void clientConnected(int newPlayerID){

    }

    /**
     * Called when this client is notified that another client has disconnected from the host.
     * @param departingPlayerID The ID of the client who has disconnected
     */
    protected void clientDisconnected(int departingPlayerID){

    }

    /**
     * Called when the connection to the host is closed due to an error.
     * @param message An error message
     */
    protected void connectionClosedByError(String message){

    }

    /**
     * Called when the connectino to the host is shutdown because the server is shutting down.
     * @param message A good message sent by the host that it is being shutdown properly (without error)
     */
    protected void serverShutdown(String message){

    }

    /**
     * Called after connection to the host is opened and after the client is assigned an ID number.  This method does
     * any extra checking or set up before the connection is fully established.
     * @param in A stream that messages from the host can be read from
     * @param out A stream that messages to the host can be sent with
     * @throws IOException
     */
    protected void extraHandshake(ObjectInputStream in, ObjectOutputStream out) throws IOException{

    }

    /**
     * Disconnect cleanly from the host.
     */
    public void disconnect(){
        if(!connection.closed){
            connection.send(new DisconnectMessage("Goodbye Earl!"));
        }
    }

    /**
     * Add a message to the queue to be sent to the host.
     * @param message The Object message to send to the host, must implement Serializable and cannot be null
     * @throws IllegalArgumentException If the message is null or does not implement Serializable
     * @throws IllegalStateException If the connection to the host is closed
     */
    public void send(Object message) throws IllegalArgumentException, IllegalStateException{
        if(message == null){
            throw new IllegalArgumentException("Null cannot be sent as a message.");
        }

        if(!(message instanceof Serializable)){
            throw new IllegalArgumentException("Messages must implement the Serializable interface.");
        }

        if(connection.closed){
            throw new IllegalStateException("Message cannot be sent because the connection is closed.");
        }

        connection.send(message);
    }

    /**
     * Return the ID of this client that was assigned when the connection was established.
     * @return The ID of this client
     */
    public int getID(){
        return connection.id_number;
    }

    /**
     * Resets the output stream, after any messages currently in the output queue have been sent.
     * This is used if the same object needs to be sent more than once, and changes have been made to it between
     * transmissions.
     */
    public void resetOutput(){
        connection.send(new ResetSignal());
    }

    /**
     * Set the autoreset property, which determines if output streams will be reset before every transmission.  Use this
     * if the same object is going to be continually changed and retransmitted.
     * @param auto True or False
     */
    public void setAutoreset(boolean auto){
        autoreset = auto;
    }

    /**
     * Return the value of the autoreset property
     * @return
     */
    public boolean getAutoreset(){
        return autoreset;
    }

    /**
     * Handles actual communication with the host
     */
    private class ConnectionToHost{

        private final int id_number;               // The ID of this client, assigned by the hub.
        private final Socket socket;               // The socket that is connected to the Hub.
        private final ObjectInputStream in;        // A stream for sending messages to the Hub.
        private final ObjectOutputStream out;      // A stream for receiving messages from the Hub.
        private final SendThread sendThread;       // The thread that sends messages to the Hub.
        private final ReceiveThread receiveThread; // The thread that receives messages from the Hub.

        private final LinkedBlockingQueue<Object> outgoingMessages;  // Queue of messages waiting to be transmitted.

        private volatile boolean closed;     // This is set to true when the connection is closing.


        /**
         * Opens the connection and sends an introduction message to the host.
         * @param host The IP address of the host
         * @param port The port the host is listening for connections on
         * @throws IOException If there are issues creating the connection
         */
        ConnectionToHost(String host, int port) throws IOException{
            outgoingMessages = new LinkedBlockingQueue<Object>();
            socket = new Socket(host,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("Hello Hub");
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            try {
                Object response = in.readObject();
                id_number = ((Integer)response).intValue();
            }
            catch (Exception e){
                throw new IOException("Illegal response from server.");
            }
            extraHandshake(in,out);  // Will throw an IOException if handshake doesn't succeed.
            sendThread = new SendThread();
            receiveThread = new ReceiveThread();
            sendThread.start();
            receiveThread.start();
        }

        /**
         * This method is called to close the connection.  It can be called from outside
         * this class, and it is also used internally for closing the connection.
         */
        void close() {
            closed = true;
            sendThread.interrupt();
            receiveThread.interrupt();
            try {
                socket.close();
            }
            catch (IOException e) {
            }
        }

        /**
         * This method is called to transmit a message to the host.
         * @param message the message, which must be a Serializable object.
         */
        void send(Object message) {
            outgoingMessages.add(message);
        }

        /**
         * This method is called by the threads that do input and output
         * on the connection when an IOException occurs.
         */
        synchronized void closedByError(String message) {
            if (! closed ) {
                connectionClosedByError(message);
                close();
            }
        }

        /**
         * This class defines a thread that sends messages to the host.
         */
        private class SendThread extends Thread {
            public void run() {
                System.out.println("Client send thread started.");
                try {
                    while ( ! closed ) {
                        Object message = outgoingMessages.take();
                        if (message instanceof ResetSignal) {
                            out.reset();
                        }
                        else {
                            if (autoreset)
                                out.reset();
                            out.writeObject(message);
                            out.flush();
                            if (message instanceof DisconnectMessage) {
                                close();
                            }
                        }
                    }
                }
                catch (IOException e) {
                    if ( ! closed ) {
                        closedByError("IO error occurred while trying to send message.");
                        System.out.println("Client send thread terminated by IOException: " + e);
                    }
                }
                catch (Exception e) {
                    if ( ! closed ) {
                        closedByError("Unexpected internal error in send thread: " + e);
                        System.out.println("\nUnexpected error shuts down client send thread:");
                        e.printStackTrace();
                    }
                }
                finally {
                    System.out.println("Client send thread terminated.");
                }
            }
        }

        /**
         * This class defines a thread that reads messages from the host.
         */
        private class ReceiveThread extends Thread {
            public void run() {
                System.out.println("Client receive thread started.");
                try {
                    while ( ! closed ) {
                        Object obj = in.readObject();
                        if (obj instanceof DisconnectMessage) {
                            close();
                            serverShutdown(((DisconnectMessage)obj).message);
                        }
                        else if (obj instanceof StatusMessage) {
                            StatusMessage msg = (StatusMessage)obj;
                            connectedClientIDs = msg.clients;
                            if (msg.connecting)
                                clientConnected(msg.clientID);
                            else
                                clientDisconnected(msg.clientID);
                        }
                        else
                            messageReceived(obj);
                    }
                }
                catch (IOException e) {
                    if ( ! closed ) {
                        closedByError("IO error occurred while waiting to receive  message.");
                        System.out.println("Client receive thread terminated by IOException: " + e);
                    }
                }
                catch (Exception e) {
                    if ( ! closed ) {
                        closedByError("Unexpected internal error in receive thread: " + e);
                        System.out.println("\nUnexpected error shuts down client receive thread:");
                        e.printStackTrace();
                    }
                }
                finally {
                    System.out.println("Client receive thread terminated.");
                }
            }
        }


    }

}

