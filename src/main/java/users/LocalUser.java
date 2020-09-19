package users;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class LocalUser extends AbstractUser {
    private String remotePlayerId;
    
    private static LocalUser instance;
    
    private LocalUser(String name){
        super(name);
        if(instance != null){
            throw new ExceptionInInitializerError("LocalUser is a singleton: Use LocalUser.getInstance() instead of explicitely calling the constructor");
        }
        remotePlayerId = null;
    }
    private LocalUser(){
        this(AbstractUser.DEFAULT_NAME);
    }
    
    public static LocalUser getInstance(){
        if(instance == null){
            instance = new LocalUser();
        }
        return instance;
    }
    
    public final void loginWindow(){
        if(getName().equals(AbstractUser.DEFAULT_NAME)){
            setName(JOptionPane.showInputDialog("Enter a username:"));
        }
    }
    
    public final void setRemotePlayerId(String id){
        remotePlayerId = id;
    }
    
    public final String getRemotePlayerId(){
        return remotePlayerId;
    }

    @Override // not sure if I like this
    public InetAddress getIpAddress() {
        InetAddress ret = null;
        try {
            ret = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
