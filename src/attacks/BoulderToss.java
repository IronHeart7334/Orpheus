package attacks;

import statuses.Stun;

public class BoulderToss extends ElementalAttack{
	public BoulderToss(){
		super("Boulder Toss", 20, 100, 300, 3, 100, 1, 1, 250);
		addStatus(new Stun(3, 60), 70);
	}
}
