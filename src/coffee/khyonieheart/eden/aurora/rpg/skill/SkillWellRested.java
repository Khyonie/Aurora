package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.event.player.PlayerBedLeaveEvent;

import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillWellRested extends AbstractSkill<PlayerBedLeaveEvent>
{

    public SkillWellRested()
    {
        super(AuroraSkill.WELL_RESTED, PlayerBedLeaveEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(PlayerBedLeaveEvent event) 
    {
        return new Tuple2<>(false, false);
    }
    
}
