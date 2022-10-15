package coffee.khyonieheart.aurora.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.aurora.rpg.enums.RpgStat;
import coffee.khyonieheart.aurora.rpg.enums.SkillCategory;
import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.permissions.ModulePlayerData;
import coffee.khyonieheart.eden.surface.SurfaceGui;
import coffee.khyonieheart.eden.surface.component.GuiComponent;
import coffee.khyonieheart.eden.surface.component.GuiTab;
import coffee.khyonieheart.eden.surface.enums.DefaultClickAction;
import coffee.khyonieheart.eden.surface.special.TabbedSurfaceGui;
import coffee.khyonieheart.eden.utils.ItemUtils;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

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
            put(skill.dataName(), 0);
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

    public static SurfaceGui genSkillsGui(Player target)
    {
        return genSkillsGui(target, 0);
    }

    public static SurfaceGui genSkillsGui(Player target, int tab)
    {
        // Generate tab data
		List<GuiTab> tabs = new ArrayList<>();
		for (SkillCategory category : SkillCategory.values())
		{
			List<GuiComponent> data = new ArrayList<>();
			
			for (AuroraSkill skill : AuroraSkill.values())
				if (skill.getCategory().equals(category))
					data.add(new RpgSkillInstance(skill, target));

            ItemStack icon = ItemUtils.build(category.getIcon(), "§r§9§l" + category.getFriendlyName(), "§7§o" + category.getDescription());
            ItemUtils.saveToNamespacedKey(icon, "skillcategory", category.name());

			tabs.add(new GuiTab(icon, data));
		}

		// Init
		int points = AuroraRpgStats.getSkillPoints(target);

        TabbedSurfaceGui gui = new TabbedSurfaceGui(54, "Skills | " + points + " skill " + PrintUtils.plural(points, "point", "points"), 0, tabs, DefaultClickAction.CANCEL, InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF);
        return gui;
    }
}
