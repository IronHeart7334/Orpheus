package net.protocols;

import java.io.IOException;
import net.OrpheusServer;
import net.ServerMessage;

/**
 * This class should be used to clarify exactly
 * what the OrpheusServer should do with messages
 * at any given time.
 * 
 * @author Matt Crow
 */
public abstract class AbstractOrpheusServerProtocol {
    
    /**
     * Restarts the server, and applies this as its protocol
     * @throws IOException 
     */
    public final void applyProtocol() throws IOException{
        OrpheusServer server = OrpheusServer.getInstance();
        server.restart();
        server.setProtocol(this);
        doApplyProtocol();
    }
    
    /**
     * An instance of OrpheusServer should pass itself as a parameter to this function
     * upon receiving a server message.
     * 
     * @param sm the message received
     * @param forServer the server which received this message
     * @return whether or not this method handled the message.
     */
    public abstract boolean receiveMessage(ServerMessage sm, OrpheusServer forServer);
    
    /**
     * Called whenever applyProtocol() is
     * invoked. This should reset the protocol,
     * preparing it.
     */
    public abstract void doApplyProtocol();
}
