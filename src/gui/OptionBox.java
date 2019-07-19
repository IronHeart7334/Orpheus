package gui;

import javax.swing.*;

import java.awt.GridLayout;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class OptionBox<T> extends JComponent{
	private final ArrayList<T> options;
	private final JLabel title;
	private final JComboBox<String> box;
	
	public OptionBox(String t, T[] opt){
		super();
		Style.applyStyling(this);
		setLayout(new GridLayout(2, 1));
		
		title = new JLabel(t);
        Style.applyStyling(title);
		add(title);
		
		options = new ArrayList<T>();
		for(int i = 0; i < opt.length; i++){
			options.add(opt[i]);
		}
		box = new JComboBox<>(getOptions());
		add(box);
        Style.applyStyling(box);
	}
	public String getTitle(){
		return title.getText();
	}
	public String[] getOptions(){
		String[] ret = new String[options.size()];
		for(int i = 0; i < options.size(); i++){
			ret[i] = options.get(i).toString();
		}
		return ret;
	}
    public void clear(){
        box.removeAllItems();
    }
	public void setSelected(T item){
		boolean found = false;
		for(int i = 0; i < box.getItemCount() && !found; i++){
			if(box.getItemAt(i).equals(item.toString())){
				found = true;
				box.setSelectedIndex(i);
			}
		}
		if(!found){
			System.out.println("Item not found: " + item);
		}
	}
	public void setSelectedIndex(int i){
		box.setSelectedIndex(i);
	}
	public void setSelectedName(String s){
		boolean found = false;
		String[] names = getOptions();
		for(int i = 0; i < names.length && !found; i++){
			if(names[i].equals(s)){
				found = true;
				setSelectedIndex(i);
			}
		}
	}
	public T getSelected(){
		return options.get(box.getSelectedIndex());
	}
	public void addActionListener(AbstractAction a){
		box.addActionListener(a);
	}
    
    @Override
    public void setEnabled(boolean b){
        super.setEnabled(b);
        box.setEnabled(b);
    }
}
