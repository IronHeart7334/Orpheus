package statuses;

import actions.*;
import entities.Player;
import util.Number;
import controllers.Master;
import java.util.function.UnaryOperator;

/**
 * The Rush status increases an Entity's movement speed
 */
public class Rush extends AbstractStatus implements OnUpdateListener{
	private static final UnaryOperator<Integer> CALC = (i)->{return Master.seconds(Number.minMax(1, i, 3) + 2);};
    /**
     * Creates the Rush status.
     * @param lv 1-3. The afflicted Entity will receive a 20% increase in speed per level.
     * @param dur 1-3. Will last for (dur + 2) seconds.
     */
    public Rush(int lv, int dur){
		super(StatusName.RUSH, lv, dur, CALC);
		// 3 - 5 seconds of + 20% to 60% movement
	}
    
    @Override
	public void inflictOn(Player p){
		p.getActionRegister().addOnUpdate(this);
	}
    
    @Override
	public String getDesc(){
		return "Rush, increasing the inflicted's movement speed by " + (20 * getIntensityLevel()) + "% for the next " + Master.framesToSeconds(getMaxUses()) + " seconds";
	}

    @Override
    public AbstractStatus copy() {
        return new Rush(getIntensityLevel(), getBaseParam());
    }

    @Override
    public void trigger(OnUpdateEvent e) {
        e.getUpdated().applySpeedFilter(1 + 0.2 * getIntensityLevel());
    }
}
