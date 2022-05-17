package fish.yukiemeralis.aurora.pylons.item;

import fish.yukiemeralis.aurora.pylons.PylonGui;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.recipe.RecipeManager;
import fish.yukiemeralis.eden.utils.ItemUtils;

public class Pylporter implements Listener
{
    private static final ItemStack item = ItemUtils.build(
        Material.PRISMARINE_SHARD, 
        "§r§b§lPylporter", 
        "§r§7§oTeleport to an active pylon.",
        "",
        "§r§7§oSingle use."
    ); 

    private static final String[] recipedata = {
        "cCc",
        "cgc",
        "ccc"
    };

    private static NamespacedKey key;
    private static ShapedRecipe recipe;

    public static void register()
    {
        key = new NamespacedKey(Eden.getInstance(), "pylporter");
        recipe = new ShapedRecipe(key, item);

        recipe.shape(recipedata);

        recipe.setIngredient('c', Material.COBBLESTONE);
        recipe.setIngredient('C', Material.COAL);
        recipe.setIngredient('g', Material.GLASS);

        ItemUtils.saveToNamespacedKey(item, "pylporterIdentity", "pylporter");
        RecipeManager.addRecipe(new NamespacedKey(Eden.getInstance(), "pylporter"), recipe, true);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event)
    {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR))
            return;

        ItemStack primaryheld = event.getPlayer().getEquipment().getItemInMainHand();
        ItemStack secondaryheld = event.getPlayer().getEquipment().getItemInOffHand();

        if (!isPylporterItem(primaryheld) && !isPylporterItem(secondaryheld))
            return;

        new PylonGui(event.getPlayer()).display();
    }

    public static boolean isPylporterItem(ItemStack item)
    {
        if (item == null)
            return false;

        if (item.getType().equals(Material.AIR))
            return false;

        return item.getItemMeta().getDisplayName().equals("§b§lPylporter");
    }

    public static void deregister()
    {
        RecipeManager.removeRecipe(key);
    }
    
    public static ItemStack getItemInstance()
    {
        return item;
    }
}
