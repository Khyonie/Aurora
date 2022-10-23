package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.eden.aurora.rpg.SkillResult;
import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.aurora.rpg.lookups.RpgBlockLookups;
import coffee.khyonieheart.eden.aurora.rpg.lookups.RpgItemLookups;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillQuadrupleOres extends AbstractSkill<BlockBreakEvent> 
{
    public SkillQuadrupleOres() 
    {
        super(AuroraSkill.QUADRUPLE_ORES, BlockBreakEvent.class);
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

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        PrintUtils.sendMessage(event.getPlayer(), "4x the resources were dropped!");
        for (int i = 0; i < 3; i++)
            for (ItemStack item : event.getBlock().getDrops())
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        return new Tuple2<>(false, true);
    }
    
}
