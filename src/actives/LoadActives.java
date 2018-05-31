package actives;

import entities.ParticleType;
import graphics.CustomColors;
import statuses.StatusName;

// there's gotta be a better way to do this
public class LoadActives {
	public static void load(){
		// read from file later?
		MeleeActive hs = new MeleeActive("Heavy Stroke", 4, 4);
		MeleeActive s = new MeleeActive("Slash", 1, 2);
		
		ElementalActive bt = new ElementalActive("Boulder Toss", 5, 5, 1, 1, 2, 2, 3, 4);
		bt.setParticleType(ParticleType.BURST);
		bt.setColorBlend(CustomColors.earthColors);
		
		ElementalActive cd = new ElementalActive("Cursed Daggers", 2, 3, 2, 5, 5, 5, 0, 0);
		cd.setParticleType(ParticleType.BEAM);
		cd.setParticleColor(CustomColors.green);
		
		ElementalActive eq = new ElementalActive("Earthquake", 4, 5, 0, 0, 0, 2, 3, 2);
		eq.setParticleType(ParticleType.BURST);
		eq.setColorBlend(CustomColors.earthColors);
		
		ElementalActive fof = new ElementalActive("Fields of Fire", 3, 3, 0, 0, 0, 5, 3, 2);
		fof.setParticleType(ParticleType.SHEAR);
		fof.setColorBlend(CustomColors.fireColors);
		
		ElementalActive fb = new ElementalActive("Fireball", 4, 5, 2, 3, 4, 3, 3, 4);
		fb.setParticleType(ParticleType.BURST);
		
		ElementalActive mfb = new ElementalActive("Mega Firebolt", 4, 4, 1, 1, 3, 3, 3, 4);
		mfb.setParticleType(ParticleType.SHEAR);
		mfb.setColorBlend(CustomColors.fireColors);
		
		ElementalActive mwb = new ElementalActive("Mini Windbolt", 1, 1, 2, 3, 5, 5, 0, 1);
		mwb.setParticleType(ParticleType.BEAM);
		mwb.setColorBlend(CustomColors.airColors);
		
		ElementalActive rod = new ElementalActive("RAINBOW OF DOOM", 1, 5, 4, 5, 3, 5, 5, 1);
		rod.setParticleType(ParticleType.BURST);
		rod.setColorBlend(CustomColors.rainbow);
		
		ElementalActive wb = new ElementalActive("Waterbolt", 3, 3, 1, 3, 3, 3, 1, 2);
		wb.setParticleType(ParticleType.BEAM);
		wb.setColorBlend(CustomColors.waterColors);
		
		BoostActive ws = new BoostActive("Warrior's Stance", 1, 3, new StatusName[]{StatusName.STRENGTH, StatusName.RESISTANCE}, new int[]{1, 1}, new int[]{5, 5});
		BoostActive st = new BoostActive("Speed Test", 1, 3, new StatusName[]{StatusName.RUSH}, new int[]{2}, new int[]{7});
		BoostActive ss = new BoostActive("Shield Stance", 1, 3, new StatusName[]{StatusName.RESISTANCE}, new int[]{2}, new int[]{7});
		BoostActive hr = new BoostActive("Healing Rain", 5, 5, new StatusName[]{StatusName.REGENERATION}, new int[]{1}, new int[]{90});
		BoostActive h = new BoostActive("Heal", 5, 5, new StatusName[]{StatusName.HEALING}, new int[]{2}, new int[]{0});
		BoostActive bs = new BoostActive("Blade Stance", 1, 3, new StatusName[]{StatusName.STRENGTH}, new int[]{2}, new int[]{7});
		BoostActive br = new BoostActive("Burning Rage", 3, 3, new StatusName[]{StatusName.STRENGTH, StatusName.BURN}, new int[]{3, 2}, new int[]{10, 10});
		
		AbstractActive.addActives(new AbstractActive[]{
			s,
			hs,
			bt,
			cd,
			eq,
			fof,
			fb,
			mfb,
			mwb,
			rod,
			wb,
			ws,
			st,
			ss,
			hr,
			h,
			bs,
			br
		});
	}
}
