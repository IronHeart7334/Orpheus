package net;

import net.protocols.AbstractOrpheusServerProtocol;
import controllers.Master;
import controllers.User;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;
import static java.lang.System.out;
import java.net.InetSocketAddress;
import java.net.SocketException;
import javax.json.JsonException;
import serialization.JsonUtil;
import util.SafeList;

/**
 * OrpheusServer is a somewhat deceptive title, as this is
 * used to establish peer-to-peer connections between players.
 * 
 * this class handles all of the interactions between computers, including
 * <ul>
 * <li>Chat</li>
 * <li>Joining a pre-match waiting room</li>
 * <li>Joining a World</li>
 * </ul>
 * 
 * Information is sent between OrpheusServers as ServerMessages,
 * encoded into JSON format, then converted into a String.
 * Upon receiving input into its socket, the server will attempt to de-serialize it.
 * If the de-serialization is successful, it takes the type of that message,
 * and calls the corresponding Consumers in the 'receivers' HashMap.
 * 
 * I am in the process of removing this consumer strategy,
 * replacing it with easier to understand AbstractOrpheusServerProtocols
 * 
 * @see OrpheusServer#receive() 
 * @see net.protocols.AbstractOrpheusServerProtocol
 * 
 * @author Matt Crow
 */
public class OrpheusServer {
    private boolean isStarted;
    private ServerSocket server;
    private String ipAddress;
    
    /*
    The users connected to this server, where the key is their
    IP address.
    */
    private final HashMap<String, Connection> connections;
    private Thread connListener; //the thread that listens for attemts to connect to this server
    private volatile boolean listenForConn; //whether or not the connListener thread is active
    
    private final HashMap<ServerMessageType, SafeList<Consumer<ServerMessage>>> receivers; //see the receive method
    private final SafeList<ServerMessage> cachedMessages; //messages received before the receiver could be
    
    private volatile AbstractOrpheusServerProtocol currentProtocol;
    
    public static final int PORT = 5000;
    private static OrpheusServer instance = null;
    
    /**
     * Creates an OrpheusServer.
     * Note that this does not actually start the server,
     * you need to call start() for that.
     */
    private OrpheusServer(){
        if(instance != null){
            throw new ExceptionInInitializerError("OrpheusServer is a singleton class: Use OrpheusServer.getInstance()");
        }
        ipAddress = "127.0.0.1"; // Loopback address. This is just a default value
        receivers = new HashMap<>();
        connections = new HashMap<>();
        cachedMessages = new SafeList<>();
        listenForConn = false;   
        isStarted = false;
        currentProtocol = null;
    }
    
    /**
     * Use this method to interact with the OrpheusServer.
     * This method create the instance of OrpheusServer if it is not yet initialized.
     * 
     * I'm not sure if I want to add a different method which creates the server,
     * so it can disable multiplayer if it fails.
     * 
     * Note that you must still call .start() on the server
     * 
     * @return the instance of OrpheusServer
     */
    public static final OrpheusServer getInstance() {
        if(instance == null){
            instance = new OrpheusServer();
        }
        return instance;
    }
    
    /**
     * 
     * @return this
     * @throws java.io.IOException
     *  
     */
    public OrpheusServer start() throws IOException{
        if(isStarted){
            //don't start if this is already started
            return this;
        }
        
        ipAddress = InetAddress.getLocalHost().getHostAddress();
        server = new ServerSocket(PORT);
        
        reset();
        
        isStarted = true;
        
        return this;
    }
    
    /**
     * Clears all receivers and connections from this,
     * then restarts the connection listener.
     * 
     * I will likely remove this later,
     * once I figure out how I want these
     * protocols to work.
     * 
     * @return this
     */
    public OrpheusServer reset(){
        System.out.println("Server reset");
        receivers.clear();
        currentProtocol = null;
        initReceivers();
        
        //connections.values().forEach((conn)->conn.close());
        //connections.clear();
        
        startConnListener();
        listenForConn = true;
        
        return this;
    }
    
    /**
     * If the server is not started, starts it.
     * If the server is already started, resets it.
     * @return this, for chaining purposes.
     * @throws IOException if an error occurs while starting the server
     */
    public OrpheusServer restart() throws IOException{
        if(isStarted){
            reset();
        } else {
            start();
        }
        return this;
    }
    
    public boolean isStarted(){
        return isStarted;
    }
    
