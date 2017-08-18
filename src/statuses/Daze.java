package statuses;

import entities.Player;
import resources.OnHitAction;
import resources.Random;

public class Daze extends Status{
	public Daze(int lv, int dur){
		super("Daze", lv, dur);
	}
	public void inflictOn(Player p){
		OnHitAction a = new OnHitAction(){
			public void f(){
				p.setDirNum((double) Random.choose(0, 19) / 10);
				use();
			}
		};
		p.addOnBeHit(a);
	}
}
