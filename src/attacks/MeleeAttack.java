package attacks;

public class MeleeAttack extends Attack{
	public MeleeAttack(String n, int chargeup, int cooldown, double chargeScale, int dmg){
		super(n, 0, chargeup, cooldown, chargeScale, 1, 0, 0, 0, 1, dmg);
	}
}
