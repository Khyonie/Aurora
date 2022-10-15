package coffee.khyonieheart.aurora.rpg.skill;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.aurora.rpg.lookups.RpgBlockLookups;
import coffee.khyonieheart.aurora.rpg.lookups.RpgItemLookups;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillArchaeologist extends AbstractSkill<BlockBreakEvent>
{
    public SkillArchaeologist() 
    {
        super(AuroraSkill.ARCHAEOLOGIST, BlockBreakEvent.class);
    }

    @Override
    protected Option shouldActivate(BlockBreakEvent event, Player player)
    {
        ItemStack held = event.getPlayer().getInventory().getItemInMainHand();

        if (!RpgItemLookups.isOfType("SHOVEL", held.getType()))
            return Option.some(new SkillResult(false, false));

        if (!RpgBlockLookups.isDirt(event.getBlock().getType()))
            return Option.some(new SkillResult(false, false));

        if (held.containsEnchantment(Enchantment.SILK_TOUCH))
            return Option.some(new SkillResult(false, false));

        return Option.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        PrintUtils.sendMessage(event.getPlayer(), "You excavated a rarity!");
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgItemLookups.randomRarity(), 1));

        return new Tuple2<>(false, true);
    }
    
}
