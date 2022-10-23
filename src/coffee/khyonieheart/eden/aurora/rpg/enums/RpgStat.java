package coffee.khyonieheart.eden.aurora.rpg.enums;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.aurora.rpg.AuroraRpgStats;
import coffee.khyonieheart.eden.permissions.ModulePlayerData;
import coffee.khyonieheart.eden.utils.PrintUtils;

public enum RpgStat 
{
    WALKING("Walking", 300*20, 900*20), // No bonus
    MINING("Mining", 256, 512),         // No bonus
    DIGGING("Digging", 512, 1024),      // No bonus
    STEALTH("Stealth", 180*20, 300*20), // Increased chance to not be noticed by mobs when sneaking
    BRAWLING("Brawling", 100, 150),      // Higher damage dealt with no weapon
    ARCHERY("Archery", 64, 128),        // Increased damage with projectiles
    SWORDS("Swordfighting", 200, 100),  // Increased damage with swords
    AXES("Axefighting", 100, 50)        // Increased damage with axes
    ;

    private final int base, levelmod;
    private final String friendlyName;

    private RpgStat(String friendlyName, int base, int levelmod)
    {
        this.friendlyName = friendlyName;
        this.levelmod = levelmod;
        this.base = base;
    }

    public int getRequiredExpAtLevel(int level)
    {
        return base + (level * levelmod);
    }

    public String getFriendlyName()
    {
        return this.friendlyName;
    }

    public String dataName()
    {
        return this.name().toLowerCase();
    }

    public String expName()
    {
        return this.dataName() + "_exp";
    }

    public void increaseExp(Player target)
    {
        ModulePlayerData data = Eden.getPermissionsManager().getPlayerData(target).getModuleData("AuroraRPG");
        
        int progress = data.incrementInt(this.expName(), 1);

        if (AuroraRpgStats.hasBar(target, this))
            AuroraRpgStats.getBarData(target).getB().setProgress(progress / (double) getRequiredExpAtLevel(data.getInt(this.dataName())));

        if (canLevelUp(target, data))
        {
            target.playSound(target.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0f, 1.0f);
            data.incrementInt(this.dataName(), 1);
            data.setValue(this.expName(), 0);

            if (AuroraRpgStats.hasBar(target, this))
                AuroraRpgStats.getBarData(target).getB().setProgress(0 / (double) getRequiredExpAtLevel(data.getInt(this.dataName())));

            data.incrementInt("skillpoints", 1);

            PrintUtils.sendMessage(target, "Level up! Your §e" + friendlyName + "§7 level has increased by 1 (" + data.getInt(this.dataName()) + "). You have earned 1 skill point.");
        }
    }

    public boolean canLevelUp(Player target, ModulePlayerData data)
    {
        int level = data.getInt(this.name().toLowerCase());

        return data.getInt(this.expName()) >= this.base + (level * levelmod);
    }
}
