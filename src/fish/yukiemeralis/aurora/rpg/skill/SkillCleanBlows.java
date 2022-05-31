package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillCleanBlows extends AbstractSkill<EntityDamageByEntityEvent>
{
    public SkillCleanBlows() 
    {
        super(AuroraSkill.CLEAN_BLOW, EntityDamageByEntityEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDamageByEntityEvent event) 
    {
        event.setDamage(event.getDamage() * 2);
        ((Player) event.getDamager()).playSound(event.getDamager().getLocation(), Sound.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 0.5f, 1.0f);
        return new Tuple2<>(false, false);
    }
}
