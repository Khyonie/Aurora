package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import coffee.khyonieheart.eden.aurora.rpg.RpgStatListener;
import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillNinjaTraining extends AbstractSkill<EntityTargetEvent>
{
    public SkillNinjaTraining() 
    {
        super(AuroraSkill.NINJA_TRAINING, EntityTargetEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityTargetEvent event) 
    {         
        RpgStatListener.blindMob((Mob) event.getEntity(), (Player) event.getTarget(), event.getReason(), 40);

        return new Tuple2<>(true, true);
    }
}
