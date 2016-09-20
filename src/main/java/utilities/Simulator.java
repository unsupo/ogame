package utilities;

import objects.PlayerSIM;
import objects.Ship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by jarndt on 8/8/16.
 */
public class Simulator {
    public static void main(String[] args) throws IOException {
//        List<PlayerSIM> battleInfo = BattleInfoParser.parse(Utility.BATTLE_INFO);
    	List<PlayerSIM> battleInfo = new LinkedList<PlayerSIM>();
    	PlayerSIM attacker = new PlayerSIM("A");
    	PlayerSIM defender = new PlayerSIM("D");
    	battleInfo.add(attacker);
    	battleInfo.add(defender);
    	for(PlayerSIM player : battleInfo){
    		player.setArmourTeach(12);
    		player.setShieldingTech(12);
    		player.setWeaponsTech(12);
    	}
    	defender.addShip("light fighter", 10000);
    	attacker.addShip("deathstar", 1);
        new Simulator().simulate(battleInfo.get(0),battleInfo.get(1));
    }

    /*
    sudo code
    get two arrays of attacking ships and defending ships

    each tech level increases by 10% for each level then:
        V=V(1+L/10) is the base level for each ship after research improvement

    each round ships start with default W,S but H is that of last round (default if it is first round)

    In each round, all participating units(defenses+ships) randomly choose a target enemy unit.

    while !isRoundOver and numRounds < 7:
        For each shooting unit:
            for each defending unit:
                if 100*s.W < d.S:
                    continue;
                else if s.W < d.S:
                    d.S = d.S - s.W;
                else
                    d.H = d.H - (s.W - d.S)
                    d.S = 0
                if d.H < .7* d.H_i: //(initial hull)
                    isExploded = 1-d.H/d.H_i
                if isExploded:
                    d.H = 0
                if s.hasRapidFireAgainst(d):
                    isChooseAnotherUnit = (r-1)/r

        for each unit:
            if H <= 0:
                remove from units list //(it is destroyed)

        if attacker.hasNoUnits or defender.hasNoUnits:
            isRoundOver = true;

    for each defending unit of the defender (RL,LL,HL,GC,IC,PT,LSD,SSD):
        if unit.H == 0 && chance <= 70:
            unit.H = unit.H_i

     */
    public void simulate(PlayerSIM attacker, PlayerSIM defender){
        modifyShips(attacker);
        modifyShips(defender);
        List<Ship>      attackerStart = clone(attacker.getShips()),
                		defenderStart = clone(defender.getShips());
        
        
        List<Ship>      attackerAfter = clone(attacker.getShips()),
                        defenderAfter = clone(defender.getShips());

        int maxNumRounds = 6;
        boolean isFightingOver = false;
        while(maxNumRounds-- > 0 && !isFightingOver){
            fight(attacker, defender, attackerAfter, defenderAfter);
            fight(defender,attacker, defenderAfter, attackerAfter);

            removeTheDestroyed(attackerAfter);
            removeTheDestroyed(defenderAfter);
        }
        System.out.println("Attacker: ");
        printBeforeAfter(attackerStart, attackerAfter);
        System.out.println("Defender: ");
        printBeforeAfter(defenderStart, defenderAfter);
    }
    
    private void printBeforeAfter(List<Ship> before, List<Ship> after){
    	HashMap<String, Integer> shipCountsBefore, shipCountsAfter;
    	shipCountsBefore = new HashMap<String, Integer>();
    	shipCountsAfter = new HashMap<String, Integer>();
    	for(Ship ship : before){
    		Integer curShips = shipCountsBefore.get(ship.getName());
    		shipCountsBefore.put(ship.getName(), curShips == null ? 1: curShips+1);
    	}
    	for(Ship ship : after){
    		Integer curShips = shipCountsAfter.get(ship.getName());
    		shipCountsAfter.put(ship.getName(), curShips == null ? 1: curShips+1);
    	}
    	for(String name : shipCountsBefore.keySet()){
    		Integer beforeNum = shipCountsBefore.get(name);
    		Integer afterNum = shipCountsAfter.get(name) == null ? 0 : shipCountsAfter.get(name);
    		System.out.println(name + " " + beforeNum + " -> " + afterNum);
    	}
    	System.out.println();
    }

    private void removeTheDestroyed(List<Ship> ships) {
        for(int i = ships.size()-1; i>=0; i--)
            if(ships.get(i).getStructural_integrity()<=0)
                ships.remove(i);
    }

    private void modifyShips(PlayerSIM p) {
        for (Ship s : p.getShips()) {
            s.setShield_power(s.getShield_power()*(1+p.getShieldingTech()/10));
            s.setStructural_integrity(s.getStructural_integrity()*(1+p.getArmourTeach()/10));
            s.setWeapon_power(s.getWeapon_power()*(1+p.getWeaponsTech()/10));
        }
    }

