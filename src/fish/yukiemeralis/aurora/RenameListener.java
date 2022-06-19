package fish.yukiemeralis.aurora;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fish.yukiemeralis.eden.Eden;

public class RenameListener implements Listener 
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRename(PrepareAnvilEvent event)
    {
        // PrintUtils.log("§f######### §eNEW EVENT §f#########");

        // Can't rename air
        if (event.getResult() == null)
        {
            // PrintUtils.log("§bRENAME \\>§7 Cannot rename air. Returning.");
            return;
        }
        // PrintUtils.log("§9RENAME \\>§7 Result is not air.");

        // Can't rename something with no meta
        if (event.getResult().getItemMeta() == null)
        {
            // PrintUtils.log("§bRENAME \\>§7 Cannot rename item with no meta. Returning.");
            return;
        }        
        // PrintUtils.log("§9RENAME \\>§7 Item has meta.");

        // Can't rename if we're not renaming something
        if (event.getInventory().getRenameText().equals(""))
        {
            // PrintUtils.log("§bRENAME \\>§7 Not attempting to rename. Returning.");
            return;
        }
        // PrintUtils.log("§9RENAME \\>§7 Attempting to rename.");

        // Make sure we're not combining something
        if (event.getInventory().getItem(1) != null)
            if (!event.getInventory().getItem(1).getType().equals(Material.AIR))
            {
                // PrintUtils.log("§bRENAME \\>§7 Attempting to combine. Returning.");
                return;
            }
        // PrintUtils.log("§9RENAME \\>§7 Not attempting to combine.");

        // Perform the rename
        ItemStack item = event.getResult();
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(meta.getDisplayName().replace("&", "§"));
        item.setItemMeta(meta);

        event.setResult(item);
    
        // Set repair cost to 0
        event.getInventory().setRepairCost(0);
        // PrintUtils.log("§aRENAME \\>§7 ITEM COST IS NOW 0");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRepair(PrepareAnvilEvent event)
    {
        event.getInventory().setMaximumRepairCost(Integer.MAX_VALUE); // Effectively delete the "Too expensive!" message, allowing for custom costs no matter what

        if (event.getInventory().getRepairCost() > 30)
            Bukkit.getScheduler().runTask(Eden.getInstance(), () -> event.getInventory().setRepairCost(30));

        // PrintUtils.log("§dCOST \\>§f Final EXP cost is " + event.getInventory().getRepairCost());
    }
}
