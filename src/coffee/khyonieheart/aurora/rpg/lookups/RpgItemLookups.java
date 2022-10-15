package coffee.khyonieheart.aurora.rpg.lookups;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;

public class RpgItemLookups 
{
    private static Map<String, List<Material>>  data = LookupFactory.generateValidMaterials("SWORD", "AXE", "PICKAXE", "SHOVEL");
    private static final Random random = new Random();    

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

    private static final Map<Material, Material> CROP_SEEDS = new HashMap<>() {{
        put(Material.WHEAT, Material.WHEAT_SEEDS);
        put(Material.CARROTS, Material.CARROT);
        put(Material.POTATOES, Material.POTATO);
        put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        put(Material.NETHER_WART, Material.NETHER_WART);
    }};

    public static boolean isOfType(String category, Material mat)
    {
        if (!data.containsKey(category))
            throw new IllegalArgumentException("Unknown RPG item lookup category \"" + category + "\"");
        return data.get(category).contains(mat);
    }

    public static boolean isSeededCrop(Material mat)
    {
        return CROP_SEEDS.containsKey(mat);
    }

    public static Material getSeed(Material mat)
    {
        return CROP_SEEDS.get(mat);
    }

    public static Material randomRarity()
    {
        return RARITIES[random.nextInt(RARITIES.length)];
    }
}
