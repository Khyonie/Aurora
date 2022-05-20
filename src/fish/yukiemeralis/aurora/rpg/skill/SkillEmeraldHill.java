package fish.yukiemeralis.aurora.rpg.skill;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillEmeraldHill extends AbstractSkill<BlockBreakEvent>
{
    private static final Random random = new Random();

    protected SkillEmeraldHill() 
    {
        super(AuroraSkill.EMERALD_HILL, BlockBreakEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        if (!RpgBlockLookups.isCrop(event.getBlock().getType()))
            return new Tuple2<>(false, false);
        
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.0f);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD, 1 + random.nextInt(3)));

        return new Tuple2<>(false, false);
    }
}
