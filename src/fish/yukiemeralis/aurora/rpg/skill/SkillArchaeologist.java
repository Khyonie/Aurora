package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.eden.utils.Option;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillArchaeologist extends AbstractSkill<BlockBreakEvent>
{
    protected SkillArchaeologist() 
    {
        super(AuroraSkill.ARCHAEOLOGIST, BlockBreakEvent.class);
    }

    @Override
    protected Option<SkillResult> shouldActivate(BlockBreakEvent event, Player player)
    {
        Option<SkillResult> data = new Option<>(SkillResult.class);
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

        if (!RpgItemLookups.isOfType("SHOVEL", held.getType()))
            return data.some(new SkillResult(false, false));

        if (!RpgBlockLookups.isDirt(event.getBlock().getType()))
            return data.some(new SkillResult(false, false));

        if (held.containsEnchantment(Enchantment.SILK_TOUCH))
            return data.some(new SkillResult(false, false));

        return data.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        PrintUtils.sendMessage(event.getPlayer(), "You excavated a rarity!");
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgItemLookups.randomRarity(), 1));

        return new Tuple2<>(false, true);
    }
    
}
