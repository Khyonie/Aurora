package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import fish.yukiemeralis.aurora.rpg.RpgStatListener;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillNinjaTraining extends AbstractSkill
{
    protected SkillNinjaTraining() 
    {
        super(AuroraSkill.NINJA_TRAINING, EntityTargetEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(Event event) 
    {
        EntityTargetEvent e = (EntityTargetEvent) event;

        if (e.getReason().equals(TargetReason.TEMPT))
            return new Tuple2<>(false, false);

        RpgStatListener.blindMob((Mob) e.getEntity(), (Player) e.getTarget(), 40);

        return new Tuple2<>(true, true);
    }
}
