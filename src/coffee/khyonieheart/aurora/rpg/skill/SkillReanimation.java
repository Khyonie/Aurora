package coffee.khyonieheart.aurora.rpg.skill;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillReanimation extends AbstractSkill<EntityDeathEvent>
{
    public SkillReanimation() 
    {
        super(AuroraSkill.REANIMATION, EntityDeathEvent.class);
    }

    @Override
    protected Option shouldActivate(EntityDeathEvent event, Player player)
    {
        if (event.getEntity() == null)
            return Option.some(SkillResult.of(false, true));

       return Option.none(); 
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDeathEvent event) 
    {
        return null;
    }
    
}
