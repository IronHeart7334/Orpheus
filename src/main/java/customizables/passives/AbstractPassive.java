package customizables.passives;

import java.util.*;

import java.io.Serializable;
import java.util.NoSuchElementException;
import statuses.*;
import customizables.AbstractCustomizable;
import customizables.CustomizableType;

public abstract class AbstractPassive extends AbstractCustomizable implements Serializable{
	/**
	 * Passives are abilities that have specific triggers, 
	 * i.e., the user does not directly trigger them:
	 * they are triggered passively
	 */
	private final PassiveType type; // used when upcasting
	private final boolean targetsUser;
	
	private static final HashMap<String, AbstractPassive> ALL = new HashMap<>();
	static{
        ThresholdPassive def = new ThresholdPassive("Default", 2);
        def.addStatus(new Resistance(2, 2));
        addPassive(def);
    }
    
	public AbstractPassive(PassiveType t, String n, boolean b){
		super(CustomizableType.PASSIVE, n);
		type = t;
		targetsUser = b;
	}
    
    public static void loadAll(){
        // on be hit
		OnBeHitPassive r = new OnBeHitPassive("Recover", true);
		r.addStatus(new Regeneration(1, 1));
		OnBeHitPassive t = new OnBeHitPassive("Toughness", true);
		t.addStatus(new Resistance(1, 1));
        OnBeHitPassive cu = new OnBeHitPassive("Cursed", false);
        cu.addStatus(new Stun(3, 3));
		
        // on hit
        OnHitPassive rc = new OnHitPassive("Recharge", true);
        rc.addStatus(new Charge(1, 1));
        OnHitPassive ch = new OnHitPassive("Crippling Hits", false);
        ch.addStatus(new Stun(1, 1));
        
        // on melee hit
        OnMeleeHitPassive lh = new OnMeleeHitPassive("Leechhealer", true);
		lh.addStatus(new Regeneration(1, 1));
		OnMeleeHitPassive m = new OnMeleeHitPassive("Momentum", true);
		m.addStatus(new Rush(1, 1));
		OnMeleeHitPassive s = new OnMeleeHitPassive("Sharpen", true);
		s.addStatus(new Strength(1, 1));
		OnMeleeHitPassive ss = new OnMeleeHitPassive("Sparking Strikes", true);
		ss.addStatus(new Charge(1, 1));
        OnMeleeHitPassive cg = new OnMeleeHitPassive("Crushing Grip", false);
        cg.addStatus(new Stun(2, 1));
        
		//threshold
		ThresholdPassive a = new ThresholdPassive("Adrenaline", 3);
		a.addStatus(new Charge(2, 1));
		ThresholdPassive b = new ThresholdPassive("Bracing", 1);
		b.addStatus(new Resistance(2, 1));
		ThresholdPassive d = new ThresholdPassive("Determination", 2);
		d.addStatus(new Strength(1, 1));
		d.addStatus(new Resistance(1, 1));
		ThresholdPassive e = new ThresholdPassive("Escapist", 2);
		e.addStatus(new Rush(2, 1));
		ThresholdPassive re = new ThresholdPassive("Retaliation", 2);
		re.addStatus(new Strength(2, 1));
        
        
        
		addPassives(new AbstractPassive[]{
				lh,
				m,
				s,
				ss,
				r,
				t,
				a,
				b,
				d,
				e,
				re,
                rc,
                cu,
                ch,
                cg
		});
	}
    
    @Override
	public abstract AbstractPassive copy();
	
	// static methods
	public static void addPassive(AbstractPassive p){
		ALL.put(p.getName().toUpperCase(), p);
	}
	public static void addPassives(AbstractPassive[] ps){
		for(AbstractPassive p : ps){
			addPassive(p);
		}
	}
	public static AbstractPassive getPassiveByName(String n){
        if(!ALL.containsKey(n.toUpperCase())){
            throw new NoSuchElementException("Passive with name " + n + " not found. Did you remember to call AbstractPassive.addPassive(...)?");
        }
		return ALL.get(n.toUpperCase()).copy();
	}
	public static AbstractPassive[] getAll(){
		AbstractPassive[] ret = new AbstractPassive[ALL.size()];
		Collection<AbstractPassive> values = ALL.values();
		int i = 0;
		for(AbstractPassive ap : values){
			ret[i] = ap;
			i++;
		}
		return ret;
	}
	public static String[] getAllNames(){
		String[] ret = new String[ALL.size()];
		Set<String> keys = ALL.keySet();
		int i = 0;
		for(String key : keys){
			ret[i] = key;
			i++;
		}
		return ret;
	}
	
	
	// setters / getters
	public PassiveType getPassiveType(){
		return type;
	}
	public boolean getTargetsUser(){
		return targetsUser;
	}
}
