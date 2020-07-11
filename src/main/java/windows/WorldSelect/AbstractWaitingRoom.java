package windows.WorldSelect;

import controllers.User;
import gui.BuildSelect;
import gui.Chat;
import gui.Style;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import windows.Page;

/**
 *
 * @author Matt
 */
public class AbstractWaitingRoom extends Page{
    private final JTextArea teamList;
    private final Chat chat;
    private final BuildSelect playerBuild;
    
    public AbstractWaitingRoom(){
        super();
        
        addBackButton(new WSMain());
        
        //grid layout was causing problems with chat.
        //since it couldn't fit in 1/4 of the JPanel, it compressed to just a thin line
        setLayout(new BorderLayout());
        
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new GridLayout(1, 3));
        
        teamList = new JTextArea("Player Team:");
        Style.applyStyling(teamList);
        infoSection.add(teamList);
        
        add(infoSection, BorderLayout.PAGE_START);
        
        JPanel center = new JPanel();
        center.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.anchor = GridBagConstraints.LINE_START;
        playerBuild = new BuildSelect();
        center.add(playerBuild, gbc.clone());
        
        gbc.anchor = GridBagConstraints.LINE_END;
        chat = new Chat();
        center.add(chat, gbc.clone());
        
        add(center, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    public final Chat getChat(){
        return chat;
    }
    
    /**
     * Updates the text of this' team displays
     * to match the proto-teams of the backend.
     */
    public void updateTeamDisplays(){
        String newStr = "Player Team: \n";
        newStr = Arrays
            .stream(backend.getTeamProto())
            .map((User use) -> "* " + use.getName() + "\n")
            .reduce(newStr, String::concat);
        teamList.setText(newStr);
    }
}