    private void fight(PlayerSIM attacker, PlayerSIM defender, List<Ship> attackerAfter, List<Ship> defenderAfter) {
        List<Ship> removed = new ArrayList<>();
        for(int i = 0; i<attackerAfter.size(); i++){
        	if(defenderAfter.size() == 0){
        		break;
        	}
            Ship attackShip = attackerAfter.get(i);
            List<Ship> thisRoundDefender = new ArrayList<>(defenderAfter);
            int W = attackShip.getWeapon_power();
            int ran = new Random().nextInt(thisRoundDefender.size());
            Ship ranTarget = thisRoundDefender.get(ran);
            attack(W,defender,ranTarget);
            thisRoundDefender.remove(ran);
            if(attackShip.getRapidFire().containsKey(ranTarget.getId()+"")) {
                double r = attackShip.getRapidFire().get(ranTarget.getId()+"");
                if (isChance((r-1)/r)) {
                    defenderAfter.remove(ran);
                    removed.add(ranTarget);
                    i--;
                }
            }
        }
        defenderAfter.addAll(removed);
    }
    

    private List<Ship> clone(List<Ship> original) {
    	List<Ship> clone = new ArrayList<Ship>();
    	for(Ship ship : original){
    		clone.add(ship.clone(ship));
    	}
    	return clone;
    }

