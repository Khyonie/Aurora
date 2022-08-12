package fish.yukiemeralis.aurora.rpg;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.surface2.SimpleComponentBuilder;
import fish.yukiemeralis.eden.surface2.SurfaceGui;
import fish.yukiemeralis.eden.surface2.component.GuiComponent;
import fish.yukiemeralis.eden.surface2.component.GuiItemStack;
import fish.yukiemeralis.eden.utils.option.Some;
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

        String message = "§r§aSkill level: " + skill.getLevel(target) + "/" + skill.getMaxLevel();
        if (!skill.isUnlocked(target)) 
            message = AuroraRpgStats.hasSkillPoint(target) ? "§r§7Click to unlock skill!" : "§r§7You don't have any skill points.";
        
        description.add(message);

        if (skill.isUnlocked(target))
            description.add(skill.getLevel(target) == skill.getMaxLevel() ? "§r§7Skill has reached its max level." : (AuroraRpgStats.hasSkillPoint(target) ? "§r§7Click to level up skill!" : "§r§7You don't have any skill points."));

        GuiItemStack item = SimpleComponentBuilder.build(icon, name, 
            (e) -> {
                if (!AuroraRpgStats.hasSkillPoint(target))
                {
                    PrintUtils.sendMessage(target, "§cYou don't have any skill points to spend!");
                    return;
                }

                int newLevel = skill.skillLevelUp(target);

                if (newLevel == Integer.MIN_VALUE)
                {
                    PrintUtils.sendMessage(target, "This skill is at its max level! (" + skill.getLevel(target) + "/" + skill.getMaxLevel() + ")");
                    return;
                }

                int points = AuroraRpgStats.getSkillPoints(target);

                if (newLevel == 1)
                {
                    PrintUtils.sendMessage(target, "§aUnlocked " + skill.getName() + "§a!");
                } else {   
                    PrintUtils.sendMessage(target, "§aLevelled up §b" + skill.getName() + "§a! §e" + (newLevel - 1) + " -> " + newLevel + "§a.");
                }
                target.playSound(target.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);

                // Update item
                switch (SurfaceGui.getOpenGui(e.getWhoClicked()))
                {
                    case Some some:
                        some.unwrap(SurfaceGui.class).rename("Skills | " + points + " skill " + PrintUtils.plural(points, "point", "points"));
                        some.unwrap(SurfaceGui.class).updateSingleComponent(e.getWhoClicked(), e.getSlot(), generate());
                        return;
                    case default:
                        return;
                }
            }, 
            description.toArray(new String[description.size()])
        );

        if (skill.isUnlocked(target))
        {
            item.setAmount(skill.getLevel(target));

            if (skill.getLevel(target) == skill.getMaxLevel())
            {
                item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 0);
                
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                item.setItemMeta(meta);
            }
        }

        return item;
    }

    public AuroraSkill getAssociatedSkill()
    {
        return skill;   
    }
}
