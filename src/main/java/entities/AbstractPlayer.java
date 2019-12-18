package entities;

import customizables.passives.AbstractPassive;
import customizables.actives.AbstractActive;
import customizables.actives.MeleeActive;
import customizables.Build;
import customizables.characterClass.CharacterStatName;
import customizables.characterClass.CharacterClass;
import java.awt.Color;
import java.awt.Graphics;
import ai.Path;
import ai.PathInfo;
import battle.*;
import ai.PlayerAI;
import statuses.AbstractStatus;
import controllers.Master;
import controllers.World;
import static java.lang.System.out;
import java.util.Arrays;
import java.util.NoSuchElementException;
import util.SafeList;

public abstract class AbstractPlayer extends Entity{
	private final String name;
	private CharacterClass c;
	private final AbstractActive[] actives;
	private final AbstractPassive[] passives;
    
	private DamageBacklog log;
	private EnergyLog energyLog;
	
	private MeleeActive slash;
	private final SafeList<AbstractStatus> statuses; //change to hashtable
	private int lastHitById; //the useId of the last projectile that hit this player
    
    private Path path;
    
    public static final int RADIUS = 50;
    
	
	public AbstractPlayer(String n){
		super();
        setSpeed(Master.UNITSIZE * 5 / Master.FPS);
		name = n;
		actives = new AbstractActive[3];
		passives = new AbstractPassive[3];
        setRadius(RADIUS);
        path = null;
        statuses = new SafeList<>();
	}
	
	public String getName(){
		return name;
	}
	
	public DamageBacklog getLog(){
		return log;
	}
	public EnergyLog getEnergyLog(){
		return energyLog;
	}
	
	
	// Build stuff
	public void applyBuild(Build b){
		setClass(b.getClassName());
		setActives(b.getActiveNames());
		setPassives(b.getPassiveNames());
		setSpeed((int) (c.getStatValue(CharacterStatName.SPEED) * (500 / Master.FPS)));
    }
	public void setClass(String name){
        try{
            c = CharacterClass.getCharacterClassByName(name.toUpperCase());
        } catch(NoSuchElementException ex){
            ex.printStackTrace();
            c = CharacterClass.getCharacterClassByName("Default");
        }
		c.setUser(this);
	}
	public void setActives(String[] names){
		for(int nameIndex = 0; nameIndex < 3; nameIndex ++){
            try{
                actives[nameIndex] = AbstractActive.getActiveByName(names[nameIndex]);
            } catch(NoSuchElementException ex){
                ex.printStackTrace();
                actives[nameIndex] = AbstractActive.getActiveByName("Default");
            }
			actives[nameIndex].setUser(this);
		}
	}
	public void setPassives(String[] names){
		for(int nameIndex = 0; nameIndex < 3; nameIndex ++){
            try{
                passives[nameIndex] = AbstractPassive.getPassiveByName(names[nameIndex]);
            } catch(NoSuchElementException ex){
                ex.printStackTrace();
                passives[nameIndex] = AbstractPassive.getPassiveByName("Default");
            }
            passives[nameIndex].setUser(this);
		}
	}
	
    public CharacterClass getCharacterClass(){
		return c;
	}
    public AbstractActive[] getActives(){
		return actives;
    }
    
    
    
    public void moveToMouse(){
        setPath(getWorld().getCanvas().getMouseX(), getWorld().getCanvas().getMouseY());
    }
    public void setPath(int x, int y){
        World w = getWorld();
        setPath(w.getMap().findPath(getX(), getY(), x, y));
    }
    public void setPath(Path p){
        path = p;
        if(!path.noneLeft()){
            PathInfo pi = path.get();
            setFocus(pi.getEndX(), pi.getEndY());
        }
    }
    
