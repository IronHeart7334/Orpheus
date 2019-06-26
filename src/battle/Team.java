package battle;

import controllers.Master;
import java.awt.Color;
import java.util.ArrayList;
import entities.Player;
import util.Random;
import util.Coordinates;
import customizables.Build;
import entities.EntityManager;
import java.io.Serializable;
import java.util.function.Consumer;
import static java.lang.System.out;
import java.util.HashMap;

/**
 * The Team class is used to keep track of
 * which Players are allies, and which are enemies.
 * It keeps track of which team members are still in the game,
 * and reports itself as defeated once it has no members remaining.
 * 
 * @author Matt Crow
 */
public class Team extends EntityManager implements Serializable{
	private final String name;
	private final Color color;
	private Team enemyTeam;
	private boolean defeated;
	private final int id;
	private static int nextId = 0;
    
    private final HashMap<Integer, Player> roster;
    private final ArrayList<Player> membersRem;
	
	public Team(String n, Color c){
		super();
        name = n;
		color = c;
		id = nextId;
		nextId++;
        roster = new HashMap<>();
        membersRem = new ArrayList<>();
	}
    
	public int getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	
	public Color getColor(){
		return color;
	}
	
	public void addMember(Player m){
        roster.put(m.id, m);
        add(m);
        m.setTeam(this);
	}
    
    /**
     * Returns the Player on this team
     * with the given ID, if one exists.
     * Note that this includes all members
     * in the roster, not just players that
     * are still in play.
     * 
     * @param id the ID of the member to search for
     * @return the player on this team with the given id, or null if one doesn't exist
     */
    public Player getMemberById(int id){
        return roster.get(id);
    }
    
	public static Team constructRandomTeam(String name, Color color, int size){
		Team t = new Team(name, color);
		for(int teamSize = 0; teamSize < size; teamSize++){
			Player p = new Player(name + " member #" + (teamSize + 1));
			p.applyBuild(Build.getAllBuilds().get(Random.choose(0, Build.getAllBuilds().size() - 1)));
			
			t.addMember(p);
		}
		return t;
	}
    
    //OK to have different params, as this does not extend entity
	public void init(int y, int spacing, int facing){
		int x = spacing;
        membersRem.clear();
		for(Player p : roster.values()){
			p.initPos(x, y, facing);
            p.doInit();
            membersRem.add(p);
			x += spacing;
		}
		defeated = false;
	}
	public void setEnemy(Team t){
		enemyTeam = t;
	}
	public Team getEnemy(){
		return enemyTeam;
	}
	
	public boolean isDefeated(){
		return defeated;
	}
	
    public Player nearestPlayerTo(int x, int y){
		if(membersRem.isEmpty()){
			throw new IndexOutOfBoundsException("No players exist for team " + name);
		}
        Player ret = membersRem.get(0);
		int distance = (int)Coordinates.distanceBetween(ret.getX(), ret.getY(), x, y);
		for(Player p : membersRem){
            if(p.getShouldTerminate()){
                continue;
            }
			int check = (int)Coordinates.distanceBetween(p.getX(), p.getY(), x, y);
			if(check < distance){
				ret = p;
				distance = check;
			}
		}
		return ret;
	}
	public final ArrayList<Player> getMembersRem(){
		return membersRem;
	}
    
    public int getRosterSize(){
        return roster.size();
    }
    
    /**
     * Invokes a method on each member in the team
     * note that this is EVERY member,
     * NOT just members remaining
     * @param f 
     */
    public final void forEachMember(Consumer<Player> f){
        roster.values().stream().forEach((Player p)->f.accept(p));
    }
    
    /**
     * Notifies this that a member is out of the game.
     * Updates the list of members remaining accordingly.
     * Note that this does not affect iteration,
     * just anything that references membersRem
     * @param p the Player who was removed from the game.
     */
    public void notifyTerminate(Player p){
        membersRem.remove(p);
        defeated = membersRem.isEmpty();
    }
    
    public void displayData(){
        out.println("TEAM: " + name + "(ID: " + id + ")");
        out.println("Roster: ");
        roster.values().stream().forEach((Player p)->{
            if(p.equals(Master.getUser().getPlayer())){
                out.print("*");
            }
            out.println(p.getName() + "(ID: " + p.id + ")");
        });
    }
    
    @Override
    public boolean equals(Object obj){
        return obj != null && obj instanceof Team && ((Team)obj).getId() == id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.id;
        return hash;
    }
}
