package initializers;
import windows.StartWindow;
import resources.Player;

public class Run {
	public static Player player;
	public static void main(String[] args) {
		new StartWindow();
		player = new Player();
	}
}
