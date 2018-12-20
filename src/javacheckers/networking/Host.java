package javacheckers.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Defines a host object for connecting with clients and passing messages
 * Designed and adapted from the notes here
 * http://math.hws.edu/javanotes/c12/s5.html
 */
public class Host {

    /**
     * Defines a mapping between client ids and their connection objects
     */
    private TreeMap<Integer, ConnectionToClient> clientConnections;

    /**
     * A Queue of incoming messages from clients to be processed.
     */
    private LinkedBlockingQueue<Message> incomingMessages;

    /**
     * Determines if the ObjectOutputStream objects should be reset before sending each message
     */
    private volatile boolean autoreset;


    private ServerSocket serverSocket;  // To listen for new connections
    private Thread serverThread;        // A thread to accept new connections on the serverSocket
    private volatile boolean shutdown;  // True if the server has stopped listening

    private int nextID;                 // The next id to give a new client

    /**
     * Create a host listening on the specified port, and start the message processing thread.
     * @param port The port to listen on
     * @throws IOException if it is not possible to create a listening socket on the specified port
     */
    public Host(int port) throws IOException{

        // Initialize the things
        clientConnections = new TreeMap<Integer, ConnectionToClient>();
        incomingMessages = new LinkedBlockingQueue<Message>();
        serverSocket = new ServerSocket(port);
        System.out.println("Listening for client connections...");
        serverThread = new ServerThread();
        serverThread.start();

        // Create a thread to read incoming connections
        Thread readerThread = new Thread(){
            public void run(){
                while(true){
                    try {
                        Message msg = incomingMessages.take();
                        messageReceived(msg.clientConnection, msg.message);
                    }catch (Exception e){
                        System.out.println("Exception while handling received message");
                        e.printStackTrace();
                    }
                }
            }
        };

        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Called when a message is received by a client.
     * @param clientID The ID of the client the message is from
     * @param message The actual message that was sent
     */
    protected void messageReceived(int clientID, Object message){
        // Do nothing, it will be the job of the subclass to handle this
    }

    /**
     * Called after a new client has connected
     * @param clientID The ID of the new client
     */
    protected void clientConnected(int clientID){
        // Do nothing, it will be the job of the subclass to handle this
    }

    /**
     * Called when a client disconnects
     * @param clientID The ID of the client that disconnected
     */
    protected void clientDisconnected(int clientID){
        // Do nothing, it will be the job of the subclass to handle this
    }

    /**
     * Called after a connection is received to handle extra set up.
     * @param clientID The id of the client to shake hands with
     * @param in The stream from which messages from the client can be read
     * @param out The stream that messages to the client can be sent
     * @throws IOException if there is a problem with the connection. The connection will be closed and the client
     * never added to the list.
     */
    protected void extraHandshake(int clientID, ObjectInputStream in, ObjectOutputStream out) throws IOException{
        // Do nothing, it will be the job of the subclass to handle this
    }

    /**
     * Get a list of ID numbers of the clients that are currently connected.
     * @return An array containing the ID numbers of currently connected clients
     */
    synchronized public int[] getClientList(){
        int[] clients = new int[clientConnections.size()];
        int i = 0;
        for(int c : clientConnections.keySet()){
            clients[i++] = c;
        }

        return clients;
    }

    /**
     * Stops listening without dropping connections.  Useful if a max number of connections is reached.
     */
    public void shutdownServerSocket(){
        if (serverThread == null){
            return;
        }

        incomingMessages.clear();
        shutdown = true;

        try {
            serverSocket.close();
        }catch (IOException e){

        }

        serverThread = null;
        serverSocket = null;
    }

    /**
     * Restarts listening and accepting new clients
     * @param port The port to listen on
     * @throws IOException if it is not possible to create a socket on the specified port
     */
    public void restartServer(int port) throws IOException{
        if(serverThread != null && serverThread.isAlive()){
            throw new IllegalStateException("Server is already listening for connections.");
        }

        shutdown = false;

        serverSocket = new ServerSocket(port);
        serverThread = new ServerThread();
        serverThread.start();
    }

    /**
     * Disconnect all currently connected clients and stop accepting new clients.
     */
    public void shudownHost(){
        shutdownServerSocket();
        sendToAll(new DisconnectMessage("*shutdown*"));

        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){

        }

        for(ConnectionToClient client : clientConnections.values()){
            client.close();
        }
    }

