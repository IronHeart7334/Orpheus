package gui;

import javax.swing.JComponent;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import javax.swing.*;
import upgradables.AbstractUpgradable;

@SuppressWarnings("serial")
public class UpgradableSelector<T> extends JComponent{
	private OptionBox<AbstractUpgradable<T>> box;
	private JTextArea desc;
	
	public UpgradableSelector(String title, AbstractUpgradable<T>[] a){
		super();
		setLayout(new GridLayout(2, 1));
		box = new OptionBox<AbstractUpgradable<T>>(title, a);
		box.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				desc.setText(box.getSelected().getDescription());
			}
		});
		add(box);
		
		desc = new JTextArea(box.getSelected().getDescription());
		add(desc);
        Style.applyStyling(desc);
		Style.applyStyling(this);
	}
	public OptionBox<AbstractUpgradable<T>> getBox(){
		return box;
	}
}
