package net.protocols;

import gui.components.Chat;
import java.io.IOException;
import java.util.List;
import net.OrpheusServer;
import net.messages.ServerMessagePacket;

/**
 * need to override apply protocol so this doesn't apply over other protocols
 * @author Matt
 */
public class ChatProtocol extends AbstractOrpheusServerProtocol{
    private final Chat widget; // he he he. Widgit is fun to say
    
    public ChatProtocol(Chat chat){
        widget = chat;
    }
    
    @Override
    public void applyProtocol() throws IOException {
        OrpheusServer.validateServer();
        OrpheusServer server = OrpheusServer.getInstance();
        server.restart();
        server.setChatProtocol(this);
        doApplyProtocol();
    }

    @Override
    public void doApplyProtocol() {
        List<String> ips = OrpheusServer.getInstance().getIpList();
        widget.logLocal("Initialized chat server on the following addresses:");
        ips.forEach((i)->widget.logLocal("* " + i));
        widget.logLocal("Have other people use the /connect command with one of these addresses to connect.");
    }
    
    private void receiveChatMsg(ServerMessagePacket sm){
        widget.logLocal(String.format("(%s): %s", sm.getSender().getName(), sm.getMessage().getBody()));
    }
    
    @Override
    public boolean receiveMessage(ServerMessagePacket sm, OrpheusServer forServer) {
        boolean handled = true;
        switch(sm.getMessage().getType()){
            case CHAT:
                receiveChatMsg(sm);
                break;
            default:
                handled = false;
                break;
        }
        return handled;
    }
}
