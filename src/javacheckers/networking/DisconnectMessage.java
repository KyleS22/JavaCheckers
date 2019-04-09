package javacheckers.networking;

import java.io.Serializable;

/**
 * Sent from the client to the host when the client wants to disconnect.
 * Also sent from the host to each client when the host shuts down normally.
 */
public final class DisconnectMessage implements Serializable {

    /**
     * The message associated with the disconnect.  When the Hub
     * sends disconnects because it is shutting down, the message
     * is "*shutdown*".
     */
    final public String message;

    /**
     * Creates a DisconnectMessage containing a given String, which
     * is meant to describe the reason for the disconnection.
     */
    public DisconnectMessage(String message) {
        this.message = message;
    }

}
