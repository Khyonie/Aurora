package fish.yukiemeralis.aurora.rpg.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.eden.utils.option.Option;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillVeinMiner extends AbstractSkill<BlockBreakEvent> 
{
    public SkillVeinMiner() 
    {
        super(AuroraSkill.VEIN_MINER, BlockBreakEvent.class);
    }

    @Override
    protected Option shouldActivate(BlockBreakEvent event, Player player)
    {
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

        if (!RpgItemLookups.isOfType("PICKAXE", held.getType()))
            return Option.some(new SkillResult(false, false));

        if (!RpgBlockLookups.isOre(event.getBlock().getType()))
            return Option.some(new SkillResult(false, false));

        if (held.containsEnchantment(Enchantment.SILK_TOUCH))
            return Option.some(new SkillResult(false, false));

        return Option.none();
    }

    private static List<Block> LOCKED_BLOCKS = new ArrayList<>();

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        if (LOCKED_BLOCKS.contains(event.getBlock()))
            return new Tuple2<>(false, false);

        List<Block>
            open = new ArrayList<>(),
            closed = new ArrayList<>();
            
        World world = event.getBlock().getWorld();
        Block host = event.getBlock();
        
        open.add(host);

        double 
            startX = host.getLocation().getX(), 
            startY = host.getLocation().getY(), 
            startZ = host.getLocation().getZ();

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

                        if (!RpgBlockLookups.isOre(targetted.getType()) || open.contains(targetted) || closed.contains(targetted))
                            continue;

                        open.add(targetted);
                    }

            closed.add(current);
            open.remove(current);
        }

        synchronized (LOCKED_BLOCKS)
        {
            LOCKED_BLOCKS.addAll(closed);
        }

        closed.forEach(block -> event.getPlayer().breakBlock(block));       
        
        synchronized (LOCKED_BLOCKS)
        {
            LOCKED_BLOCKS.removeAll(closed);
        }

        return new Tuple2<Boolean,Boolean>(false, false);
    }
    
}
