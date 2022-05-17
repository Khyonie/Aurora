package fish.yukiemeralis.aurora.rpg;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;

public class RpgBlockTypes 
{
    private static final Random random = new Random();

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

    private static final Material[] RARITIES = new Material[] {
        Material.BONE,
        Material.BONE_MEAL,
        Material.BONE_BLOCK,
        Material.POTATO,
        Material.CARROT,
        Material.BEETROOT,
        Material.SKELETON_SKULL,
        Material.WITHER_SKELETON_SKULL,
        Material.WITHER_ROSE,
        Material.GOLD_NUGGET,
        Material.GOLD_INGOT,
        Material.IRON_NUGGET,
        Material.IRON_INGOT,
        Material.DIAMOND,
        Material.EMERALD
    };

    static final Map<Material, Object> STONE_LOOKUPS = new HashMap<>() {{
       for (Material m : STONE_ARRAY) 
           put(m, null);
    }};

    static final Map<Material, Object> DIRT_LOOKUPS = new HashMap<>() {{
        for (Material m : DIRT_ARRAY)
            put(m, null);
    }};

    static final Map<Material, Material> CROPS_LOOKUPS = new HashMap<>() {{
        put(Material.WHEAT, Material.WHEAT);
        put(Material.CARROTS, Material.CARROT);
        put(Material.POTATOES, Material.POTATO);
        put(Material.BEETROOTS, Material.BEETROOT);
    }};

    public static Material getRandomRarity()
    {
        return RARITIES[random.nextInt(RARITIES.length)];
    }
}
