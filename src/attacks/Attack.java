package attacks;
import java.util.ArrayList;

import entities.*;
import upgradables.Stat;
import resources.Op;

public class Attack {
	private static ArrayList<Attack> attackList = new ArrayList<>();
	private String name;
	private ArrayList<Stat> stats;
	private int cooldown;
	private Projectile registeredProjectile;
	private String type;
	
	public Attack(String n, int energyCost, int cooldown, int range, int speed, int aoe, int areaScale, int distanceScale, int dmg){
		name = n;
		stats = new ArrayList<>();
		stats.add(new Stat("Energy Cost", energyCost, 2));
		stats.add(new Stat("Cooldown", cooldown));
		stats.add(new Stat("Range", range));
		stats.add(new Stat("Speed", speed));
		stats.add(new Stat("AOE", aoe));
		stats.add(new Stat("Area Scale", areaScale));
		stats.add(new Stat("Distance Scale", distanceScale));
		stats.add(new Stat("Damage", dmg));
		
		attackList.add(this);
	}
	public static Attack getAttackByName(String name){
		for(Attack a : attackList){
			if(a.getName() == name){
				return a;
			}
		}
		return new Slash();
	}
	public void setType(String t){
		type = t;
	}
	public String getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	public Stat getStat(String n){
		for(Stat stat : stats){
			if(stat.name == n){
				return stat;
			}
		}
		Op.add("The stat by the name of " + n + " is not found for Attack " + name);
		Op.dp();
		return new Stat("STATNOTFOUND", 0);
	}
	public double getStatValue(String n){
		return getStat(n).get();
	}
	public Projectile getRegisteredProjectile(){
		return registeredProjectile;
	}
	public boolean onCooldown(){
		return cooldown > 0;
	}
	public boolean canUse(Player user){
		return user.getEnergy() >= getStat("Energy Cost").get() && !onCooldown();
	}
	public void use(Player user){
		registeredProjectile = new SeedProjectile(user.getX(), user.getY(), user.getDirNum(), (int) getStatValue("Speed"), user, this);
		if(registeredProjectile.getAttack().getStatValue("Range") == 0){
			registeredProjectile.terminate();
		}
		cooldown = (int) getStatValue("Cooldown");
		//displayData();
	}
	public void init(){
		cooldown = 0;
	}
	public void update(){
		cooldown -= 1;
		if (cooldown < 0){
			cooldown = 0;
		}
	}
	public void displayData(){
		for(Stat stat : stats){
			Op.add(stat.name + ": " + getStatValue(stat.name));
		}
		Op.dp();
	}
}
