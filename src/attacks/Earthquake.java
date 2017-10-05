package attacks;

import entities.ParticleType;
import statuses.Stun;

public class Earthquake extends ElementalAttack{
	public Earthquake(){
		super("Earthquake", 10, 60, 0, 5, 500, 1, 1, 200);
		addStatus(new Stun(3, 30), 100);
		setParticleType(ParticleType.BURST);
	}
}
