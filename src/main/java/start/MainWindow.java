package start;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JPanel;
import util.CombatLog;
import gui.pages.Page;
import gui.pages.PageSwitchListener;
import gui.pages.mainMenu.StartMainMenu;

/**
 * MainWindow acts as the main frame for the game,
 * and serves as the controller for rendering the various
 * pages.
 * 
 * @author Matt Crow
 */
public class MainWindow extends JFrame{
    private final JPanel content;
    private Page currentPage;
    
    private static MainWindow instance = null;
    
    private MainWindow(){
        super();
        
        if(instance != null){
            throw new ExceptionInInitializerError("Can only have 1 instance of MainWindow");
        }
        
        setTitle("The Orpheus Proposition");
        
        content = new JPanel();
        content.setLayout(new GridLayout(1, 1));
        setContentPane(content);
        
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(
            (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom
        );
        setVisible(true);
		addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                //CombatLog.displayLog();
            }
        });
        
        currentPage = null;
        
        switchToPage(new StartMainMenu());
        
        instance = this;
    }
    
    public MainWindow switchToPage(Page p){
        if(p == null){
            throw new NullPointerException();
        }
        content.removeAll();
        content.add(p);
        content.revalidate();
        p.requestFocus();
        content.repaint();
        if(currentPage != null && currentPage instanceof PageSwitchListener){
            ((PageSwitchListener)currentPage).leavingPage(currentPage);
        }
        currentPage = p;
        if(p instanceof PageSwitchListener){
            ((PageSwitchListener)currentPage).switchedToPage(p);
        }
        return this;
    }
    
    public static MainWindow getInstance(){
        if(instance == null){
            instance = new MainWindow();
        }
        return instance;
    }
    
    public static void main(String[] args){
        new MainWindow();
    }
}
