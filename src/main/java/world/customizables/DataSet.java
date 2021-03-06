package world.customizables;

import world.customizables.actives.AbstractActive;
import world.customizables.actives.BoostActive;
import world.customizables.actives.BoulderToss;
import world.customizables.actives.ElementalActive;
import world.customizables.actives.FlameCharge;
import world.customizables.characterClass.CharacterClass;
import world.customizables.passives.AbstractPassive;
import world.customizables.passives.OnBeHitPassive;
import world.customizables.passives.OnHitPassive;
import world.customizables.passives.ThresholdPassive;
import world.entities.ParticleType;
import gui.graphics.CustomColors;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import world.statuses.AbstractStatus;
import world.statuses.Burn;
import world.statuses.Regeneration;
import world.statuses.Resistance;
import world.statuses.Rush;
import world.statuses.Strength;
import world.statuses.Stun;

/**
 * The DataSet class is used to store all the Actives, Passives, CharacterClasses, and Builds.
 * Future versions will add the ability to load additional classes at runtime.
 * 
 * Currently, Master.java contains a static DataSet, which is automatically populated
 * with all the default customizables when that file first loads: I needn't worry about customizables not being loaded.
 * 
 * @author Matt Crow
 */
public final class DataSet {
    public final HashMap<String, AbstractActive> allActives;
    public final HashMap<String, CharacterClass> allCharacterClasses;
    public final HashMap<String, AbstractPassive> allPassives;
    public final HashMap<String, Build> allBuilds;
    
    private final AbstractActive DEFAULT_ACTIVE = new ElementalActive("Default", 3, 3, 3, 3, 3);
    private final CharacterClass DEFAULT_CHARACTER_CLASS = new CharacterClass("Default", CustomColors.rainbow, 3, 3, 3, 3);
    private final AbstractPassive DEFAULT_PASSIVE = new ThresholdPassive("Default", 2);
    private final Build DEFAULT_BUILD = new Build("0x138", "Default", "RAINBOW OF DOOM", "Healing Rain", "Speed Test", "Burning Edge", "Escapist", "Cursed");
    
    public DataSet(){
        allActives = new HashMap<>();
        allCharacterClasses = new HashMap<>();
        allPassives = new HashMap<>();
        allBuilds = new HashMap<>();
        
        DEFAULT_PASSIVE.addStatus(new Resistance(2, 2));
        
        addActive(DEFAULT_ACTIVE);
        addCharacterClass(DEFAULT_CHARACTER_CLASS);
        addPassive(DEFAULT_PASSIVE);
        addBuild(DEFAULT_BUILD);
    }
    
    private void addToMap(AbstractCustomizable c, HashMap<String, ? extends AbstractCustomizable> map){
        if(c == null){
            throw new NullPointerException("Cannot add null to map");
        }
        if(map == null){
            throw new NullPointerException("Cannot add customizable to null map");
        }
        ((HashMap<String, AbstractCustomizable>)map).put(c.getName().toUpperCase(), c.copy());
    }
    public void addActive(AbstractActive a){
        addToMap(a, allActives);
    }
    public void addCharacterClass(CharacterClass c){
        addToMap(c, allCharacterClasses);
    }
    public void addPassive(AbstractPassive p){
		addToMap(p, allPassives);
	}
    //Build does not extends AbstractCustomizable, so I cannot trigger addToMap
    public void addBuild(Build b){
		if(b == null){
            throw new NullPointerException();
        }
        allBuilds.put(b.getName().toUpperCase(), b);
	}
    
    private void addAllToMap(AbstractCustomizable[] cs, HashMap<String, ? extends AbstractCustomizable> map){
        if(cs == null){
            throw new NullPointerException("Cannot add null to map");
        }
        if(map == null){
            throw new NullPointerException("Cannot add customizables to null map");
        }
        for(AbstractCustomizable c : cs){
            addToMap(c, map);
        }
    }
    public void addActives(AbstractActive[] as){
        addAllToMap(as, allActives);
    }
    public void addCharacterClasses(CharacterClass[] cs){
        addAllToMap(cs, allCharacterClasses);
    }
    public void addPassives(AbstractPassive[] ps){
		addAllToMap(ps, allPassives);
	}
    public void addBuilds(Build[] bs){
        if(bs == null){
            throw new NullPointerException("cannot add null builds");
        }
        for(Build b : bs){
            addBuild(b);
        }
    }
    
