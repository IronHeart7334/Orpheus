package gui.pages.worldPlay;

import controls.userControls.AbstractPlayerControls;
import gui.components.Chat;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import gui.pages.Page;
import gui.pages.worldSelect.WSMain;
import world.HostWorld;
import world.RemoteProxyWorld;

/**
 * The WorldPage is used to render WorldCanvases.
 * So yes, it is rather convoluted:
 use the MainWindow to render the WorldPage,
 which renders the WorldPage,
 which renders the WorldCanvas,
 which renders the AbstractWorldShell.
 * 
 * @author Matt Crow
 */
public class WorldPage extends Page{
    private final JPanel canvasArea;
    private WorldCanvas canvas;
    private final Chat chat;
    
    public WorldPage(){
        super();
        addBackButton(new WSMain(), ()->{
            if(canvas != null){
                canvas.stop();
            }
        });
        
        setLayout(new BorderLayout());
        
        canvasArea = new JPanel();
        canvasArea.setBackground(Color.red);
        canvasArea.setFocusable(false);
        canvasArea.setLayout(new GridLayout(1, 1));
        add(canvasArea, BorderLayout.CENTER);
        
        //other area
        //make this collapsable
        JPanel otherArea = new JPanel();
        otherArea.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        chat = new Chat();
        c2.fill = GridBagConstraints.VERTICAL;
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.weightx = 1.0;
        c2.weighty = 1.0;
        otherArea.add(chat, c2.clone());
        
        JTextArea controls = new JTextArea(
            AbstractPlayerControls.getPlayerControlScheme()
        );
        controls.setEditable(false);
        controls.setWrapStyleWord(true);
        controls.setLineWrap(true);
        JScrollPane scrolly = new JScrollPane(controls);
        scrolly.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrolly.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        c2.gridx = GridBagConstraints.RELATIVE;
        c2.fill = GridBagConstraints.BOTH;
        otherArea.add(scrolly, c2.clone());
        
        add(otherArea, BorderLayout.PAGE_END);
    }
    
    public WorldPage setCanvas(WorldCanvas w){
        canvas = w;
        canvasArea.removeAll();
        canvasArea.add(w);
        w.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                w.requestFocusInWindow();
            }
        });
        if(w.getWorldShell() instanceof RemoteProxyWorld || w.getWorldShell() instanceof HostWorld){
            try {
                chat.openChatServer();
            } catch (IOException ex) {
                chat.logLocal("Failed to start chat server");
                ex.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(()->w.requestFocusInWindow());
        chat.logLocal("Currently rendering World " + w.getWorldShell().hashCode());
        chat.logLocal("Rendered on WorldCanvas " + w.hashCode());
        revalidate();
        repaint();
        return this;
    }
}
