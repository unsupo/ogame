package ogame.pages;


import ogame.utility.Initialize;
import ogame.utility.Resource;
import utilities.selenium.UIMethods;

import java.util.concurrent.TimeUnit;

/**
 * Created by jarndt on 9/19/16.
 */
public class Research extends OGamePage{
    public static final String ID = "id";
    
    public static final String ENERGY = "Energy Technology";
    public static final String LASER = "Laser Technology";
    public static final String ION = "Ion Technology";
    public static final String HYPERSPACE_TECH = "Hyperspace Technology";
    public static final String PLASMA = "Plasma Technology";
    public static final String COMBUSTION = "Combustion Drive";
    public static final String IMPULSE = "Impulse Drive";
    public static final String HYPERSPACE_DRIVE = "Hyperspace Drive";
    public static final String ESPIONAGE = "Espionage Technology";
    public static final String COMPUTER = "Computer Technology";
    public static final String ASTROPHYSICS = "Astrophysics";
    public static final String INTERGALACTIC = "Intergalactic Research Network";
    public static final String GRAVITON = "Graviton Technology";
    public static final String WEAPONS = "Weapons Technology";
    public static final String SHIELDING = "Shielding Technology";
    public static final String ARMOUR = "Armour Technology";
    
    public static final String[] names = {ENERGY, LASER, ION, HYPERSPACE_TECH, PLASMA, COMBUSTION, IMPULSE, 
    		HYPERSPACE_DRIVE, ESPIONAGE, COMPUTER, ASTROPHYSICS, INTERGALACTIC, GRAVITON, WEAPONS, SHIELDING, ARMOUR};
    

    public static final String RESEARCH = "Research";

    @Override
    public String getPageLoadedConstant() {
        return null;
    }

    private Action performAction(String constant){
        String webName = Initialize.getBuildableByName(constant).getWebName();
        UIMethods.clickOnAttributeAndValue(ID,webName);
        UIMethods.waitForText(constant,30, TimeUnit.SECONDS);
        return new Action();
    }

    public Action clickOnEnergyTechnology() {
        return performAction(ENERGY);
    }

    public Action clickOnCombustionDrive() {
        return performAction(COMBUSTION);
    }

    public Action clickOnResearchByName(String name) {
        return performAction(name);
    }

}
