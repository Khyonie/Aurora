package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.event.player.PlayerItemMendEvent;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillDoubleMend extends AbstractSkill<PlayerItemMendEvent> 
{
    public SkillDoubleMend() 
    {
        super(AuroraSkill.DOUBLE_MEND, PlayerItemMendEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(PlayerItemMendEvent event) 
    {
        event.setRepairAmount(event.getRepairAmount() * 2);

        return new Tuple2<>(false, false);
    }
    
}
