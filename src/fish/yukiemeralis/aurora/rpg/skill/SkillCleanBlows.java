package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillCleanBlows extends AbstractSkill<EntityDamageByEntityEvent>
{
    protected SkillCleanBlows() 
    {
        super(AuroraSkill.CLEAN_BLOW, EntityDamageByEntityEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDamageByEntityEvent event) 
    {
        event.setDamage(event.getDamage() * 2);
        return new Tuple2<>(true, true);
    }
    
}
