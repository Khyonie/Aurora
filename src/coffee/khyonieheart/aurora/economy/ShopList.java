package coffee.khyonieheart.aurora.economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

@SuppressWarnings("unused")
public class ShopList 
{
    private static Map<ShopCategory, List<ShopEntry>> SHOP_DATA = new HashMap<>() {{
        for (ShopCategory cat : ShopCategory.values())
            SHOP_DATA.put(cat, new ArrayList<>());
    }};

    public static void init()
    {
        // Logs
        ofAll(ShopCategory.BUILDING, 4, 40, -1, 1.0f, 1.0f, -1,
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.ACACIA_LOG,
            Material.JUNGLE_LOG,
            Material.SPRUCE_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM
        );

        ofAll(ShopCategory.BUILDING, 4, 10, -1, 1.0f, 1.0f, -1, 
            Material.OAK_PLANKS,
            Material.BIRCH_PLANKS,
            Material.ACACIA_PLANKS,
            Material.JUNGLE_PLANKS,
            Material.SPRUCE_PLANKS,
            Material.DARK_OAK_PLANKS,
            Material.MANGROVE_PLANKS,
            Material.CRIMSON_PLANKS,
            Material.WARPED_PLANKS
        );
    }

    private static ShopEntry of(ShopCategory category, Material material, int buyValue, int sellValue, float sellModifier, float inflationModifier, int maxBeforeLock)
    {
        return of(category, material, 1, buyValue, sellValue, sellModifier, inflationModifier, maxBeforeLock);
    }    

    private static ShopEntry of(ShopCategory category, Material material, int amount, int buyValue, int sellValue, float sellModifier, float inflationModifier, int maxBeforeLock)
    {
        ShopEntry entry = new ShopEntry(material, amount, buyValue, sellValue, sellModifier, inflationModifier, maxBeforeLock);

        SHOP_DATA.get(category).add(entry);

        return entry;
    }

    private static void ofAll(ShopCategory category, int amount, int buyValue, int sellValue, float sellModifier, float inflationModifier, int maxBeforeLock, Material... mats)
    {
        for (Material m : mats)
            of(category, m, amount, buyValue, sellValue, sellModifier, inflationModifier, maxBeforeLock);
    }
}