	public void inflict(AbstractStatus newStat){
		boolean found = false;
		boolean shouldReplace = false;
        
        AbstractStatus[] objStatuses = Arrays
            .stream(statuses.toArray())
            .toArray(size -> new AbstractStatus[size]);
		
        for(AbstractStatus s : objStatuses){
			if(s.getStatusName() == newStat.getStatusName()){
				// already inflicted
				found = true;
				if(s.getIntensityLevel() < newStat.getIntensityLevel()){
					// better level
                    s.terminate();
                    shouldReplace = true;
				} else if(s.getUsesLeft() < newStat.getMaxUses()){
                    s.terminate();
                    shouldReplace = true;
                }
			}
		}
		if(shouldReplace || !found){
			statuses.add(newStat);
            newStat.inflictOn(this);
		}
	}
	
	public double getStatValue(CharacterStatName n){
		return c.getStatValue(n);
	}
	public void useMeleeAttack(){
		if(slash.canUse()){
			slash.use();
		}
	}
	
	public void useAttack(int num){
		if(actives[num].canUse()){
			actives[num].use();
		}
	}
	
	public void logDamage(int dmg){
		log.log(dmg);
	}
	public void setLastHitById(int id){
		lastHitById = id;
	}
	public int getLastHitById(){
		return lastHitById;
	}
    
    public Path getPath(){
        return path;
    }
	
    @Override
	public void init(){
        statuses.clear();
        getActionRegister().reset();
        
		path = null;
        slash = (MeleeActive)AbstractActive.getActiveByName("Slash");
		slash.setUser(this);
		slash.init();
		c.init();
		log = new DamageBacklog(this);
		energyLog = new EnergyLog(this);
		
		for(AbstractActive a : actives){
			a.init();
		}
		for(AbstractPassive p : passives){
			p.init();
		}
		lastHitById = -1;
        
        playerInit();
	}
	
    @Override
	public void update(){
        if(path != null){
            //follow path
            if(path.noneLeft()){
                path = null;
            } else {
                //System.out.println("Refocusing..");
                while(withinFocus() && !path.noneLeft()){
                    path.deque();
                    if(!path.noneLeft()){
                        PathInfo p = path.get();
                        //System.out.println(p);
                        setFocus(p.getEndX(), p.getEndY());
                    }
                }
            }
        }
		slash.update();
		for(AbstractActive a : actives){
			a.update();
		}
		for(AbstractPassive p : passives){
			p.update();
		}
		getActionRegister().triggerOnUpdate();
		log.update();
		energyLog.update();
        
        playerUpdate();
	}
    
    //can get rid of this later
    public void listStatuses(){
        out.println("STATUSES:");
        statuses.forEach((status)->out.println(status.getName()));
        out.println("End of statues");
    }
    
    @Override
    public void terminate(){
        super.terminate();
        getTeam().notifyTerminate(this);
    }
	
    @Override
	public void draw(Graphics g){
        int w = getWorld().getCanvas().getWidth();
		int h = getWorld().getCanvas().getHeight();
		int r = getRadius();
        
        if(path != null){
            path.draw(g);
        }
        
        
		// HP value
		g.setColor(Color.black);
		g.drawString("HP: " + log.getHP(), getX() - w/12, getY() - h/8);
		
		// Update this to sprite later, doesn't scale to prevent hitbox snafoos
		g.setColor(getTeam().getColor());
		g.fillOval(getX() - r, getY() - r, 2 * r, 2 * r);
		g.setColor(c.getColors()[0]);
		g.fillOval(getX() - (int)(r * 0.8), getY() - (int)(r * 0.8), (int)(r * 1.6), (int)(r * 1.6));
		
		g.setColor(Color.black);
		int y = getY() + h/10;
		for(Object obj : statuses.toArray()){
			AbstractStatus s = (AbstractStatus)obj;
            String iStr = "";
			int i = 0;
			while(i < s.getIntensityLevel()){
				iStr += "I";
				i++;
			}
			g.drawString(s.getName() + " " + iStr + "(" + s.getUsesLeft() + ")", getX() - r, y);
			y += h/30;
		}
	}

    public abstract void playerInit();
    public abstract void playerUpdate();
}