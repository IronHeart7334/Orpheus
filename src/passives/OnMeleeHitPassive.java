package passives;

public class OnMeleeHitPassive extends AbstractPassive{
	/*
	 * Same as onHitPassive, though only triggering off
	 * of melee hits
	 */
	public OnMeleeHitPassive(String n, boolean targetsUser){
		super(PassiveType.ONMELEEHIT, n, targetsUser);
	}
	
    @Override
	public OnMeleeHitPassive copy(){
		OnMeleeHitPassive copy = new OnMeleeHitPassive(getName(), getTargetsUser());
		copyInflictTo(copy);
		return copy;
	}
    @Override
	public void update(){
		getRegisteredTo().getActionRegister().addOnMeleeHit(getKey());
	}
    @Override
	public String getDescription(){
		String desc = getName() + ": ";
		desc += "When the user strikes a target with a melee attack, ";
		desc += "inflicts " + ((getTargetsUser()) ? "user" : "target") + " with: ";
		desc += getInflict().getStatusString();
		return desc;
	}
}
