package menus;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;

public class Menu {
	private String text;
	private int x;
	private int y;
	private int maxX;
	private int maxY;
	private int width;
	private int height;
	private boolean clicked;
	
	public Menu(String text, int x, int y, int w, int h){
		this.text = text;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		clicked = false;
	}
	public void setMaxX(int x){
		maxX = x;
	}
	public void setMaxY(int y){
		maxY = y;
	}
	
	public void draw(Graphics g){
		JButton button = new JButton(text);
		button.setLayout(null);
		button.setOpaque(true);
		button.setBorderPainted(false);
		button.setBounds(x, y, 200, 200);
		button.setBackground(Color.gray);
		add(button);
	}
}
