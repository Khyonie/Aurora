package fish.yukiemeralis.aurora.autocrafter;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.module.java.annotations.Unimplemented;
import fish.yukiemeralis.eden.utils.PrintUtils;

@Unimplemented("Proof-of-concept for turning a chest inventory into an Autocrafter.")
/** This class is a proof-of-concept that a chest inventory can be turned into a pseudo-crafting table */
public class TestListener implements Listener
{
    @EventHandler
    public void onChestInteract(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null)
            return;

        if (!event.getClickedBlock().getType().equals(Material.CHEST))
            return;

        if (!event.getPlayer().isSneaking())
            return;

        Chest chest = (Chest) event.getClickedBlock().getState();
        ItemStack[] data = new ItemStack[9];

        for (int i = 0; i < 9; i++)
        {
            data[i] = chest.getBlockInventory().getItem(i);
        }

        ItemStack item = Eden.getInstance().getServer().craftItem(data, event.getPlayer().getWorld(), event.getPlayer());
        event.getPlayer().getInventory().addItem(item);

        PrintUtils.sendMessage(event.getPlayer(), "Crafted.");
    }   
}
