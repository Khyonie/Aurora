package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.event.player.PlayerItemMendEvent;

import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

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
