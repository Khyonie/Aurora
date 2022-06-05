package fish.yukiemeralis.aurora;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameListener implements Listener 
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRename(PrepareAnvilEvent event)
    {
        // Can't rename air
        if (event.getResult() == null)
            return;

        // Can't rename something with no meta
        if (event.getResult().getItemMeta() == null)
            return;

        // Can't rename if we're not renaming something
        if (event.getInventory().getRenameText().equals(""))
            return;

        // Make sure we're not combining something
        if (event.getInventory().getItem(1) != null)
            if (!event.getInventory().getItem(1).getType().equals(Material.AIR))
                return;

        // Perform the rename
        ItemStack item = event.getResult();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(meta.getDisplayName().replace("&", "§"));
        item.setItemMeta(meta);

        event.setResult(item);
    
        // Set repair cost to 0
        event.getInventory().setRepairCost(0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRepair(PrepareAnvilEvent event)
    {
        event.getInventory().setMaximumRepairCost(Integer.MAX_VALUE); // Effectively delete the "Too expensive!" message, allowing for custom costs no matter what

        if (event.getInventory().getRepairCost() > 30)
            event.getInventory().setRepairCost(30);
    }
}
