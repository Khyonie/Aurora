package fish.yukiemeralis.aurora.economy;

import org.bukkit.Material;

public enum ShopCategory {
    BLOCKS("Blocks", Material.BRICKS),
    BUILDING("Building", Material.SPRUCE_FENCE),
    MATERIALS("Materials", Material.STICK),
    FOOD("Foodstuffs", Material.COOKED_BEEF)
    ;

    private ShopCategory(String friendlyName, Material icon)
    {

    }
}
