package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.utils.Option;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillAutoReplant extends AbstractSkill<BlockBreakEvent> 
{
    public SkillAutoReplant() 
    {
        super(AuroraSkill.AUTO_REPLANT, BlockBreakEvent.class);
    }

    public Option<SkillResult> shouldActivate(BlockBreakEvent event, Player player)
    {
        Option<SkillResult> option = new Option<>(SkillResult.class);

        if (!RpgItemLookups.isSeededCrop(event.getBlock().getType()))
            return option.some(new SkillResult(false, false));

        if (((Ageable) event.getBlock().getBlockData()).getAge() < ((Ageable) event.getBlock().getBlockData()).getMaximumAge())
            return option.some(new SkillResult(false, false));

        if (event.getPlayer().getInventory().first(RpgItemLookups.getSeed(event.getBlock().getType())) == -1)
            return option.some(new SkillResult(false, false));

        return option.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        Material type = event.getBlock().getType();
        Material seed = RpgItemLookups.getSeed(type);     

        int targetSlot = event.getPlayer().getInventory().first(seed);

        // Reset state
        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                event.getBlock().setType(type);
            Ageable data = (Ageable) event.getBlock().getBlockData();
            data.setAge(0);
            event.getBlock().setBlockData(data);
            }
        }.runTaskLater(Eden.getInstance(), 1L);

        // Remove a seed
        event.getPlayer().getInventory().getItem(targetSlot).setAmount(event.getPlayer().getInventory().getItem(targetSlot).getAmount() - 1);

        return new Tuple2<Boolean, Boolean>(false, false);
    }
    
}
