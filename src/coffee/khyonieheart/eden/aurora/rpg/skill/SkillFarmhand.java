package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.eden.aurora.rpg.SkillResult;
import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.aurora.rpg.lookups.RpgBlockLookups;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillFarmhand extends AbstractSkill<BlockBreakEvent>
{
    public SkillFarmhand() 
    {
        super(AuroraSkill.FARMHAND, BlockBreakEvent.class);
    }

    @Override
    protected Option shouldActivate(BlockBreakEvent event, Player player)
    {
        if (!RpgBlockLookups.isCrop(event.getBlock().getType()))
            return Option.some(new SkillResult(false, false));

        if (((Ageable) event.getBlock().getBlockData()).getAge() < ((Ageable) event.getBlock().getBlockData()).getMaximumAge())
            return Option.some(new SkillResult(false, false));

        return Option.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.BLOCKS, 0.5f, 1.0f);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgBlockLookups.getCropLookups().get(event.getBlock().getType()), 3));

        return new Tuple2<>(false, false);
    }
}