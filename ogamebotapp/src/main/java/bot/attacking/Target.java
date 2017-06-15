package bot.attacking;

import ogame.objects.game.Coordinates;
import ogame.objects.game.Resource;
import ogame.objects.game.data.PlayerData;
import ogame.objects.game.messages.CombatMessage;
import ogame.objects.game.messages.EspionageMessage;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Created by jarndt on 6/14/17.
 */
public class Target {
    EspionageMessage espionageMessage;
    CombatMessage combatMessage;
    PlayerData player;
    LocalDateTime lastAttack, lastEspionage;
    long points;
    Coordinates coordinates;
    Resource resources, debris;
    HashMap<String,Integer> levels;
    String activity;
}