    /**
     * Sends a specified Object as a message to all connected clients.
     * @param message The message to send to all clients.  It must implement the Serializable interface and
     *               must not be null.
     */
    synchronized public void sendToAll(Object message){
        if(message == null){
            throw new IllegalArgumentException("Null messages not allowed.");
        }

        if(! (message instanceof Serializable)){
            throw new IllegalArgumentException("Messages must implement the serializable interface.");
        }

        for(ConnectionToClient client : clientConnections.values()){
            client.send(message);
        }
    }


    /**
     * Sends a specified Object as a message to the client with the specified ID.
     * @param clientID The ID of the client to send the message to
     * @param message The message to send to the client.  Must implement the serializable interface and must not be null.
     * @return
     */
    synchronized public boolean sendToOne(int clientID, Object message){
        if(message == null){
            throw new IllegalArgumentException("Null messages not allowed.");
        }

        if(! (message instanceof Serializable)){
            throw new IllegalArgumentException("Messages must implement the serializable interface.");
        }

        ConnectionToClient client = clientConnections.get(clientID);

        if(client == null){
            return false;
        }else{
            client.send(message);
            return true;
        }
    }

    /**
     * Reset all output streams after any messages currently in the output queue have been sent.
     * Use this if the same object is sent more than once, and some changes have been made to it.
     */
    public void resetOutput(){
        ResetSignal rs = new ResetSignal();
        for(ConnectionToClient client : clientConnections.values()){
            client.send(rs);
        }
    }

    /**
     * If autoreset is set to true, all output streams will be reset before each transmission.
     * @param auto The value to set autoreset to
     */
    public void setAutoreset(boolean auto){
        autoreset = auto;
    }

    /**
     * Return the value of the autoreset property
     * @return The value of the autoreset property
     */
    public boolean getAutoreset(){
        return autoreset;
    }

    /**
     * Called when a message is received from a client
     * @param fromConnection The connection the message is received from
     * @param message The message received
     */
    synchronized private void messageReceived(ConnectionToClient fromConnection, Object message){
        // Note: Disconnect messages are handled in the ConnectionToClientClass
        int sender = fromConnection.getClient();
        messageReceived(sender, message);
    }

    /**
     * Accept a new connection and notify other clients
     * @param newConnection The new connection to accept
     */
    synchronized private void acceptConnection(ConnectionToClient newConnection){
        int ID = newConnection.getClient();
        clientConnections.put(ID, newConnection);

        StatusMessage sm = new StatusMessage(ID, true, getClientList());
        sendToAll(sm);

        clientConnected(ID);
        System.out.println("Connection accepted from client number " + ID);
    }

    /**
     * Disconnect from a client
     * @param clientID The ID of the client to disconnect from
     */
    synchronized private void clientDisconnectedFromServer(int clientID){
        if(clientConnections.containsKey(clientID)){
            clientConnections.remove(clientID);
            StatusMessage sm = new StatusMessage(clientID, false, getClientList());
            sendToAll(sm);
            clientDisconnected(clientID);
            System.out.println("Connection with client number " + clientID + " closed by Disconnect Message from client.");
        }
    }

    /**
     * Called when a client connection is closed due to error.  Notifies other clients of the status.
     * @param clientConnection The connection that was disconnected with error
     * @param message An error message
     */
    synchronized private void connectionToClientClosedWithError(ConnectionToClient clientConnection, String message){
        int ID = clientConnection.getClient();
        if(clientConnections.remove(ID) != null){
            StatusMessage sm = new StatusMessage(ID, false, getClientList());
            sendToAll(sm);
        }
    }

    /**
     * Represents a message that can be sent to clients
     */
    private class Message{
        ConnectionToClient clientConnection;
        Object message;
    }

    /**
     * A thread that can listen for connection requests from clients
     */
    private class ServerThread extends Thread{
        public void run(){
            try{
                while(!shutdown){
                    Socket connection = serverSocket.accept();
                    if(shutdown){
                        System.out.println("Listener socket has shut down.");
                    }
                    new ConnectionToClient(incomingMessages, connection);
                }
            }catch (Exception e){
                if(shutdown){
                    System.out.println("Listener Socket has shut down.");
                }else{
                    System.out.println("Listener socket has been shut down by error: " + e);
                }
            }
        }
    }

    /**
     * Handles communication with one client
     */
    private class ConnectionToClient{

        private int clientID;                                   // The ID for this client
        private BlockingQueue<Message> incomingMessages;        // The incoming message queue for this client
        private LinkedBlockingQueue<Object> outgoingMessages;   // The outgoing message queue for this client
        private Socket connection;                              // The socket for communicating on
        private ObjectInputStream in;                           // The input stream to read bytes from
        private ObjectOutputStream out;                         // The output stream to send bytes to
        private volatile boolean closed;                        // Set to true when connection is closing normally
        private Thread sendThread;                              // Handles setup and outgoing messages
        private volatile Thread receiveThread;                  // Created after connection is open

