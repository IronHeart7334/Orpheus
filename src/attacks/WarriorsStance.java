package attacks;

import statuses.Strength;
import statuses.Resistance;

public class WarriorsStance extends BoostAttack{
	public WarriorsStance(){
		super("Warrior's Stance", 10, 60, new Strength(1, 5));
		addStatus(new Resistance(1, 5));
	}
}