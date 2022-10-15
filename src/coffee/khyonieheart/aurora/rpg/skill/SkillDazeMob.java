package coffee.khyonieheart.aurora.rpg.skill;

import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.aurora.rpg.RpgStatListener;
import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillDazeMob extends AbstractSkill<EntityDamageByEntityEvent>
{
    public SkillDazeMob() 
    {
        super(AuroraSkill.DAZE_MOB, EntityDamageByEntityEvent.class);
    }

    @Override
    protected Option shouldActivate(EntityDamageByEntityEvent event, Player player)
    {
        ItemStack held = ((Player) event.getDamager()).getInventory().getItemInMainHand();    

        if (!held.getType().equals(Material.AIR))
            return Option.some(new SkillResult(false, false));

        return Option.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDamageByEntityEvent event) 
    {
        RpgStatListener.blindMob((Mob) event.getEntity(), (Player) event.getEntity(), TargetReason.CUSTOM, 100);
        PrintUtils.sendMessage(event.getDamager(), "Enemy dazed!");
        
        return new Tuple2<>(false, false);
    }   
}