package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.RpgStatListener;
import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.Option;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillDazeMob extends AbstractSkill<EntityDamageByEntityEvent>
{
    protected SkillDazeMob() 
    {
        super(AuroraSkill.DAZE_MOB, EntityDamageByEntityEvent.class);
    }

    @Override
    protected Option<SkillResult> shouldActivate(EntityDamageByEntityEvent event, Player player)
    {
        Option<SkillResult> data = new Option<>(SkillResult.class);

        ItemStack held = ((Player) event.getDamager()).getInventory().getItemInMainHand();    

        if (!held.getType().equals(Material.AIR))
            return data.some(new SkillResult(false, false));

        return data.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDamageByEntityEvent event) 
    {
        RpgStatListener.blindMob((Mob) event.getEntity(), (Player) event.getEntity(), TargetReason.CUSTOM, 100);
        PrintUtils.sendMessage(event.getDamager(), "Enemy dazed!");
        
        return new Tuple2<>(false, false);
    }   
}