    public AbstractCustomizable getByName(String n, HashMap<String, ? extends AbstractCustomizable> map){
        if(!map.containsKey(n.toUpperCase())){
            throw new NoSuchElementException("No customizable found with name " + n);
        }
        return map.get(n.toUpperCase()).copy();
    }
    public AbstractActive getActiveByName(String n){
        return (AbstractActive)getByName(n, allActives);
    }
    public CharacterClass getCharacterClassByName(String n){
        return (CharacterClass)getByName(n, allCharacterClasses);
    }
    public AbstractPassive getPassiveByName(String n){
        return (AbstractPassive)getByName(n, allPassives);
	}
    public Build getBuildByName(String name){
		if(!allBuilds.containsKey(name.toUpperCase())){
            throw new NoSuchElementException("No build found with name " + name + ". Did you remember to call Build.addBuild(...)?");
        }
        return allBuilds.get(name.toUpperCase()).copy();
	}
    
    public AbstractCustomizable[] getAll(HashMap<String, ? extends AbstractCustomizable> map){
        return map.values().toArray(new AbstractCustomizable[map.size()]);
    }
    public AbstractActive[] getAllActives(){
        return Arrays.copyOf(getAll(allActives), allActives.size(), AbstractActive[].class);
    }
    public CharacterClass[] getAllCharacterClasses(){
        return Arrays.copyOf(getAll(allCharacterClasses), allCharacterClasses.size(), CharacterClass[].class);
    }
    public AbstractPassive[] getAllPassives(){
		return Arrays.copyOf(getAll(allPassives), allPassives.size(), AbstractPassive[].class);
	}
    public Build[] getAllBuilds(){
        return allBuilds.values().toArray(new Build[allBuilds.size()]);
    }
    
    public String[] getAllNames(HashMap<String, ? extends AbstractCustomizable> map){
        //key is capitalized, but we want regular casing, so we can't get this from the key set
        return map.values().stream().map((AbstractCustomizable c)->c.getName()).toArray(String[]::new);
    }
    public String[] getAllActiveNames(){
        return getAllNames(allActives);
    }
    public String[] getAllCharacterClassNames(){
        return getAllNames(allCharacterClasses);
    }
    public String[] getAllPassivesNames(){
		return getAllNames(allPassives);
	}
    public String[] getAllBuildNames(){
		return allBuilds.values().stream().map((b)->b.getName()).toArray(String[]::new);
	}
    
    public AbstractActive getDefaultActive(){
        return DEFAULT_ACTIVE.copy();
    }    
    public CharacterClass getDefaultCharacterClass(){
        return DEFAULT_CHARACTER_CLASS.copy();
    }
    public AbstractPassive getDefaultPassive(){
        return DEFAULT_PASSIVE.copy();
    }
    public Build getDefaultBuild(){
        return DEFAULT_BUILD.copy();
    }
    
    public void loadDefaultActives(){
		ElementalActive s = new ElementalActive("Slash", 1, 1, 5, 0, 3);
		s.setParticleType(ParticleType.SHEAR);
        
		ElementalActive bt = new BoulderToss();
        ElementalActive fc = new FlameCharge();
		
        ElementalActive eq = new ElementalActive("Earthquake", 1, 0, 2, 5, 1);
		eq.setParticleType(ParticleType.BURST);
        eq.setColors(CustomColors.earthColors);
        eq.addStatus(new Stun(3, 1));
        
		ElementalActive fof = new ElementalActive("Fields of Fire", 1, 0, 5, 3, 1);
		fof.setParticleType(ParticleType.SHEAR);
        fof.setColors(CustomColors.fireColors);
        fof.addStatus(new Burn(2, 3));
		
		ElementalActive fb = new ElementalActive("Fireball", 2, 3, 3, 3, 5);
		fb.setParticleType(ParticleType.BURST);
        fb.setColors(CustomColors.fireColors);
        
		ElementalActive b = new ElementalActive("Boreus", 1, 5, 5, 0, 1);
		b.setParticleType(ParticleType.BEAM);
        b.setColors(CustomColors.airColors);
        
        ElementalActive z = new ElementalActive("Zephyrus", 1, 5, 5, 0, 1);
		z.setParticleType(ParticleType.BEAM);
		z.setColors(CustomColors.airColors);
        
        ElementalActive wb = new ElementalActive("Waterbolt", 1, 3, 3, 1, 2);
		wb.setParticleType(ParticleType.BEAM);
        wb.setColors(CustomColors.waterColors);
        
        ElementalActive wp = new ElementalActive("Whirlpool", 1, 0, 4, 4, 3);
        wp.setParticleType(ParticleType.SHEAR);
        wp.setColors(CustomColors.waterColors);
        
		ElementalActive rod = new ElementalActive("RAINBOW OF DOOM", 4, 3, 5, 5, 1);
		rod.setParticleType(ParticleType.BURST);
		rod.setColors(CustomColors.rainbow);
		
		
		BoostActive ws = new BoostActive("Warrior's Stance", new AbstractStatus[]{new Strength(1, 2), new Resistance(1, 2)});
		BoostActive st = new BoostActive("Speed Test", new AbstractStatus[]{new Rush(2, 3)});
		BoostActive ss = new BoostActive("Shield Stance", new AbstractStatus[]{new Resistance(2, 3)});
		BoostActive hr = new BoostActive("Healing Rain", new AbstractStatus[]{new Regeneration(2, 3)});
		BoostActive bs = new BoostActive("Blade Stance", new AbstractStatus[]{new Strength(2, 3)});
        BoostActive br = new BoostActive("Burning Rage", new AbstractStatus[]{new Strength(3, 3), new Burn(3, 3)});
		
		addActives(new AbstractActive[]{
			s,
			bt,
            fc,
			eq,
			fof,
			fb,
			br,
			b,
            z,
			rod,
			wb,
            wp,
			ws,
			st,
			ss,
			hr,
			bs
		});
    }
    
