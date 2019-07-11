package windows;

import customizables.passives.AbstractPassive;
import customizables.actives.AbstractActive;
import customizables.CustomizableJsonUtil;
import customizables.AbstractCustomizable;
import customizables.characterClass.CharacterClass;
import javax.swing.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import customizables.CustomizableType;
import gui.*;
import java.io.File;

@SuppressWarnings({"serial", "rawtypes"})

// looks like I'll have to do seperate active and passive customizers
public class CustomizeCanvas extends OldContentPage{
	
	private OptionBox<String> upgradableName;
	private AbstractCustomizable customizing;
	
	// used to choose the type of what to customize
	private JButton act;
	private JButton cha;
	private JButton pas;
	
	private JButton customize;
	
	public CustomizeCanvas(){
		super();
		
		JButton quit = new JButton("Return to build window");
		quit.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				switchTo(new BuildCanvas());
			}
		});
		addMenuItem(quit);
        
        JButton imp = new JButton("Import all customizables from a file");
        imp.addActionListener((ActionEvent e)->{
            File[] chosen = FileChooserUtil.chooseFiles();
            if(chosen != null){
                for(File f : chosen){
                    CustomizableJsonUtil.loadFile(f);
                }
            }
        });
        addMenuItem(imp);
        
        JButton export = new JButton("Export all customizables to a file");
        export.addActionListener((ActionEvent e)->{
            File f = FileChooserUtil.chooseDir();
            if(f != null){
                String exportName = JOptionPane.showInputDialog("Enter a name for this export:");
                File dir = new File(f.getAbsolutePath() + "/" + exportName);
                dir.mkdir();
                
                AbstractActive.saveAllToFile(new File(dir.getAbsolutePath() + "/actives.json"));
                AbstractPassive.saveAll(new File(dir.getAbsolutePath() + "/passives.json"));
                CharacterClass.saveAll(new File(dir.getAbsolutePath() + "/characterClasses.json"));
            }
        });
        addMenuItem(export);
		
		act = new JButton("Active");
		act.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				phase2(CustomizableType.ACTIVE);
			}
		});
		add(act);
		
		cha = new JButton("Character Class");
		cha.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				phase2(CustomizableType.CHARACTER_CLASS);
			}
		});
		add(cha);
		
		pas = new JButton("Passive");
		pas.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				phase2(CustomizableType.PASSIVE);
			}
		});
		add(pas);
		resizeComponents(1, 3);
		resizeMenu(2);
	}
	private void phase2(CustomizableType type){
		removePhase1();
		
		String[] names = new String[]{"An error occurred in CustomizeCanvas.phase2..."};
		customize = new JButton("Customize selected build");
		add(customize);
		
		switch(type){
		case ACTIVE:
			names = AbstractActive.getAllNames();
			customize.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					customizing = AbstractActive.getActiveByName(upgradableName.getSelected());
					add(new ActiveCustomizer((AbstractActive)customizing));
					phase3(CustomizableType.ACTIVE);
				}
			});
			break;
		case PASSIVE:
			names = AbstractPassive.getAllNames();
			customize.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					customizing = AbstractPassive.getPassiveByName(upgradableName.getSelected());
					add(new PassiveCustomizer((AbstractPassive)customizing));
					phase3(CustomizableType.PASSIVE);
				}
			});
			break;
		case CHARACTER_CLASS:
			names = CharacterClass.getAllNames();
			customize.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent e){
					customizing = CharacterClass.getCharacterClassByName(upgradableName.getSelected());
					add(new CharacterClassCustomizer((CharacterClass)customizing));
					phase3(CustomizableType.PASSIVE);
				}
			});
			break;
		}
		
		upgradableName = new OptionBox<>("Select upgradable to customize", names);
		add(upgradableName);
		resizeComponents(2, 1);
		revalidate();
		repaint();
	}
	private void phase3(CustomizableType type){
		removePhase2();
		resizeComponents(1, 2);
	}
	private void removePhase1(){
		remove(act);
		remove(cha);
		remove(pas);
		revalidate();
		repaint();
	}
	private void removePhase2(){
		remove(customize);
		remove(upgradableName);
		revalidate();
		repaint();
	}
}
