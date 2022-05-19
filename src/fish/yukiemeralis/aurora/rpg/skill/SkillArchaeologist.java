package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillArchaeologist extends AbstractSkill<BlockBreakEvent>
{

    protected SkillArchaeologist() 
    {
        super(AuroraSkill.ARCHAEOLOGIST, BlockBreakEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

        if (!RpgItemLookups.isOfType("SHOVEL", held.getType()))
            return new Tuple2<>(false, false);

        if (!RpgBlockLookups.isDirt(event.getBlock().getType()))
            return new Tuple2<>(false, false);

        if (held.containsEnchantment(Enchantment.SILK_TOUCH))
            return new Tuple2<>(false, false);

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgItemLookups.randomRarity(), 1));

        return new Tuple2<>(false, true);
    }
    
}
