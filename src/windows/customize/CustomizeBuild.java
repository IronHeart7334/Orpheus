package windows.customize;

import customizables.Build;
import customizables.actives.AbstractActive;
import customizables.characterClass.CharacterClass;
import customizables.passives.AbstractPassive;
import gui.CustomizableSelector;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import windows.Page;
import windows.SubPage;

/**
 *
 * @author Matt
 */
public class CustomizeBuild extends SubPage{
    private final JTextArea name;
    private final CustomizableSelector charClassSel;
    private final CustomizableSelector[] acts;
    private final CustomizableSelector[] pass;
    
    public CustomizeBuild(Page p){
        super(p);
        setLayout(new GridLayout(3, 3));
        
        JPanel nameArea = new JPanel();
        name = new JTextArea("Build name");
        name.setEditable(true);
        nameArea.add(name);
        add(nameArea);
        
        charClassSel = new CustomizableSelector("Character Class", new CharacterClass[]{});
        add(charClassSel);
        
        JButton save = new JButton("Save changes");
        save.addActionListener((e)->{
            
        });
        add(save);
        
        acts = new CustomizableSelector[3];
        for(int i = 0; i < 3; i++){
            acts[i] = new CustomizableSelector("Active #" + (i + 1), new AbstractActive[]{});
            add(acts[i]);
        }
        
        pass = new CustomizableSelector[3];
        for(int i = 0; i < 3; i++){
            pass[i] = new CustomizableSelector("Passive #" + (i + 1), new AbstractPassive[]{});
            add(pass[i]);
        }
    }

    public void setCustomizing(Build selectedBuild) {
         
    }
    
    @Override
    public void switchedToThis(){
        super.switchedToThis();
        charClassSel.setOptions(CharacterClass.getAll());
        for(int i = 0; i < 3; i++){
            acts[i].setOptions(AbstractActive.getAll());
            pass[i].setOptions(AbstractPassive.getAll());
        }
    }
}
