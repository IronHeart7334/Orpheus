package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Particle extends Entity{
	private final Color color;
	private final int size;
	private final int lifeSpan;
	private int age;
	
	public Particle(int momentum, Color c){
		super(momentum);
		setMoving(true);
		color = c;
		size = 10;
		lifeSpan = 15;
		age = 0;
	}
    
    @Override
	public void draw(Graphics g){
		g.setColor(color);
		g.fillRect(getX(), getY(), size, size);
	}
    
	public void update(){
		age++;
		if(age >= lifeSpan){
			terminate();
		}
	}
}