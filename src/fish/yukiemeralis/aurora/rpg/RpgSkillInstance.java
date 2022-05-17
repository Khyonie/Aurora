package fish.yukiemeralis.aurora.rpg;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.surface2.SimpleComponentBuilder;
import fish.yukiemeralis.eden.surface2.SurfaceGui;
import fish.yukiemeralis.eden.surface2.component.GuiComponent;
import fish.yukiemeralis.eden.surface2.component.GuiItemStack;
import fish.yukiemeralis.eden.utils.DataUtils;
import fish.yukiemeralis.eden.utils.PrintUtils;

public class RpgSkillInstance implements GuiComponent
{
    private final AuroraSkill skill;
    private final Player target;

    public RpgSkillInstance(AuroraSkill skill, Player target)
    {
        this.skill = skill;
        this.target = target;
    }

    @Override
    public GuiItemStack generate()
    {
        Material icon = skill.isUnlocked(target) ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE;
        String name = (skill.isUnlocked(target) ? "§r§a§l" : "§r§8§l") + skill.getName();

        List<String> description = DataUtils.mapList(Arrays.asList(skill.getDescription()), (in) -> "§7§o" + in);
        description.add("");

        String message = "§r§aThis skill has been unlocked!";
        if (!skill.isUnlocked(target))
            message = AuroraRpgStats.hasSkillPoint(target) ? "§r§7Click to unlock skill!" : "§r§7You don't have any skill points.";

        description.add(message);
        return SimpleComponentBuilder.build(icon, name, 
            (e) -> {
                if (!AuroraRpgStats.hasSkillPoint(target))
                {
                    PrintUtils.sendMessage(target, "§cYou don't have any skill points to spend!");
                    return;
                }

                if (skill.isUnlocked(target))
                {
                    PrintUtils.sendMessage(target, "You already have this skill!");
                    return;
                }

                if (!skill.unlockForPlayer(target))
                {
                    PrintUtils.sendMessage(target, "You already have this skill!");
                    return;
                }
                PrintUtils.sendMessage(target, "§aUnlocked " + skill.getName() + "!");
                target.playSound(target.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

                int points = AuroraRpgStats.getSkillPoints((Player) e.getWhoClicked());
                GuiItemStack pointsItem = SimpleComponentBuilder.build(Material.GOLD_NUGGET, "§r§6§l" + points + " skill " + PrintUtils.plural(points, "point", "points") + " available", (event) -> {}, "§7§oSkill points are earned by leveling", "§7§oup any stat.");
                SurfaceGui.getOpenGui(e.getWhoClicked()).unwrap().updateSingleItem(e.getWhoClicked(), 1, pointsItem, false);

                int index = 0;
                for (AuroraSkill skill : AuroraSkill.values())
                {
                    SurfaceGui.getOpenGui(e.getWhoClicked()).unwrap().updateSingleComponent(e.getWhoClicked(), 9 + index, new RpgSkillInstance(skill, (Player) e.getWhoClicked()).generate());
                    index++;
                }
            }, 
            description.toArray(new String[description.size()])
        );
    }

    public AuroraSkill getAssociatedSkill()
    {
        return skill;   
    }
}
