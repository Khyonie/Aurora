package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillQuadrupleOres extends AbstractSkill<BlockBreakEvent> 
{

    protected SkillQuadrupleOres() 
    {
        super(AuroraSkill.QUADRUPLE_ORES, BlockBreakEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

        if (!RpgItemLookups.isOfType("PICKAXE", held.getType()))
            return new Tuple2<>(false, false);

        if (!RpgBlockLookups.isOre(event.getBlock().getType()))
            return new Tuple2<>(false, false);

        if (held.containsEnchantment(Enchantment.SILK_TOUCH))
            return new Tuple2<>(false, false);

        PrintUtils.sendMessage(event.getPlayer(), "4x the resources were dropped!");
        for (int i = 0; i < 3; i++)
            for (ItemStack item : event.getBlock().getDrops())
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        return new Tuple2<>(false, true);
    }
    
}
