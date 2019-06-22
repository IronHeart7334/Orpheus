package windows.WorldSelect;

import controllers.Master;
import controllers.User;
import gui.Chat;
import gui.Style;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.*;
import javax.swing.JButton;
import javax.swing.Timer;
import net.OrpheusServerState;
import net.ServerMessage;
import net.ServerMessageType;
import serialization.JsonUtil;
import windows.Page;
import windows.SubPage;

/**
 * how to make this send init message when player joins?
 * @author Matt
 */
public class WSWaitForPlayers extends SubPage{
    private int teamSize;
    
    /*
    For now, I'm using IP address as the key, and the User as the value.
    I'm not sure if this will work, I think IP addresses are unique to each computer,
    but I'm not quite sure
    */
    private final HashMap<String, User> team1;
    private final HashMap<String, User> team2;
    private final Chat chat;
    private final JButton joinT1Button;
    private final JButton joinT2Button;
    
    private final Consumer<ServerMessage> receiveJoin = (sm)->{
        sendInit(sm.getSender().getIpAddress());
    };
    private final Consumer<ServerMessage> receiveInit = (sm)->{
        JsonReader read = Json.createReader(new StringReader(sm.getBody()));
        JsonObject obj = read.readObject();
        read.close();
        
        JsonUtil.verify(obj, "team size");
        JsonUtil.verify(obj, "team 1");
        JsonUtil.verify(obj, "team 2");
        
        teamSize = obj.getInt("team size");
        obj.getJsonArray("team 1").stream().forEach((jv)->{
            if(jv.getValueType().equals(JsonValue.ValueType.OBJECT)){
                joinTeam1(User.deserializeJson((JsonObject)jv));
            }
        });
        obj.getJsonArray("team 2").stream().forEach((jv)->{
            if(jv.getValueType().equals(JsonValue.ValueType.OBJECT)){
                joinTeam2(User.deserializeJson((JsonObject)jv));
            }
        });
    };
    private final Consumer<ServerMessage> receiveUpdate = (sm)->{
        //not sure I like this.
        //update messages are either 'join team 1', or 'join team 2'
        String[] split = sm.getBody().split(" ");
        if(null == split[split.length - 1]){
            System.out.println("not sure how to handle this: ");
            sm.displayData();
        } else switch (split[split.length - 1]) {
            case "1":
                joinTeam1(sm.getSender());
                break;
            case "2":
                joinTeam2(sm.getSender());
                break;
            default:
                System.out.println("not sure how to handle this: ");
                sm.displayData();
                break;
        }
    };
    
    //todo add build select, start button, display teams
    public WSWaitForPlayers(Page p){
        super(p);
        
        teamSize = 1;
        team1 = new HashMap<>();
        team2 = new HashMap<>();
        
        joinT1Button = new JButton("Join team 1");
        joinT1Button.addActionListener((e)->{
            joinTeam1(Master.getUser());
        });
        Style.applyStyling(joinT1Button);
        add(joinT1Button);
        
        joinT2Button = new JButton("Join team 2");
        joinT2Button.addActionListener((e)->{
            joinTeam2(Master.getUser());
        });
        Style.applyStyling(joinT2Button);
        add(joinT2Button);
        
        chat = new Chat();
        add(chat);
        
        //grid layout was causing problems with chat.
        //since it couldn't fit in 1/4 of the JPanel, it compressed to just a thin line
        setLayout(new FlowLayout());
        revalidate();
        repaint();
    }
    
