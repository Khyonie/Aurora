package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillFarmhand extends AbstractSkill<BlockBreakEvent>
{
    protected SkillFarmhand() 
    {
        super(AuroraSkill.FARMHAND, BlockBreakEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        if (!RpgBlockLookups.isCrop(event.getBlock().getType()))
            return new Tuple2<>(false, false);

        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.BLOCKS, 0.5f, 1.0f);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgBlockLookups.getCropLookups().get(event.getBlock().getType()), 3));

        return new Tuple2<>(false, false);
    }
}