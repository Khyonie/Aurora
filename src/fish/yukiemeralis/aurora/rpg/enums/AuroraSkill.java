package fish.yukiemeralis.aurora.rpg.enums;

import java.util.Random;

import org.bukkit.entity.Player;

import fish.yukiemeralis.eden.Eden;

public enum AuroraSkill 
{
    NINJA_TRAINING(20,  "Ninja training",      "Low chance to ignore incoming", "damage."), // Done
    ARROW_REFUND  (33,  "Loyal arrows",        "High chance to return fired arrows.", "Increases arrow damage by 20%."), // Done
    QUADRUPLE_ORES(5,   "Pickaxe technique",   "Small chance to quadruple dropped", "materials when breaking ore."),
    ARCHAEOLOGIST (1,   "Archaeologist",       "Tiny chance to obtain rarities when", "digging dirt."),
    CLEAN_BLOW    (10,  "Clean blows",         "Small chance to deal double damage", "when attacking with swords."), // Done
    WAKING_RUSH   (100, "Waking rush",         "Respawn with speed, strength, and", "resistance effects."), // Done
    SWANSONG      (100, "Swan song",           "On death, stay alive for 10 seconds.", "Gain speed, jump height, strength,", "and become invincible."), // Done
    WELL_RESTED   (100, "Well rested",         "After sleeping, gain mining haste for", "5 minutes."), // Done
    DOUBLE_MEND   (50,  "Double mend",         "High chance for mending to proc twice."),
    EMERALD_HILL  (1,   "Emerald hill zone",   "Tiny chance to drop an emerald when", "breaking a crop."),
    DAZE_MOB      (5,   "Daze mob",            "Small chance to make enemies lose", "aggro when they get hit."), // Done
    REANIMATION   (10,  "§cReanimation",       "Low chance for an enemy to revive", "and attack another enemy for 20 seconds", "when slain."),
    FARMHAND      (5,   "Farmhand",            "Small chance to quadruple crop yield."),
    TIPPED_ARROWS (10,  "§cJester",            "Low chance for a random, dramatic", "effect when an arrow hits."),
    ALCHEMIST     (100, "Alchemist",           "Thrown potions return after a short", "time."), // Done
    CROUCHJUMP    (100, "§cCrouch jump",       "Crouching for a short time doubles", "your next jump's height for 2 seconds.")
    // Silk spawners
    // Boat railgun
    // Headshots
    ;

    private final int proc_chance;
    private final String name;
    private final String[] description;

    private static final Random random = new Random();

    private AuroraSkill(int proc_chance, String name, String... description)
    {
        this.proc_chance = proc_chance;
        this.description = description;
        this.name = name;
    }

    public String dataName()
    {
        return this.name().toLowerCase();
    }

    public boolean proc()
    {
        return random.nextInt(100) <= proc_chance;
    }

    public boolean isUnlocked(Player player)
    {
        return Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").getValue(this.dataName(), Boolean.class);
    }

    public boolean unlockForPlayer(Player player)
    {
        if (isUnlocked(player))
            return false;
        Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").setValue(this.dataName(), true);
        Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").incrementInt("skillpoints", -1);
        return true;
    }

    public String[] getDescription()
    {
        return this.description;
    }

    public String getName()
    {
        return this.name;
    }
}