    public WSWaitForPlayers startServer(){
        if(Master.getServer() == null){
            try {
                Master.startServer();
                Master.getServer().setState(OrpheusServerState.WAITING_ROOM);
                chat.openChatServer();
                chat.logLocal("Server started on host address " + Master.getServer().getIpAddr());
                Master.getServer().addReceiver(ServerMessageType.PLAYER_JOINED, receiveJoin);
                Master.getServer().addReceiver(ServerMessageType.WAITING_ROOM_INIT, receiveInit);
                Master.getServer().addReceiver(ServerMessageType.WAITING_ROOM_UPDATE, receiveUpdate);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return this;
    }
    
    public WSWaitForPlayers joinServer(String ipAddr){
        if(Master.getServer() == null){
            try {
                Master.startServer();
            } catch (IOException ex) {
                Logger.getLogger(WSWaitForPlayers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(Master.getServer() != null){//successfully started
            Master.getServer().connect(ipAddr);
            chat.joinChat(ipAddr);
            //Master.getServer().addReceiver(ServerMessageType.PLAYER_JOINED, receiveJoin); I don't think we need this on more than 1 person
            Master.getServer().addReceiver(ServerMessageType.WAITING_ROOM_INIT, receiveInit);
            Master.getServer().addReceiver(ServerMessageType.WAITING_ROOM_UPDATE, receiveUpdate);
        }
        return this;
    }
    
    /**
     * Sends a message containing the state of this waiting room.
     * Is sent whenever a player joins the server
     */
    private void sendInit(String ipAddr){
        JsonObjectBuilder build = Json.createObjectBuilder();
        build.add("type", "waiting room init");
        build.add("team size", teamSize);
        
        JsonArrayBuilder t1 = Json.createArrayBuilder();
        team1.values().stream().forEach((User u)->{
            t1.add(u.serializeJson());
        });
        build.add("team 1", t1.build());
        
        JsonArrayBuilder t2 = Json.createArrayBuilder();
        team2.values().stream().forEach((User u)->{
            t2.add(u.serializeJson());
        });
        build.add("team 2", t2.build());
        
        ServerMessage initMsg = new ServerMessage(
            build.build().toString(),
            ServerMessageType.WAITING_ROOM_INIT
        );
        
        Master.getServer().send(initMsg, ipAddr);
    }
    
    public WSWaitForPlayers joinTeam1(User u){
        if(team1.containsKey(u.getIpAddress())){
            chat.logLocal(u.getName() + " is already on team 1.");
        }else if(team1.size() < teamSize){
            if(team2.containsKey(u.getIpAddress())){
                team2.remove(u.getIpAddress());
                chat.logLocal(u.getName() + " has left team 2.");
            }
            team1.put(u.getIpAddress(), u);
            chat.logLocal(u.getName() + " has joined team 1.");
            if(u.equals(Master.getUser())){
                //only send an update if the user is the one who changed teams. Prevents infinite loop
                ServerMessage sm = new ServerMessage(
                    "join team 1",
                    ServerMessageType.WAITING_ROOM_UPDATE
                );
                Master.getServer().send(sm);
            }
            displayData();
        }else{
            chat.logLocal(u.getName() + " cannot joint team 1: Team 1 is full.");
        } 
        return this;
    }
    
    public WSWaitForPlayers joinTeam2(User u){
        if(team2.containsKey(u.getIpAddress())){
            chat.logLocal(u.getName() + " is already on team 2.");
        }else if(team2.size() < teamSize){
            if(team1.containsKey(u.getIpAddress())){
                team1.remove(u.getIpAddress());
                chat.logLocal(u.getName() + " has left team 1.");
            }
            team2.put(u.getIpAddress(), u);
            chat.logLocal(u.getName() + " has joined team 2.");
            if(u.equals(Master.getUser())){
                //only send an update if the user is the one who changed teams. Prevents infinite loop
                ServerMessage sm = new ServerMessage(
                    "join team 2",
                    ServerMessageType.WAITING_ROOM_UPDATE
                );
                Master.getServer().send(sm);
            }
            displayData();
        }else{
            chat.logLocal(u.getName() + " cannot joint team 2: Team 2 is full.");
        } 
        return this;
    }
    
    private void startWorld(){
        //TODO: disable start world button
        chat.log("The game will start in 30 seconds. Please select your build and team.");
        //TODO prevent new people from joining
        Timer t = new Timer(30000, (e)->{
            /*
            TODO:
            obtain player information from each User (don't do this beforehand)
            construct teams and start world
            serialize the world and send it to all connected users
            switch to that new world
            make orpheus server stop acception connections
            remove all of this' receivers from the server
            */
        });
        t.start();
    }
    
    public WSWaitForPlayers setTeamSize(int s){
        teamSize = s;
        return this;
    }
    
    public void displayData(){
        System.out.println("WAITING ROOM");
        System.out.println("Team size: " + teamSize);
        System.out.println("Team 1: ");
        team1.values().forEach((member)->System.out.println("--" + member.getName()));
        System.out.println("Team 2: ");
        team2.values().forEach((member)->System.out.println("--" + member.getName()));
        System.out.println("END OF WAITING ROOM");
    }
}
