package fish.yukiemeralis.aurora.rpg;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.permissions.ModulePlayerData;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class AuroraRpgStats 
{
    private static Map<String, Object> DEFAULT_DATA = new HashMap<>() {{
        put("skillpoints", 0);

        for (RpgStat stat : RpgStat.values())
        {
            put(stat.dataName(), 0);
            put(stat.expName(), 0);
        }

        for (AuroraSkill skill : AuroraSkill.values())
            put(skill.dataName(), false);
    }};

    private static Map<Player, Tuple2<RpgStat, BossBar>> TRACKING_PROGRESSION = new HashMap<>();

    public static Map<String, Object> getDefaultData()
    {
        return DEFAULT_DATA;
    }

    public static boolean hasSkillPoint(Player target)
    {
        return getSkillPoints(target) > 0;
    }

    public static int getSkillPoints(Player target)
    {
        return Eden.getPermissionsManager().getPlayerData(target).getModuleData("AuroraRPG").getInt("skillpoints");
    }

    public static boolean hasBar(Player player, RpgStat stat)
    {
        if (!TRACKING_PROGRESSION.containsKey(player))
            return false;
        return TRACKING_PROGRESSION.get(player).getA().equals(stat);
    }

    public static boolean hasBar(Player player)
    {
        return TRACKING_PROGRESSION.containsKey(player);
    }

    public static Tuple2<RpgStat, BossBar> getBarData(Player player)
    {
        if (!TRACKING_PROGRESSION.containsKey(player))
            return null;
        return TRACKING_PROGRESSION.get(player);
    }

    public static BossBar registerNewBar(Player player, RpgStat stat)
    {
        BossBar bar = Eden.getInstance().getServer().createBossBar("§r§9" + stat.getFriendlyName() + "  progress", BarColor.BLUE, BarStyle.SEGMENTED_10);

        TRACKING_PROGRESSION.put(player, new Tuple2<>(stat, bar));
        ModulePlayerData data = Eden.getPermissionsManager().getPlayerData(player).getModuleData("AuroraRPG");

        bar.setProgress(data.getInt(stat.expName()) / stat.getRequiredExpAtLevel(data.getInt(stat.dataName())));
        bar.addPlayer(player);
        bar.setVisible(true);

        return bar;
    }

    public static void removeBar(Player player)
    {
        if (!TRACKING_PROGRESSION.containsKey(player))
            return; // No bar to remove
        TRACKING_PROGRESSION.get(player).getB().setVisible(false);
        TRACKING_PROGRESSION.get(player).getB().removeAll();
    }
}
