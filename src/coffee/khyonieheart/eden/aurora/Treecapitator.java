package coffee.khyonieheart.eden.aurora;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.utils.PrintUtils;

public class Treecapitator implements Listener
{
    private static final String[] AXE_MATERIALS = {"WOODEN", "STONE", "IRON", "GOLDEN", "DIAMOND", "NETHERITE"};
    private static final List<Material> AXE_TYPES = new ArrayList<>() {{
        for (String mat : AXE_MATERIALS)
            add(Material.valueOf(mat + "_AXE"));
    }};

    private static final String[] LOG_MATERIALS = {"OAK", "BIRCH", "JUNGLE", "ACACIA", "SPRUCE", "DARK_OAK", "MANGROVE"};
    private static final List<Material> LOG_TYPES = new ArrayList<>() {{
        for (String mat : LOG_MATERIALS)
            add(Material.valueOf(mat + "_LOG"));

        add(Material.CRIMSON_STEM);
        add(Material.WARPED_STEM);
    }};

    private static final List<Material> LEAF_TYPES = new ArrayList<>() {{
        for (String mat : LOG_MATERIALS)
            add(Material.valueOf(mat + "_LEAVES"));

        add(Material.WARPED_WART_BLOCK);
        add(Material.NETHER_WART_BLOCK);
        add(Material.MANGROVE_ROOTS);
    }};

    private static final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!LOG_TYPES.contains(event.getBlock().getType()))
            return;

        if (event.getPlayer() == null)
            return;
        
        if (!Eden.getPermissionsManager().getPlayerData(event.getPlayer()).getModuleData("Aurora").getValue("treecapEnabled", Boolean.class))
            return;

        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItem(EquipmentSlot.HAND);

        if (!AXE_TYPES.contains(held.getType()))
            return;

        /*
         * We handle this very similarly to Dijkstra's pathfinding algorithm
         * https://en.wikipedia.org/wiki/Dijkstra's_algorithm
         * 
         * 1) "Seed" the first log block, adding it to the open set
         * 2) While open still has blocks...
         * 3) Look at all blocks adjacent and diagonal to the given block, adding new logs* to the open set. Add the current block to the closed set
         * 4) Break all blocks in the closed set
         * 
         * *Leaves, netherwart blocks, and twisted wart blocks are all added to closed, but not open. This results in all adjacent "leaf" type blocks being destroyed.
         */ 

        List<Block> 
            open = new ArrayList<>(),
            closed = new ArrayList<>();

        // Seed open
        open.add(event.getBlock());
        int logsMarked = 1;

        World world = event.getBlock().getWorld();
        Block host = event.getBlock();
        double 
            startX = host.getLocation().getX(), 
            startY = host.getLocation().getY(), 
            startZ = host.getLocation().getZ();


        // And perform pathfinding around the tree
        // Leaves are marked for destruction, but aren't added to open
        Block current = host;
        while (!open.isEmpty())
        {
            current = open.get(0);

            startX = current.getLocation().getX(); 
            startY = current.getLocation().getY(); 
            startZ = current.getLocation().getZ();

            for (int iy = -1; iy < 2; iy++)
                for (int ix = -1; ix < 2; ix++) 
                    for (int iz = -1; iz < 2; iz++)
                    {
                        Block targetted = world.getBlockAt((int) Math.round(startX + ix), (int) Math.round(startY + iy), (int) Math.round(startZ + iz));

                        if (LEAF_TYPES.contains(targetted.getType()) && !closed.contains(targetted))
                        {
                            closed.add(targetted);
                            continue;
                        }

                        if (!LOG_TYPES.contains(targetted.getType()) || open.contains(targetted) || closed.contains(targetted))
                            continue;

                        logsMarked++;
                        open.add(targetted);
                    }

            closed.add(current);
            open.remove(current);
        }
        
        // And destroy all marked blocks
        closed.forEach(block -> {
            block.breakNaturally();
        });

        Damageable meta = (Damageable) held.getItemMeta();
        int unbreakingLevel = meta.getEnchantLevel(Enchantment.DURABILITY);

        if (meta.getDamage() == held.getType().getMaxDurability())
        {
            // Final break
            if (random.nextInt(unbreakingLevel + 1) != 0) // Roll for break vs unbreaking level
                return;

            event.getPlayer().getEquipment().setItem(EquipmentSlot.HAND, null);
            return;
        }

        // Damage item
        int finalDamage = logsMarked / (1 + unbreakingLevel); // Simulate unbreaking
        meta.setDamage(meta.getDamage() + finalDamage);

        // Add a grace use
        if (meta.getDamage() >= held.getType().getMaxDurability())
        {
            PrintUtils.sendMessage(event.getPlayer(), "Your axe has 1 use left.");
            meta.setDamage(held.getType().getMaxDurability());
        }

        held.setItemMeta(meta);
    }
}
