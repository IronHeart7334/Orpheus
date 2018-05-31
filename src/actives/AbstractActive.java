package actives;
import java.util.HashMap;
import java.util.Set;

import java.awt.Color;
import java.awt.Graphics;
import entities.*;
import initializers.Master;
import upgradables.AbstractUpgradable;
import upgradables.Stat;
import resources.Op;
import resources.Number; // use later for minMax?

public abstract class AbstractActive extends AbstractUpgradable{
	/**
	 * Actives are abilities that the user triggers
	 */
	private ParticleType particleType;
	private ActiveType type; // used for upcasting
	
	private static HashMap<String, AbstractActive> allActives = new HashMap<>();
	
	public AbstractActive(ActiveType t, String n, int energyCost, int arcLength, int range, int speed, int aoe, int dmg){
		super(n);
		type = t;
		
		setStat(ActiveStat.COST, energyCost);
		setStat(ActiveStat.ARC, arcLength);
		setStat(ActiveStat.RANGE, range);
		setStat(ActiveStat.SPEED, speed);
		setStat(ActiveStat.AOE, aoe);
		setStat(ActiveStat.DAMAGE, dmg);
		
		particleType = ParticleType.NONE;
		setCooldown(1);
	}
	public AbstractActive copy(){
		// used to allow override
		// DO NOT INVOKE THIS
		return this;
	}
	
	// static methods
	public static void addActive(AbstractActive a){
		allActives.put(a.getName().toUpperCase(), a);
	}
	public static void addActives(AbstractActive[] as){
		for(AbstractActive a : as){
			addActive(a);
		}
	}
	public static AbstractActive getActiveByName(String n){
		AbstractActive ret = allActives.getOrDefault(n.toUpperCase(), allActives.get("SLASH"));
		if(ret.getName().toUpperCase().equals("SLASH") && !n.toUpperCase().equals("SLASH")){
			Op.add("No active was found with name " + n + " in AbstractActive.getActiveByName");
			Op.dp();
		}
		return ret;
	}
	public static String[] getAllNames(){
		String[] ret = new String[allActives.size()];
		Set<String> keys = allActives.keySet();
		int i = 0;
		for(String key : keys){
			ret[i] = key;
			i++;
		}
		return ret;
	}
	
	public void setStat(ActiveStat n, int value){
		// 1-5 stat system
		switch(n){
		case COST:
			// 5-25 to 10-50 cost
			addStat(new Stat("Cost", value * 5, 2));
			setBase("Cost", value);
			break;
		case ARC:
			// 0 - 360 degrees
			/*
			 * 1: 45
			 * 2: 90
			 * 3: 135
			 * 4: 180
			 * 5: 360
			 */
			addStat(new Stat("Arc", (value == 5) ? 360 : 45 * value));
			setBase("Arc", value);
			break;
		case RANGE:
			// 1-15 units of range. Increases exponentially
			int units = 0;
			for(int i = 0; i <= value; i++){
				units += i;
			}
			addStat(new Stat("Range", units * 100));
			setBase("Range", value);
			break;
		case SPEED:
			// 1-5 units per second
			addStat(new Stat("Speed", 100 * value / Master.FPS));
			setBase("Speed", value);
			break;
		case AOE:
			// 1-5 units (or 0)
			addStat(new Stat("AOE", value * 100));
			setBase("AOE", value);
			break;
		case DAMAGE:
			// 50-250 to 250-500 damage (will need to balance later?)
			addStat(new Stat("Damage", value * 50, 2));
			setBase("Damage", value);
			break;
		}
	}
	
	// particle methods
	public void setParticleType(ParticleType t){
		particleType = t;
	}
	public ParticleType getParticleType(){
		return particleType;
	}
	
	// misc
	public ActiveType getType(){
		return type;
	}
	
	// in battle methods
	public boolean canUse(){
		return getRegisteredTo().getEnergyLog().getEnergy() >= getStat("Cost").get() && !onCooldown();
	}
	public void consumeEnergy(){
		getRegisteredTo().getEnergyLog().loseEnergy((int) getStatValue("Cost"));
		setToCooldown();
	}
	public void use(){
		consumeEnergy();
		
		if(type != ActiveType.BOOST){
			spawnArc((int)getStatValue("Arc"));
		}
	}
	
	// spawning
	public void spawnProjectile(int facingDegrees){
		new SeedProjectile(getRegisteredTo().getX(), getRegisteredTo().getY(), facingDegrees, (int) getStatValue("Speed"), getRegisteredTo(), this);
	}
	public void spawnProjectile(){
		spawnProjectile(getRegisteredTo().getDir().getDegrees());
	}
	public void spawnArc(int arcDegrees){
		int start = getRegisteredTo().getDir().getDegrees() - arcDegrees / 2;
		
		// spawn projectiles every 15 degrees
		for(int angleOffset = 0; angleOffset < arcDegrees; angleOffset += 15){
			int angle = start + angleOffset;
			spawnProjectile(angle);
		}
	}
	
	public void drawStatusPane(Graphics g, int x, int y, int w, int h){
		if(!onCooldown()){
			g.setColor(Color.white);
			g.fillRect(x, y, w, h);
			g.setColor(Color.black);
			g.drawString(getName(), x + 10, y + 20);
		} else {
			g.setColor(Color.black);
			g.fillRect(x, y, w, h);
			g.setColor(Color.red);
			g.drawString("On cooldown: " + Master.framesToSeconds(getCooldown()), x + 10, y + 20);
		}	
	}
}