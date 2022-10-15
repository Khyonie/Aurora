package coffee.khyonieheart.aurora.rpg.enums;

import org.bukkit.Material;

public enum SkillCategory 
{
    COMBAT("Combat", "Influence your damage output.", Material.IRON_SWORD),
    MINING("Mining", "Refine your resource-gathering.", Material.DIAMOND_PICKAXE),
    FARMING("Farming", "Reap better rewards from crops.", Material.IRON_HOE),
    ARCHERY("Archery", "Draw more power from arrows.", Material.BOW),
    MISC("Misc.", "General abilities.", Material.CRAFTING_TABLE)
    ;

    private final String friendlyName, description;
    private final Material icon;

    private SkillCategory(String friendlyName, String description, Material icon)
    {
        this.friendlyName = friendlyName;
        this.description = description;
        this.icon = icon;
    }

    public String getFriendlyName()
    {
        return this.friendlyName;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Material getIcon()
    {
        return this.icon;
    }
}
