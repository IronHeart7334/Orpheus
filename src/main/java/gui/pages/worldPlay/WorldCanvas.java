package gui.pages.worldPlay;

import battle.Battle;
import world.AbstractWorldShell;
import util.Settings;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import battle.Team;
import start.MainWindow;
import controls.userControls.AbstractPlayerControls;
import controls.userControls.SoloPlayerControls;
import entities.AbstractPlayer;
import java.awt.Graphics2D;
import java.io.IOException;
import entities.HumanPlayer;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import users.LocalUser;
import util.SerialUtil;
import gui.pages.Canvas;
import world.SoloWorld;
import world.WorldContent;

/**
 * P: pause
 * Z: zoom in
 * X: zoom out
 * 
 * @author Matt Crow
 */
public class WorldCanvas extends Canvas{
    private AbstractWorldShell world;
    private final Timer timer;
    private boolean paused;
    private boolean pauseEnabled;
    
    private String focusedEntityId;
    
    public WorldCanvas(AbstractWorldShell w){
        super();
        world = w;
        
        w.setCanvas(this);
        
        paused = true;
        pauseEnabled = true;
        timer = new Timer(1000 / Settings.FPS, (ActionEvent e) -> {
            world.update();
            endOfFrame();
            repaint();
        });
        timer.setRepeats(true);
        timer.stop();
        
        registerKey(KeyEvent.VK_Z, true, ()->zoomIn());
        registerKey(KeyEvent.VK_X, true, ()->zoomOut());
        registerKey(KeyEvent.VK_P, true, ()->togglePause());
        setZoom(0.5);
        
        focusedEntityId = null;
    }
    
    /**
     * Remember to use this if you want to control a player!
     * @param pc 
     */
    public final void addPlayerControls(AbstractPlayerControls pc){
        addMouseListener(pc);
        addEndOfFrameListener(pc);
        pc.registerControlsTo(this);
        focusedEntityId = pc.getPlayer().id;
    }
    
    public void setPauseEnabled(boolean canPause){
        pauseEnabled = canPause;
        if(!canPause && !timer.isRunning()){
            paused = false;
            timer.start();
        }
    }
    private void togglePause(){
        if(pauseEnabled){
            paused = !paused;
            if(paused){
                timer.stop();
            } else {
                timer.start();
            }
            repaint();
        }
    }
    
    //need this for when leaving world page
    public void stop(){
        paused = true;
        timer.stop();
    }
    
    public AbstractWorldShell getWorldShell(){
        return world;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(focusedEntityId == null){
            centerOn(0, 0);
        } else {
            AbstractPlayer focus = world.getPlayerTeam().getMemberById(focusedEntityId);
            centerOn(
                focus.getX(),
                focus.getY()
            );
        }
		Graphics2D g2d = applyTransforms(g);
		
		world.draw(g2d);
        
		reset();
        
		if(focusedEntityId != null){
            ((HumanPlayer)world.getPlayerTeam().getMemberById(focusedEntityId)).drawHUD(g2d, this);
        }
        
        if(world.getCurrentMinigame() != null && world.getCurrentMinigame().isDone()){
			drawMatchResolution(g2d);
        }
        if(paused){
            drawPause(g2d);
        }
    }
    public void drawPause(Graphics g){
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.red);
		g.drawString("The game is paused", (int) (getWidth() * 0.3), (int) (getHeight() * 0.3));
		g.drawString("Press 'p' to continue", (int) (getWidth() * 0.4), (int) (getHeight() * 0.5));
	}
    
	public void drawMatchResolution(Graphics g){
		paused = true;
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.yellow);
		g.drawString("The match is ended,", (int) (getWidth() * 0.3), (int) (getHeight() * 0.3));
		g.drawString(world.getCurrentMinigame().getWinner().getName(), (int) (getWidth() * 0.5), (int) (getHeight() * 0.5));
		g.drawString("is victorious!", (int) (getWidth() * 0.7), (int) (getHeight() * 0.7));
	}
    
    
    
    public static void main(String[] args) throws IOException{
        LocalUser user = LocalUser.getInstance();
        HumanPlayer player = new HumanPlayer(user.getName());
        
        player.applyBuild(Settings.getDataSet().getDefaultBuild());
        
        SoloWorld world = new SoloWorld(WorldContent.createDefaultBattle());
        Team t1 = new Team("Test", Color.BLUE);
        Team t2 = Team.constructRandomTeam("Rando", Color.yellow, 1, 1);
        t1.addMember(player);
        world.setPlayerTeam(t1).setEnemyTeam(t2);
        
        Battle b = new Battle(10, 5);
        b.setHost(world.getContent());
        world.setCurrentMinigame(b);
        world.init();
        
        WorldCanvas canvas = new WorldCanvas(world);
        canvas.addPlayerControls(new SoloPlayerControls(world, player.id));
        canvas.setPauseEnabled(true);
        MainWindow mw = MainWindow.getInstance();
        WorldPage wp = new WorldPage();
        wp.setCanvas(canvas);
        mw.switchToPage(wp);
        
        user.setRemotePlayerId(player.id);
        
        
        //now to try serializing it...
        String serial = world.getContent().serializeToString();
        
        WorldContent newContent = WorldContent.fromSerializedString(serial);
        world.setContent(newContent);
        newContent.setShell(world);
        
        //user.setPlayer((HumanPlayer)newContent.getPlayerTeam().getMemberById(user.getRemotePlayerId()));
        
        //newWorld.init();
        
        wp = new WorldPage();
        wp.setCanvas(canvas);
        mw.switchToPage(wp);
        canvas.setPauseEnabled(true);
        world.getCanvas().registerKey(KeyEvent.VK_S, true, ()->{
            Team t = world.getPlayerTeam();
            System.out.println("Total entities to serialize: " + t.length());
            String s = SerialUtil.serializeToString(t);
            Team tClone = (Team)SerialUtil.fromSerializedString(s);
            System.out.println("Total entities deserialized: " + tClone.length());
        });
        
        
        
        ObjectOutputStream out = new ObjectOutputStream(System.out);
        String ser = null;
        for(int i = 0; i < 1000000; i++){
            world.getContent().serializeToString();
            out.writeObject(ser);
            //out.reset();
            //WorldContent deser = WorldContent.fromSerializedString(ser);
            //world.setContent(deser);
            if(i % 10000 == 0){
                System.out.println(i);
            }
        }
        out.close();
    }
}
