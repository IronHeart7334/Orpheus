package entities;

import battle.EnergyLog;
import controllers.Master;
import customizables.Build;
import customizables.DataSet;
import customizables.actives.AbstractActive;
import customizables.characterClass.CharacterClass;
import customizables.characterClass.CharacterStatName;
import customizables.passives.AbstractPassive;
import graphics.CustomColors;
import java.awt.Color;
import java.awt.Graphics;
import java.util.NoSuchElementException;
import windows.world.WorldCanvas;

/**
 *
 * @author Matt
 */
public class HumanPlayer extends AbstractPlayer{
    private CharacterClass c;
    private final AbstractActive[] actives;
	private final AbstractPassive[] passives;
    private final EnergyLog energyLog;
    private boolean followingMouse;
    public static final int MIN_LIFE_SPAN = 10;
    
    public HumanPlayer(String n) {
        super(n, MIN_LIFE_SPAN);
        c = null;
        actives = new AbstractActive[3];
		passives = new AbstractPassive[3];
        setClass("Default");
        energyLog = new EnergyLog(this);
        
        followingMouse = false;
    }
    
    // Build stuff
	public void applyBuild(Build b){
		setClass(b.getClassName());
		setActives(b.getActiveNames());
		setPassives(b.getPassiveNames());
		setSpeed((int) (c.getSpeed() * (500.0 / Master.FPS)));
    }
    public void setClass(String name){
        DataSet ds = Master.getDataSet();
        try{
            c = ds.getCharacterClassByName(name.toUpperCase());
        } catch(NoSuchElementException ex){
            ex.printStackTrace();
            c = ds.getDefaultCharacterClass();
        }
        setColor(c.getColors()[0]);
        c.setUser(this);
	}
    public CharacterClass getCharacterClass(){
		return c;
	}
    
    public void setActives(String[] names){
		DataSet ds = Master.getDataSet();
        for(int nameIndex = 0; nameIndex < 3; nameIndex ++){
            try{
                actives[nameIndex] = ds.getActiveByName(names[nameIndex]);
            } catch(NoSuchElementException ex){
                ex.printStackTrace();
                actives[nameIndex] = ds.getDefaultActive();
            }
			actives[nameIndex].setUser(this);
		}
	}
    public AbstractActive[] getActives(){
		return actives;
    }
    
    public void setPassives(String[] names){
        DataSet ds = Master.getDataSet();
		for(int nameIndex = 0; nameIndex < 3; nameIndex ++){
            try{
                passives[nameIndex] = ds.getPassiveByName(names[nameIndex]);
            } catch(NoSuchElementException ex){
                ex.printStackTrace();
                passives[nameIndex] = ds.getDefaultPassive();
            }
            passives[nameIndex].setUser(this);
		}
	}
    
    public EnergyLog getEnergyLog(){
		return energyLog;
	}
    
    public void useAttack(int num){
		if(actives[num].canUse()){
			actives[num].trigger();
		}
	}
    
    public final void setFollowingMouse(boolean b){
        followingMouse = b;
    }
    public final boolean getFollowingMouse(){
        return followingMouse;
    }
    public void moveToMouse(){
        setPath(getWorld().getCanvas().getMouseX(), getWorld().getCanvas().getMouseY());
    }

    @Override
    public void playerInit() {
        c.doInit();
        for(AbstractActive a : actives){
			a.doInit();
		}
        for(AbstractPassive p : passives){
			p.doInit();
		}
        energyLog.init();
    }

    @Override
    public void playerUpdate() {
        for(AbstractActive a : actives){
			a.doUpdate();
		}
        for(AbstractPassive p : passives){
			p.doUpdate();
		}
        energyLog.update();
    }
    
    public void drawHUD(Graphics g, WorldCanvas wc){
        int w = wc.getWidth();
		int h = wc.getHeight();
		
		// compass
		int compassX = w / 10 * 9; // center points
		int compassY = h / 10 * 3;
		int compassDiameter = w / 10;
		
		g.setColor(CustomColors.darkGrey);
		g.fillOval(compassX - compassDiameter, compassY - compassDiameter, compassDiameter * 2, compassDiameter * 2); // draws from upper-left corner, not center
		g.setColor(CustomColors.red);
		g.drawLine(compassX, compassY, (int)(compassX + getDir().getXMod() * compassDiameter), (int)(compassY + getDir().getYMod() * compassDiameter));
		
		
		int guiY = (int)(h * 0.9);
		int sw = w / 5;
		int sh = h / 10;
		
		// HP
		String strHP = getLog().getHP() + "";
		g.setColor(Color.red);
		g.fillRect(0, guiY, sw, sh);
		g.setColor(Color.black);
		g.drawString("HP: " + strHP, (int)(w * 0.1), (int) (h * 0.93));
		
		// Energy
		String strEn = getEnergyLog().getEnergy() + "";
		g.setColor(Color.yellow);
		g.fillRect((int)(w * 0.8), guiY, sw, sw);
		g.setColor(Color.black);
		g.drawString("Energy: " + strEn, (int)(w * 0.9), (int) (h * 0.93));
		
		// Actives
		int i = sw;
		for(AbstractActive a : getActives()){
			a.drawStatusPane(g, i, (int)(h * 0.9), sw, sh);
			i += sw;
		}
	}

    @Override
    public double getStatValue(CharacterStatName n) {
        double ret = 0.0;
        switch(n){
            case HP:
                ret = c.getMaxHP();
                break;
            case ENERGY:
                ret = c.getMaxEnergy();
                break;
            case DMG:
                ret = c.getOffMult();
                break;
            case REDUCTION:
                ret = c.getDefMult();
                break;
            case SPEED:
                ret = c.getSpeed();
                break;
        }
        return ret;
    }
}
