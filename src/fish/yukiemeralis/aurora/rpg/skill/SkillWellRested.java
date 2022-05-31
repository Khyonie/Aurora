package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.event.player.PlayerBedLeaveEvent;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

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