    private void attack(int W, PlayerSIM defender, Ship defenseShip){
        int S = defenseShip.getShield_power();
        double H = defenseShip.getStructural_integrity();
        double Hi= 0;
        try {
            Hi = getInitialStructuralIntegrity(defenseShip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(100*W < S) return;
//            continue; //no damage can't get past shields
        else if(W < S)
            defenseShip.setShield_power(S-W);
        else{
            defenseShip.setStructural_integrity((int)(H - (W - S)));
            defenseShip.setShield_power(0);
            H = defenseShip.getStructural_integrity();
        }
        if(H < .7*Hi)
        	if(H< 0){
        		H = 0;
        	}
            if(isChance(1-H/Hi))
                defenseShip.setStructural_integrity(0);
    }

    private int getInitialStructuralIntegrity(Ship defenseShip) throws IOException {
        return Ship.getAllShips().stream().filter(a->a.getName().equals(defenseShip.getName())).collect(Collectors.toList()).get(0).getStructural_integrity();
    }
    
    int counter;
    Random random = new Random();

    public boolean isChance(double chance){
        boolean c =  random.nextDouble() <= chance;
//        counter++;
//        System.out.print(".");
//        if(!c){
//        	System.out.println("We did it " + counter);
//        }
        return c;
    }

}
/*

Combat takes place when an attacking fleet reaches its destination.

Sides
In combat, there are two sides: the attacker and the defender. There is the possibility that the attacker has launched his fleets against a planet with no defense or ships, in which case he automatically wins the combat. But otherwise, if the defender has ships or defense on his planet, each side will fire upon the enemy.

If Alliance Combat System is enabled in the universe, there can be multiple attackers and defenders.

How it works
Before we can understand how combat works, we have to know that every unit has three basic parameters that affect combat: Weapon Power (aka Weaponry), Shield Power (aka Shielding), and Hull plating. There is also a fourth parameter, Rapid Fire, which can be significant to the final outcome.

Each combat can have 3 outcomes: attacker wins, draw, and defender wins; Each combat is organized in rounds. There are at most 6 rounds, which means that if the attacker is not able to destroy all the defender's units in 6 rounds, the combat ends with a draw. Either the "draw" and the "defender wins" causes the attacker to not be able to steal the resources in the planet/moon where the attack was made.

In the beginning of each round, every unit starts with its Weaponry (with value W) and Shielding (with value S) at its initial value (specific of each ship plus technology upgrades). The Hull Plating (with value H) has the value of previous round (initial value of the ship if it is the first round).

In each round, all participating units(defenses+ships) randomly choose a target enemy unit.

For each shooting unit:

If the Weaponry of the shooting unit is less than 1% of the Shielding of the target unit, the shot is bounced, and the target unit does not lose anything (i.e. shot is wasted).
Else, if the weaponry is lower than the Shielding, then the shield absorbs the shot, and the unit does not lose Hull Plating: S = S - W.
Else, the weaponry is sufficiently strong, i.e. W > S. Then the shield only absorbs part of the shoot and the rest is dealt to the hull: H = H - (W - S) and S = 0.
If the Hull of the target ship is less than 70% of the initial Hull (H_i) of the ship (initial of the combat), then the ship has a probability of 1 - H/H_i of exploding. If it explodes, the hull is set to zero: H = 0. (but it can still be shot by the other units on this round, because they already target it.)
Finally, if the shooting unit has rapid fire (with value r) against the target unit, it has a chance of (r-1)/r of choosing another target at random, and repeating the above steps for that new target.
For every unit that ends the fight with H <= 0, it is destroyed and thus does not appear in the next round. If every unit of a side (attacker or defender) is destroyed at the end of the round, the battle ends with the opposite side winning.

After the combat, each defensive structure of the defender has a 70% chance of being immediately rebuilt without additional costs.

Combat example
As an example, let's consider an attacking fleet of 1 Cruiser vs 2 Missile Launchers + 1 Heavy Laser. Both teams have 0 technology of weaponry, shield and hull plating. With these technologies, Cruiser for instance will have 2700 Hull Plating, 50 Shield and 400 Weaponry (2700:50:400 using an abbreviated notation).

One possible outcome could be the following:

round 1
attacker fires at defenderEdit
Cruiser with 2700:50:400 fires at Missile Laucher with 200:20:80; result is Missile Laucher with -180:0:80

Rapid Fire:

Cruiser has rapid fire against Missile Launcher.
dice was 0.62, comparing with 0.90: Cruiser gets another shot.
Cruiser with 2700:50:400 fires at Missile Laucher with 200:20:80; result is Missile Launcher with -180:0:80

Rapid Fire:

Cruiser has rapid fire against Missile Launcher.
dice was 0.09, comparing with 0.90: Cruiser gets another shot.
Cruiser with 2700:50:400 fires at Missile Launcher with -180:0:80; result is Missile Launcher with -580:0:80

Rapid Fire:

Cruiser has rapid fire against Missile Launcher.
dice was 0.83, comparing with 0.90: Cruiser gets another shot.
Cruiser with 2700:50:400 fires at Heavy Laser with 800:100:250;

probability of exploding of 37.50%; dice value of 0.402 comparing with 0.625: unit didn't explode.
result is Heavy Laser with 500:0:250

Rapid Fire:

Cruiser doesn't have rapid fire against Heavy Laser.
defender fires at attackerEdit
Missile Laucher with -580:0:80 fires at Cruiser with 2700:50:400; result is Cruiser with 2670:0:400

Missile Laucher with -180:0:80 fires at Cruiser with 2670:0:400; result is Cruiser with 2590:0:400

Heavy Laser with 500:0:250 fires at Cruiser with 2590:0:400; result is Cruiser with 2340:0:400

remove ships and restore shields.
Cruiser still has integrity, restore its shield.

Missile Laucher lost all its integrity, remove it from battle.

Missile Laucher lost all its integrity, remove it from battle.

Heavy Laser still has integrity, restore its shield.

round 2
attacker fires at defender
Cruiser with 2340:50:400 fires at Heavy Laser with 500:100:250

probability of exploding of 75.00%: dice value of 0.30 comparing with 0.25: unit exploded.
result is Heavy Laser with -1:0:250

Rapid Fire:

Cruiser doesn't have rapid fire against Heavy Laser.
defender fires at attacker
Heavy Laser with -1:0:250 fires at Cruiser with 2340:50:400; result is Cruiser with 2140:0:400

remove ships and restore shields.
Cruiser still has integrity, restore its shield.

Heavy Laser lost all its integrity, remove it from battle.


The battle ended after 2 rounds with attacker winning.


Some notes on the simulation.
On round 1, part 1.1.1, because Cruiser randomly chose the 2 missile launchers one after another, both were destroyed. However, they are only destroyed in the end of the round; they still fired at the Cruiser.
On round 1, part 1.1.1, the 3rd Cruiser's shot was made to an already destroyed Missile Launcher. If the roll of that Rapid Fire dice hadn't given him another shot, it wouldn't have shot the heavy laser on that round.
On round 2, part 1.2.1, because Heavy laser only had 500 integrity points, a shot from the Cruiser caused it to have 500-(400 (attack)-100 (shield)) = 200. Because 200 (current integrity)/800 (initial integrity) = 0.25, it had a probability of 75% of exploding, which occurred. Even after exploding, it still fired the shot on the Cruiser.
Outcomes and its consequences
After the combat is over, there can be 3 results:

1) The attacker wins. In this case, the attacker will pillage resources from the defender's planet. The maximum amount of resources he can pillage even if he has sufficient Cargo Capacity, is one half of the total amount of each resource. If there are multiple attackers, resources will be spread evenly. [Note that in v3.0 OGame, the honour point system can result in 75% or 100% of resources being stolen]

3) Draw. If after six rounds there is no winner, a draw is produced. The remaining attacking ships return to the planet they came from without being able to pillage the planet.

2) Defender wins. For the defender there is not much practical difference compared to a draw; for the attacker however it means all his ships are lost.


When the combat is over, the rest of the ships (not defense) destroyed in the clash are thrown into a Debris Field floating near the planet where the combat took place. The exception to this are universes with defense into DF enabled. This field may contain valuable resources (metal or crystal; deuterium cannot be present in the debris) that can be collected by whichever player's recyclers arrive there first (including any other players who did not take part in the battle). The amount of resources in the debris field depends on how many ships were destroyed that battle; defensive units produce no debris. Because the debris field is a separate location from the planet and is only created after the battle, the defender's recyclers have a better chance of getting there first than the attacker's, since the field is right next to the defender's planet and likely far away from the attacker's. The attacker can plan ahead against this by, before the main attack force arrives, attacking the defender with an espionage probe, which will generate a debris field of 300 crystal, and send the recyclers to the field so that they arrive just after the main attack force has decimated the defender's ships. This ensures the attacker gets the debris.

When a combat takes place, all participating parties receive a Combat Report unless the battle was just one round, in which case the attacker does not receive a report [so attacking with an espionage probe no longer reveals defense].
 */