    public void loadDefaultCharacterClasses(){
        CharacterClass fire = new CharacterClass("Fire", CustomColors.fireColors, 1, 5, 4, 3);
		CharacterClass air = new CharacterClass("Air", CustomColors.airColors, 2, 3, 1, 5);
		CharacterClass earth = new CharacterClass("Earth", CustomColors.earthColors, 4, 4, 4, 1);
		CharacterClass water = new CharacterClass("Water", CustomColors.waterColors, 5, 1, 3, 3);
		
		addCharacterClasses(
            new CharacterClass[]{
                fire,
                air,
                earth,
                water
            }
        );
    }
    
    public void loadDefaultPassives(){
        // on be hit
		OnBeHitPassive t = new OnBeHitPassive("Toughness", true);
		t.addStatus(new Resistance(1, 1));
        OnBeHitPassive cu = new OnBeHitPassive("Cursed", false);
        cu.addStatus(new Stun(3, 1));
		
        // on hit
        OnHitPassive be = new OnHitPassive("Burning Edge", false);
        be.addStatus(new Burn(1, 1));
        OnHitPassive ch = new OnHitPassive("Crippling Hits", false);
        ch.addStatus(new Stun(1, 1));
        OnHitPassive lh = new OnHitPassive("Leechhealer", true);
		lh.addStatus(new Regeneration(1, 1));
		OnHitPassive m = new OnHitPassive("Momentum", true);
		m.addStatus(new Rush(1, 1));
		OnHitPassive s = new OnHitPassive("Sharpen", true);
		s.addStatus(new Strength(1, 1));
        
		//threshold
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
				t,
				b,
				d,
				e,
				re,
                cu,
                be,
                ch
		});
    }
    public void loadDefaultBuilds(){
        addBuilds(new Build[]{
            new Build("Default Earth", "Earth", "Boulder Toss", "Warrior's Stance", "Earthquake", "Toughness", "Retaliation", "Crippling Hits"),
            new Build("Default Fire", "Fire", "Fireball", "Fields of Fire", "Burning Rage", "Escapist", "Burning Edge", "Bracing"),
            new Build("Default Water", "Water", "Waterbolt", "Whirlpool", "Healing Rain", "Sharpen", "Bracing", "Leechhealer"),
            new Build("Default Air", "Air", "Boreus", "Zephyrus", "Speed Test", "Momentum", "Sharpen", "Leechhealer"),
            new Build("Flame Charge Fire", "Fire", "Flame Charge", "Earthquake", "Burning Rage", "Cursed", "Burning Edge", "Crippling Hits"),
        });
    }
    
    public void loadDefaults(){
        loadDefaultActives();
        loadDefaultCharacterClasses();
        loadDefaultPassives();
        loadDefaultBuilds();
    }
    
    //https://stackoverflow.com/questions/11016092/how-to-load-classes-at-runtime-from-a-folder-or-jar
    public void loadFile(File f){
        try {
            JarFile jar = new JarFile(f);
            URLClassLoader loader = URLClassLoader.newInstance(new URL[]{
                new URL("jar:file:" + f.getAbsolutePath() + "!/")
            });
            jar.stream().filter((JarEntry entry)->{
                return !entry.isDirectory();
            }).filter((JarEntry entry)->{
                return entry.getName().endsWith(".class");
            }).map((JarEntry entry)->{
                return entry.getName().replace(".class", "").replace("/", ".");
            }).forEach((String className)->{
                System.out.println("DataSet.loadFile loading class " + className);
                try {
                    Class c = loader.loadClass(className);
                    Object obj = c.newInstance();
                    if(obj instanceof AbstractActive){
                        addActive((AbstractActive)obj);
                    } else if(obj instanceof AbstractPassive){
                        addPassive((AbstractPassive)obj);
                    } else if(obj instanceof CharacterClass){
                        addCharacterClass((CharacterClass)obj);
                    } else if(obj instanceof Build){
                        addBuild((Build)obj);
                    } else {
                        System.err.println(c.getName() + " does not extend AbstractCustomizable");
                    }
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
