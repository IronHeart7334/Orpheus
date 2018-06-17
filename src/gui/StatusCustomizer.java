package gui;

import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import statuses.Status;
import statuses.StatusName;
import upgradables.AbstractUpgradable;

//TODO: make this take only upgradable as param so that it can do chance
@SuppressWarnings("serial")
public class StatusCustomizer extends JComponent{
	
	private AbstractUpgradable statusOwner;
	private OptionBox<StatusName> box;
	private OptionBox<Integer> intensity;
	private OptionBox<Integer> duration;
	//private OptionBox<Integer> chance;
	
	public StatusCustomizer(AbstractUpgradable a, Status s){
		super();
		statusOwner = a;
		
		box = new OptionBox<>("Status", StatusName.values());
		box.setSelected(StatusName.valueOf(s.getName().toUpperCase()));
		add(box);
		
		Integer[] v1 = {1, 2, 3};
		intensity = new OptionBox<>("Intensity", v1);
		intensity.setSelected((Integer)s.getBaseLevel());
		add(intensity);
		
		Integer[] v2 = {1, 2, 3};
		duration = new OptionBox<>("Duration", v2);
		duration.setSelected((Integer)s.getBaseUses());
		add(duration);
		
		//Integer[] v3 = {1, 2, 3};
		//chance = new OptionBox<Integer>("Chance", v3);
		//chance.setSelected(?);
		//add(chance);
		
		setLayout(new GridLayout(3, 1));
		
		Style.applyStyling(this);
	}
	
	public void saveStatus(){
		statusOwner.addStatus(box.getSelected(), intensity.getSelected(), duration.getSelected(), 100);
	}
	public void addActionListener(AbstractAction a){
		box.addActionListener(a);
		intensity.addActionListener(a);
		duration.addActionListener(a);
		//chance.addActionListener(a);
	}
}