    // delete this later once I am done with protocols
    private void initReceivers(){
        addReceiver(ServerMessageType.PLAYER_JOINED, (ServerMessage sm)->{
            String ip = sm.getIpAddr();
            if(connections.containsKey(ip) && connections.get(ip).getUser() != null){
                out.println("already connected");
            } else if(connections.containsKey(ip)){
                //connected to IP, but no user data
                connections.get(ip).setUser(User.deserializeJson(JsonUtil.fromString(sm.getBody())));
                logConnections();
            } else {
                //not connected, no user data
                try {
                    connect(ip);
                    connections.get(ip).setUser(User.deserializeJson(JsonUtil.fromString(sm.getBody())));
                    logConnections();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        addReceiver(ServerMessageType.PLAYER_LEFT, (ServerMessage sm)->{
            String ip = sm.getSender().getIpAddress();
            if(connections.containsKey(ip)){
                out.println(ip + " left");
                disconnect(sm.getIpAddr());
            }else{
                out.println(ip + " is not connected, so I cannot disconnect from them");
            }
        });
    }
    
    private void startConnListener(){
        if(connListener == null){
            connListener = new Thread(){
                @Override
                public void run(){
                    System.out.println("Server started, waiting for client...");
                    
                    while(listenForConn){
                        try {
                            connect(server.accept());
                        } catch(SocketException ex){
                            ex.printStackTrace();
                            break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("done acception connections.");
                    connListener = null;
                }
            };
            connListener.start();
        }
    }
    
    
    
    public String getIpAddr(){
        return ipAddress;
    }
    
    public synchronized void connect(String ipAddr) throws IOException{
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(ipAddr, PORT), 3000);
        connect(sock);
        
    }
    private synchronized void connect(Socket otherComputer) throws IOException{
        logConnections();
        if(connections.containsKey(otherComputer.getInetAddress().getHostAddress())){
            out.println("Already connected to " + otherComputer.getInetAddress().getHostAddress());
            return;
        }

        Connection conn = new Connection(otherComputer);
        connections.put(otherComputer.getInetAddress().getHostAddress(), conn);

        //do I need to store this somewhere?
        new Thread(){
            @Override
            public void run(){
                String ip = "";
                while(true){
                    try{
                        ip = conn.readFromClient();
                        if(ip == null || ip.toUpperCase().contains(ServerMessageType.SERVER_SHUTDOWN.toString().toUpperCase())){
                            System.out.println("breaking");
                            break;
                        }
                        receive(ip);

                    } catch (EOFException ex){
                        ex.printStackTrace();
                        out.println("connection terminated");
                        break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                out.println("disconnecting...");
                disconnect(otherComputer.getInetAddress().getHostAddress());
            }
        }.start();
        //out.println("connected to " + otherComputer.getInetAddress().getHostAddress());

        //includes the User data so the other computer has access to username
        conn.writeToClient(new ServerMessage(
            Master.getUser().serializeJson().toString(),
            ServerMessageType.PLAYER_JOINED
        ).toJsonString());
        logConnections();
    }
    
    private synchronized void disconnect(String ipAddr){
        if(connections.containsKey(ipAddr)){
            connections.get(ipAddr).close();
            connections.remove(ipAddr);
        }else{
            out.println("not contains key " + ipAddr);
        }
    }
    
    public void send(String msg){
        connections.values().stream().forEach((Connection c)->{
            c.writeToClient(msg);
        });
    }
    
    public void send(ServerMessage sm){
        send(sm.toJsonString());
    }
    
    public boolean send(ServerMessage sm, String ipAddr){
        boolean success = false;
        if(connections.containsKey(ipAddr)){
            connections.get(ipAddr).writeToClient(sm.toJsonString());
            success = true;
        }
        return success;
    }
    
    public void receive(String msg){
        try{
            ServerMessage sm = ServerMessage.deserializeJson(msg);
            if(connections.containsKey(sm.getIpAddr())){
               sm.setSender(connections.get(sm.getIpAddr()).getUser()); 
            } else {
                out.println("I don't recognize " + sm.getIpAddr());
            }
            
            if(receivers.containsKey(sm.getType())){
                receivers.get(sm.getType()).forEach((c)->c.accept(sm));
            } else {
                out.println("I don't have a receiver for this, so I'll cache it: " + msg);
                out.println("(" + sm.hashCode() + ")");
                cachedMessages.add(sm);
            }
            
            if(currentProtocol == null){
                out.println("No current protocol :(");
            } else {
                if(currentProtocol.receiveMessage(sm, this)){
                    out.println("Successfully received!");
                } else {
                    out.println("Nope, didn't receive properly :(");
                }
            }
            
        } catch (JsonException ex){
            out.println("nope. not server message");
            ex.printStackTrace();
        }
    }
    
    /**
     * Migrating to use this in lieu of adding and removing receivers
     * @param protocol the new protocol to use. This can be null
     */
    public void setProtocol(AbstractOrpheusServerProtocol protocol){
        currentProtocol = protocol;
    }
    
    /**
     * 
     * @param key the type of ServerMessage this should receive.
     * @param nomNom the function to run upon receiving a ServerMessage of the given type. 
     * @see ServerMessage
     */
    public void addReceiver(ServerMessageType key, Consumer<ServerMessage> nomNom){
        if(nomNom == null){
            throw new NullPointerException();
        }
        if(!receivers.containsKey(key)){
            receivers.put(key, new SafeList<>());
        }
        receivers.get(key).add(nomNom);
        
        cachedMessages.forEach((ServerMessage sm)->{
            if(sm.getType().equals(key)){
                nomNom.accept(sm);
                cachedMessages.remove(sm);
                out.println("uncached message " + sm.hashCode());
            }
        });
    }
    
    public boolean removeReceiver(ServerMessageType type, Consumer<ServerMessage> nonNom){
        boolean wasRemoved = false;
        if(receivers.containsKey(type)){
            wasRemoved = receivers.get(type).remove(nonNom);
        }
        return wasRemoved;
    }
    
    public final void setAcceptingConn(boolean b){
        listenForConn = b;
        if(b){
            startConnListener();
        }
    }
    
    public final void shutDown(){
        if(!isStarted){
            return;
        }
        send(new ServerMessage(
            "server shutting down",
            ServerMessageType.SERVER_SHUTDOWN
        ));
        
        try {
            server.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        isStarted = false;
    }
    
    public synchronized void logConnections(){
        out.println("CONNECTIONS:");
        connections.values().stream().forEach((Connection c)->c.displayData());
        out.println("END OF CONNECTIONS");
    }
    
    public synchronized void logReceivers(){
        out.println("RECEIVERS:");
        receivers.forEach((smt, sl)->{
            out.println(smt.toString() + ": X" + sl.length());
        });
        out.println("END OF RECEIVERS");
    }
    
    public static void main(String[] args){
        try {
            OrpheusServer os = new OrpheusServer();
            os.logConnections();
            os.logReceivers();
            os.start();
            os.logConnections();
            os.logReceivers();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
