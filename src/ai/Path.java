package ai;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * The Path class is used by Player to navigate from some start to an end.
 * Older versions of Orpheus used simple "look at your target and move forward"
 * to reach destinations, but this caused problems with the introduction of tiles:
 * using the old system, Players would bump into stuff in their linear pursuit of enemies.
 * These paths are generated by World.
 * @author Matt Crow
 */
public class Path implements Serializable{
    private final LinkedList<PathInfo> path;
    public Path(){
        path = new LinkedList<>();
    }
    
    public boolean noneLeft(){
        return path.isEmpty();
    }
    
    /**
     * Gets the first element in the path
     * @return 
     */
    public PathInfo get(){
        return path.getFirst();
    }
    
    public void push(PathInfo p){
        path.push(p);
    }
    
    public void deque(){
        path.removeFirst();
    }
    
    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.red);
        g2d.setStroke(new BasicStroke(10));
        for(PathInfo p : path){
            g2d.drawLine(p.getStartX(), p.getStartY(), p.getEndX(), p.getEndY());
        }
        if(!path.isEmpty()){
            g2d.setColor(Color.red);
            g2d.fillOval(path.getLast().getEndX(), path.getLast().getEndY(), 20, 20);
            g2d.setColor(Color.green);
            g2d.fillOval(path.getFirst().getStartX(), path.getFirst().getStartY(), 20, 20);
        }
    }
    
    public void print(){
        System.out.println("Path:");
        path.forEach((PathInfo p)->System.out.println(p));
    }
}
