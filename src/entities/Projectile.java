package entities;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import attacks.Attack;
import attacks.MeleeAttack;
import initializers.Master;
import resources.CombatLog;
import resources.Op;
import resources.Random;

public class Projectile extends Entity{
	private Player user;
	private Attack registeredAttack;
	private int distanceTraveled;
	private ArrayList<Player> doNotHit;
	private Player hit;
	private boolean shouldTerminate;
	private boolean terminated;
	
	// linked list stuff
	private Projectile parent;
	private Projectile child;
	private boolean hasParent;
	private boolean hasChild;
	
	public Projectile(int x, int y, int dirNum, int momentum, Player attackUser, Attack a){
		super(x, y, dirNum, momentum);
		distanceTraveled = 0;
		user = attackUser;
		setTeam(user.getTeam());
		registeredAttack = a;
		if(a.getTracking()){
			getEntityAI().enable();
		}
		shouldTerminate = false;
		terminated = false;
		setMoving(true);
		doNotHit = new ArrayList<Player>();
		hit = new Player("Void");
		
		hasParent = false;
		hasChild = false;
		user.getTeam().registerProjectile(this);
	}
	public String getAttackName(){
		return registeredAttack.getName();
	}
	public Player getUser(){
		return user;
	}
	public Player getHit(){
		return hit;
	}
	public int getDistance(){
		return distanceTraveled;
	}
	public Attack getAttack(){
		return registeredAttack;
	}
	public boolean getShouldTerminate(){
		return shouldTerminate;
	}
	public boolean hasAlreadyTerminated(){
		return terminated;
	}
	
	public void setParent(Projectile p){
		parent = p;
		hasParent = true;
	}
	public void setChild(Projectile p){
		child = p;
		hasChild = true;
	}
	public boolean getHasParent(){
		return hasParent;
	}
	public boolean getHasChild(){
		return hasChild;
	}
	public void disableParent(){
		hasParent = false;
	}
	public void disableChild(){
		hasChild = false;
	}
	public Projectile getParent(){
		return parent;
	}
	public Projectile getChild(){
		return child;
	}
	
	public void avoid(Player p){
		doNotHit.add(p);
	}
	public boolean checkIfWithin(int leftX, int rightX, int topY, int bottomY){
		return getX() >= leftX && getX() <= rightX && getY() >= topY && getY() <= bottomY;
	}
	public void checkForCollisionsWith(Player p){
		if(checkIfWithin(p.getX() - 50, p.getX() + 50, p.getY() - 50, p.getY() + 50)){
			if(doNotHit.contains(p)){
				return;
			}
			hit = p;
			p.logDamage((int) (registeredAttack.getStatValue("Damage") * user.getStatValue("damage dealt modifier") * p.getStatValue("damage taken modifier")));
			
			if(registeredAttack instanceof MeleeAttack){
				user.getActionRegister().tripOnMeleeHit(p);
				user.getEnergyLog().gainEnergy(5);
				p.getActionRegister().tripOnBeMeleeHit(user);
			} else {
				user.getActionRegister().tripOnHit(p);
				p.getActionRegister().tripOnBeHit(user);
			}
			getActionRegister().tripOnHit(p);
			CombatLog.logProjectileData(this);
			terminate();
		}
	}
	public void terminate(){
		shouldTerminate = true;
		if(hasParent && hasChild){
			parent.setChild(child);
			child.setParent(parent);
		} else {
			child.disableParent();
			parent.disableChild();
		}
		terminated = true;
	}
	public void update(){
		super.update();
		
		distanceTraveled += getMomentum();
		ArrayList<Color> cs = registeredAttack.getColors();
		if(!Master.DISABLEPARTICLES){
			switch(registeredAttack.getParticleType()){
			case BURST:
				for(int i = 0; i < Master.TICKSTOROTATE; i++){
					Color rbu = cs.get(Random.choose(0, cs.size() - 1));
					new Particle(getX(), getY(), 360 * i / Master.TICKSTOROTATE, 5, rbu);
				}
				break;
			case SHEAR:
				Color rs = cs.get(Random.choose(0, cs.size() - 1));
				new Particle(getX(), getY(), getDir().getDegrees() - 45, 5, rs);
				rs = cs.get(Random.choose(0, cs.size() - 1));
				new Particle(getX(), getY(), getDir().getDegrees() + 45, 5, rs);
				break;
			case BEAM:
				Color rbe = cs.get(Random.choose(0, cs.size() - 1));
				new Particle(getX(), getY(), getDir().getDegrees() - 180, 5, rbe);
				break;
			case BLADE:
				Color rbl = cs.get(Random.choose(0, cs.size() - 1));
				new Particle(getX(), getY(), getDir().getDegrees(), 0, rbl);
				break;
			default:
				Op.add("The particle type of " + registeredAttack.getParticleType());
				Op.add("is not found for Projectile.java");
				Op.dp();
			}
		}
	}
	public void draw(Graphics g){
		if(registeredAttack.getParticleType() == ParticleType.NONE || Master.DISABLEPARTICLES){
			g.setColor(user.getTeam().getColor());
			g.fillOval(getX() - 25, getY() - 25, 50, 50);
		}
	}
}
