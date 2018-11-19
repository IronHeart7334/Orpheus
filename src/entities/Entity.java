package entities;

import initializers.Master;
import java.awt.Graphics;
import resources.Direction;
import battle.Team;
import actions.ActionRegister;
import ai.AI;

public abstract class Entity {
	/**
	 * The Entity class is used as the base for anything that has to interact with players in game
	 */
	
	/*
	 * Position fields
	 * 
	 * move to a movement manager class later?
	 */
	private int x;
	private int y;
	private Direction dir; // the direction the entity is facing, ranging from 0-359 degrees, with 0 being the positive x axis, turning counterclockwise
	private int maxSpeed;
	private boolean moving;
	private double speedFilter; // amount the entity's speed is multiplied by when moving. May depreciate later
	
	/*
	 * Knockback related stuff, working much like regular movement:
	 * dir -> kbDir
	 * maxSpeed -> kbVelocity 
	 * 
	 * however, kbDur is the duration (in frames) that the knockback lasts
	 * Depreciate later if too annoying
	 */
	private Direction kbDir;
	private double kbDur;
	private int kbVelocity;
	
	/*
	 * (focusX, focusY) is a point that the entity is trying to reach
	 */
	private int focusX;
	private int focusY;
	private boolean hasFocus;
	
	/*
	 * In-battle stuff
	 * 
	 * shouldTeminate is set to true once the entity should be deleted
	 * 
	 * actReg is used to store actions (onBeHit, onUpdate, etc.) see the actions package for more details
	 * entityAI is the AI that runs this. All entities have this AI, but it must be manually enabled
	 */
	private Team team;
	private boolean shouldTerminate;
	private ActionRegister actReg;
	private AI entityAI;
	
	// misc
	private final int id;
	private static int nextId = 0;
	
	//constructors
	
	// depreciate later
	public Entity(int m){
		maxSpeed = m;
		
		id = nextId;
		nextId++;
	}
	
	// movement functions
	public final int getX(){
		return x;
	}
	public final int getY(){
		return y;
	}
	public final Direction getDir(){
		return dir;
	}
	public final void setSpeed(int speed){
		maxSpeed = speed;
	}
	public final void applySpeedFilter(double f){
		speedFilter *= f;
	}
	public final void setMoving(boolean isMoving){
		moving = isMoving;
	}
	public final boolean getIsMoving(){
		return moving;
	}
	public final int getMomentum(){
		return (int)(maxSpeed * speedFilter);
	}
	
	public final void turnTo(int xCoord, int yCoord){
		dir = Direction.getDegreeByLengths(x, y, xCoord, yCoord);
	}
	
	private void move(){
		x += dir.getVector()[0] * getMomentum();
		y += dir.getVector()[1] * getMomentum();
	}
	public final void applyKnockback(Direction d, int dur, int vel){
		kbDir = d;
		kbDur = dur;
		kbVelocity = vel;
	}
	private void updateMovement(){
		if(hasFocus){
			if(withinFocus()){
				hasFocus = false;
				setMoving(false);
			}else{
				turnToFocus();
				setMoving(true);
			}
		}
		
		if(kbDur > 0){
			x += kbDir.getVector()[0] * kbVelocity;
			y += kbDir.getVector()[1] * kbVelocity;
			kbDur -= 1;
		} else {
			applyKnockback(new Direction(0), 0, 0);
		}
		
		if(moving){
			move();
		}
		
		// keep entity on the battlefield
		if(x < 0){
			x = 0;
		} else if(x > Master.getCurrentBattle().getHost().getWidth()){
			x = Master.getCurrentBattle().getHost().getWidth();
		}
		if(y < 0){
			y = 0;
		} else if(y > Master.getCurrentBattle().getHost().getHeight()){
			y = Master.getCurrentBattle().getHost().getHeight();
		}
		
		speedFilter = 1.0;
	}
	
	
	
	//focus related methods
	public final void setFocus(int xCoord, int yCoord){
		focusX = xCoord;
		focusY = yCoord;
		hasFocus = true;
	}
	public final void setFocus(Entity e){
		setFocus(e.getX(), e.getY());
	}
	public final void turnToFocus(){
		turnTo(focusX, focusY);
	}
    
    //add different versions in subclasses?
	public boolean withinFocus(){
		// returns if has reached focal point
		boolean withinX = Math.abs(getX() - focusX) < maxSpeed;
		boolean withinY = Math.abs(getY() - focusY) < maxSpeed;
		return withinX && withinY;
	}
	
	
	
	// inbattle methods
	public final void setTeam(Team t){
		team = t;
	}
	public final Team getTeam(){
		return team;
	}
	public final ActionRegister getActionRegister(){
		return actReg;
	}
	public boolean checkForCollisions(Entity e){
		return x + 50 >= e.getX() - 50 
				&& x - 50 <= e.getX() + 50
				&& y + 50 >= e.getY() - 50 
				&& y - 50 <= e.getY() + 50;
	}
	public final AI getEntityAI(){
		return entityAI;
	}
    
    //can't be final, as SeedProjectile needs to override
	public void terminate(){
		shouldTerminate = true;
	}
	
	public final boolean getShouldTerminate(){
		return shouldTerminate;
	}
	
	// misc. methods
	public final int getId(){
		return id;
	}
    public void init(int xCoord, int yCoord, int degrees){
		// called by battle
		x = xCoord;
		y = yCoord;
		dir = new Direction(degrees);
		
		moving = false;
		speedFilter = 1.0;
		kbDir = new Direction(0);
		kbDur = 0;
		kbVelocity = 0;
		actReg = new ActionRegister(this);
		shouldTerminate = false;
		entityAI = new AI(this);
		
		hasFocus = false;
	}
	public final void doUpdate(){
		if(!shouldTerminate){
			entityAI.update();
			updateMovement();
			actReg.tripOnUpdate();
            update();
		}
	}
    
    public abstract void update();
	public abstract void draw(Graphics g);
}