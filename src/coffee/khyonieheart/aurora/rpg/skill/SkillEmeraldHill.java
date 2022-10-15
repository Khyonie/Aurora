package coffee.khyonieheart.aurora.rpg.skill;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.aurora.rpg.lookups.RpgBlockLookups;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillEmeraldHill extends AbstractSkill<BlockBreakEvent>
{
    private static final Random random = new Random();

    public SkillEmeraldHill() 
    {
        super(AuroraSkill.EMERALD_HILL, BlockBreakEvent.class);
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
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.0f);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD, 1 + random.nextInt(3)));

        return new Tuple2<>(false, false);
    }
}
