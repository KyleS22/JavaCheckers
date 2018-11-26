package javacheckers.networking;

import java.io.Serializable;

/**
 * Sent to all clients by the Host whenever a client connects or disconnects
 */
final class StatusMessage implements Serializable {

        /**
         * The ID number of the client who has connected or disconnected.
         */
        public final int clientID;

        /**
         * True if the client has just connected; false if the client
         * has just disconnected.
         */
        public final boolean connecting;

        /**
         * The list of clients after the change has been made.
         */
        public final int[] clients;

        public StatusMessage(int clientID, boolean connecting, int[] clients) {
            this.clientID = clientID;
            this.connecting = connecting;
            this.clients = clients;
        }

}


