package fish.yukiemeralis.aurora.rpg.enums;

import java.util.Random;

import org.bukkit.entity.Player;

import fish.yukiemeralis.eden.Eden;

public enum AuroraSkill 
{
    NINJA_TRAINING(20.0f,  3, 5.0f,   SkillCategory.COMBAT,  "Ninja training",      "Low chance to ignore incoming",         "damage.", "", "Further levels increase the chance to", "dodge by 5%."), 
    ARROW_REFUND  (33.3f,  1, 11.5f,  SkillCategory.ARCHERY, "Loyal arrows",        "High chance to return fired arrows.",   "Increases arrow damage by 20%."), 
    QUADRUPLE_ORES(5.0f,   1, 0.0f,   SkillCategory.MINING,  "Pickaxe technique",   "Small chance to quadruple dropped",     "materials when breaking ore."),
    ARCHAEOLOGIST (0.008f, 1, 0.0f,   SkillCategory.MINING,  "Archaeologist",       "Tiny chance to obtain rarities when",   "digging dirt."),
    CLEAN_BLOW    (10.0f,  4, 13.33f, SkillCategory.COMBAT,  "Clean blows",         "Small chance to deal double damage",    "when attacking with swords.", "", "Further levels increase the chance to", "deal double damage by 13.33%."), 
    WAKING_RUSH   (100.0f, 1, 0.0f,   SkillCategory.MISC,    "Waking rush",         "Respawn with speed, strength, and",     "resistance effects."), 
    SWANSONG      (100.0f, 3, 0.0f,   SkillCategory.COMBAT,  "Swan song",           "On death, stay alive for 10 seconds.",  "Gain speed, jump height, strength,", "and become invincible.", "", "Further levels lower the cooldown", "by 3 minutes."), 
    WELL_RESTED   (100.0f, 1, 0.0f,   SkillCategory.MISC,    "Well rested",         "After sleeping, gain mining haste for", "5 minutes."), 
    DOUBLE_MEND   (33.3f,  2, 33.3f,  SkillCategory.MISC,    "Double mend",         "High chance for mending to proc twice.", "", "Further levels increase the chance to", "double-mend by 33.33%."),
    EMERALD_HILL  (2.0f,   3, 5.0f,   SkillCategory.FARMING, "Emerald hill zone",   "Tiny chance to drop an emerald when",   "breaking a crop.", "", "Further levels increase the chance to", "drop an emerald by 5%. Additionally, more", "emeralds may drop."),
    DAZE_MOB      (5.0f,   2, 5.0f,   SkillCategory.COMBAT,  "Daze mob",            "Small chance to make enemies lose",     "aggro when they get hit.", "", "Further levels increase the chance to", "daze an enemy by 5%."), 
    REANIMATION   (2.0f,   3, 2.0f,   SkillCategory.COMBAT,  "§cReanimation",       "Low chance for an enemy to revive",     "and attack another enemy for 20 seconds", "when slain.", "", "Further levels increase the effectiveness", "of reanimated monsters."),
    FARMHAND      (5.0f,   4, 5.0f,   SkillCategory.FARMING, "Farmhand",            "Small chance to quadruple crop yield.", "", "Further levels increase the chance to", "obtain more crops by 5%."),
    TIPPED_ARROWS (10.0f,  3, 2.0f,   SkillCategory.ARCHERY, "§cJester",            "Low chance for a random, dramatic",     "effect when an arrow hits.", "", "Further levels unlock more powerful", "effects."),
    ALCHEMIST     (100.0f, 1, 0.0f,   SkillCategory.COMBAT,  "Alchemist",           "Thrown potions return after a short",   "time."), 
    CROUCHJUMP    (100.0f, 1, 0.0f,   SkillCategory.MISC,    "§cCrouch jump",       "Crouching for a short time doubles",    "your next jump's height for 2 seconds."),
    HEADSHOT      (100.0f, 4, 0.0f,   SkillCategory.ARCHERY, "Headshot",            "Increases arrow damage when hitting",   "the head.", "", "Further levels increase damage", "by 50%."),
    SILK_SPAWNERS (100.0f, 1, 0.0f,   SkillCategory.MINING,  "Silk spawners",       "Allows the safe breaking of monster",   "spawners with a silk touch pickaxe."),
    AUTO_REPLANT  (100.0f, 1, 0.0f,   SkillCategory.FARMING, "Auto-replant",        "Automatically replants applicable",     "crops using seeds in your Inventory."),
    VEIN_MINER    (100.0f, 1, 0.0f,   SkillCategory.MINING,  "Veinminer",           "Automatically break connected ores.")
    // Boat railgun
    // Double jump
    ;

    private final float procChance, procMod;
    private final int maxLevel;
    private final String name;
    private final String[] description;
    private final SkillCategory category;

    private static final Random random = new Random();

    private AuroraSkill(float procChance, int maxLevel, float procMod, SkillCategory category, String name, String... description)
    {
        this.procChance = procChance;
        this.category = category;
        this.maxLevel = maxLevel;
        this.procMod = procMod;
        this.description = description;
        this.name = name;
    }

    public String dataName()
    {
        return this.name().toLowerCase();
    }

    public boolean proc(Player player)
    {
        return (random.nextFloat() * 100) <= procChance + (getLevel(player) * procMod);
    }

    public boolean isUnlocked(Player player)
    {
        return getLevel(player) != 0;
    }

    public int getLevel(Player player)
    {
        return Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").getInt(this.dataName());
    }

    /**
     * @deprecated Use skillLevelUp instead.
     */
    @Deprecated
    public boolean unlockForPlayer(Player player)
    {
        if (isUnlocked(player))
            return false;
        Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").setValue(this.dataName(), 1);
        Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").incrementInt("skillpoints", -1);
        return true;
    }

    public int skillLevelUp(Player player)
    {
        if (getLevel(player) == maxLevel)
            return Integer.MIN_VALUE;

        Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").incrementInt("skillpoints", -1);
        return Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG").incrementInt(this.dataName(), 1);
    }

    public String[] getDescription()
    {
        return this.description;
    }

    public String getName()
    {
        return this.name;
    }

    public int getMaxLevel()
    {
        return this.maxLevel;
    }

    public SkillCategory getCategory()
    {
        return this.category;
    }
}