        /**
         * Create a new connection to client/
         * @param receivedMessageQueue The queue for incoming messages to be stored
         * @param connection The connection socket
         */
        ConnectionToClient(BlockingQueue<Message> receivedMessageQueue, Socket connection){
            this.connection = connection;
            incomingMessages = receivedMessageQueue;
            outgoingMessages = new LinkedBlockingQueue<Object>();
            sendThread = new SendThread();
            sendThread.start();
        }

        /**
         * Get this client's ID
         * @return This client's ID
         */
        int getClient(){
            return clientID;
        }

        /**
         * Close connection to this client
         */
        void close(){
            closed = true;
            sendThread.interrupt();
            if(receiveThread != null){
                receiveThread.interrupt();
            }

            try{
                connection.close();
            }catch (IOException e){

            }
        }


        /**
         * Send the given object to this client
         * @param obj The object to sent to the client
         */
        void send(Object obj){
            if(obj instanceof DisconnectMessage){
                outgoingMessages.clear();
            }

            outgoingMessages.add(obj);
        }

        /**
         * Close this connection with an error message
         * @param message The error message
         */
        private void closedWithError(String message){
            connectionToClientClosedWithError(this, message);
            close();
        }

        /**
         * Handle set up and then create a thread for receiving messages, then send outgoing messages.
         */
        private class SendThread extends Thread{
            public void run(){
                try{
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());
                    String handle = (String)in.readObject();    // First input must be "This game rocks"
                    System.out.println(handle);
                    if(! "This game rocks".equals(handle)){
                        throw new Exception("Incorrect handle string received from client.");
                    }

                    // Get ID for this client
                    synchronized (Host.this){
                        clientID = nextID ++;
                    }

                    // Send the ID to the client
                    out.writeObject(clientID);
                    out.flush();

                    // Do extra stuff before connection is established
                    extraHandshake(clientID, in, out);

                    acceptConnection(ConnectionToClient.this);
                    receiveThread = new ReceiveThread();
                    receiveThread.start();
                } catch (Exception e){
                    try {
                        closed = true;
                        connection.close();
                    }catch (Exception e1){

                    }

                    System.out.println("\nError while setting up connection: " + e);
                    e.printStackTrace();
                    return;
                }

                // Start sending messages
                try{
                    while(!closed){
                        try {
                            Object message = outgoingMessages.take();
                            if(message instanceof ResetSignal){
                                out.reset();
                            }else{
                                if(autoreset){
                                    out.reset();
                                }
                                out.writeObject(message);
                                out.flush();

                                if(message instanceof DisconnectMessage){
                                    close();
                                }
                            }
                        }catch (InterruptedException e){
                            // Connection is closing
                        }
                    }
                }catch (IOException e){
                    if (!closed){
                        closedWithError("Error while sending data to client.");
                        System.out.println("Host send thread terminated by IOException: " + e);
                    }
                }catch (Exception e) {
                    if (!closed){
                        closedWithError("Internal Error: Unexpected exception in output thread: " + e);
                        System.out.println("\nUnexpected error shut down hub's send thread:");
                        e.printStackTrace();
                    }
                }

            }
        }

        /**
         * A thread for receiving incoming messages
         */
        private class ReceiveThread extends Thread{
            public void run(){
                try {
                    while ( ! closed ) {
                        try {
                            Object message = in.readObject();
                            Message msg = new Message();
                            msg.clientConnection = ConnectionToClient.this;
                            msg.message = message;
                            if ( ! (message instanceof DisconnectMessage) )
                                incomingMessages.put(msg);
                            else {
                                closed = true;
                                outgoingMessages.clear();
                                out.writeObject("*goodbye*");
                                out.flush();
                                clientDisconnected(clientID);
                                close();
                            }
                        }
                        catch (InterruptedException e) {
                            // should mean that connection is closing
                        }
                    }
                }
                catch (IOException e) {
                    if (! closed) {
                        closedWithError("Error while reading data from client.");
                        System.out.println("Hub receive thread terminated by IOException: " + e);
                    }
                }
                catch (Exception e) {
                    if ( ! closed ) {
                        closedWithError("Internal Error: Unexpected exception in input thread: " + e);
                        System.out.println("\nUnexpected error shuts down hub's receive thread:");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
