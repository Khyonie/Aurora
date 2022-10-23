package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.aurora.rpg.SkillResult;
import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.aurora.rpg.lookups.RpgItemLookups;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillAutoReplant extends AbstractSkill<BlockBreakEvent> 
{
    public SkillAutoReplant() 
    {
        super(AuroraSkill.AUTO_REPLANT, BlockBreakEvent.class);
    }

    public Option shouldActivate(BlockBreakEvent event, Player player)
    {
        if (!RpgItemLookups.isSeededCrop(event.getBlock().getType()))
            return Option.some(new SkillResult(false, false));

        if (((Ageable) event.getBlock().getBlockData()).getAge() < ((Ageable) event.getBlock().getBlockData()).getMaximumAge())
            return Option.some(new SkillResult(false, false));

        if (event.getPlayer().getInventory().first(RpgItemLookups.getSeed(event.getBlock().getType())) == -1)
            return Option.some(new SkillResult(false, false));

        return Option.none();
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
