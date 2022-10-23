package coffee.khyonieheart.eden.aurora.rpg.lookups;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public class RpgBlockLookups 
{
    private static final Material[] STONE_ARRAY = new Material[] {
        Material.STONE,
        Material.GRANITE,
        Material.ANDESITE,
        Material.DIORITE,
        Material.BLACKSTONE,
        Material.DEEPSLATE,
        Material.CALCITE,
        Material.COBBLESTONE,
        Material.COBBLED_DEEPSLATE,
        Material.COAL_ORE,
        Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE,
        Material.DEEPSLATE_COAL_ORE,
        Material.COPPER_ORE,
        Material.DEEPSLATE_COPPER_ORE,
        Material.LAPIS_ORE,
        Material.DEEPSLATE_LAPIS_ORE,
        Material.REDSTONE_ORE,
        Material.DEEPSLATE_REDSTONE_ORE,
        Material.GOLD_ORE,
        Material.DEEPSLATE_GOLD_ORE,
        Material.DIAMOND_ORE,
        Material.DEEPSLATE_DIAMOND_ORE,
        Material.EMERALD_ORE,
        Material.DEEPSLATE_EMERALD_ORE,
        Material.ANCIENT_DEBRIS,
        Material.OBSIDIAN,
        Material.BASALT,
        Material.END_STONE
    };    

    private static final Material[] DIRT_ARRAY = new Material[] {
        Material.GRASS_BLOCK,
        Material.DIRT,
        Material.COARSE_DIRT,
        Material.PODZOL,
        Material.ROOTED_DIRT,
        Material.SAND,
        Material.RED_SAND,
        Material.GRAVEL,
        Material.SOUL_SAND,
        Material.CLAY
    };

    private static final Map<Material, Material> CROPS_LOOKUPS = new HashMap<>() {{
        put(Material.WHEAT, Material.WHEAT);
        put(Material.CARROTS, Material.CARROT);
        put(Material.POTATOES, Material.POTATO);
        put(Material.BEETROOTS, Material.BEETROOT);
    }};

    private static Map<String, List<Material>> data = LookupFactory.generateValidMaterials("ORE");

    static {
        data.put("STONE", Arrays.asList(STONE_ARRAY));
        data.put("DIRT", Arrays.asList(DIRT_ARRAY));
    }

    public static Map<Material, Material> getCropLookups()
    {
        return CROPS_LOOKUPS;
    }
    
    public static boolean isOre(Material mat)
    {
        return data.get("ORE").contains(mat);
    }

    public static boolean isStone(Material mat)
    {
        return data.get("STONE").contains(mat);
    }

    public static boolean isDirt(Material mat)
    {
        return data.get("DIRT").contains(mat);
    }

    public static boolean isCrop(Material mat)
    {
        return CROPS_LOOKUPS.containsKey(mat);
    }
}
