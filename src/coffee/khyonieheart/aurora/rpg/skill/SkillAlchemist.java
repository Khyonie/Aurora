package coffee.khyonieheart.aurora.rpg.skill;

import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillAlchemist extends AbstractSkill<ProjectileLaunchEvent>
{
    public SkillAlchemist() 
    {
        super(AuroraSkill.ALCHEMIST, ProjectileLaunchEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(ProjectileLaunchEvent event) 
    {
        ItemStack item = ((ThrownPotion) event.getEntity()).getItem();

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                ((Player) event.getEntity().getShooter()).getInventory().addItem(item);
            }
        }.runTaskLater(Eden.getInstance(), 5*20);
        return new Tuple2<>(false, false);
    }
    
}
