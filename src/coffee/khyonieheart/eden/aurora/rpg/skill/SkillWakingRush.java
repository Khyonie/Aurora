package coffee.khyonieheart.eden.aurora.rpg.skill;

import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import coffee.khyonieheart.eden.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillWakingRush extends AbstractSkill<PlayerRespawnEvent> 
{
    public SkillWakingRush() 
    {
        super(AuroraSkill.WAKING_RUSH, PlayerRespawnEvent.class);
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(PlayerRespawnEvent event) 
    {
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 0));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300*20, 0));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300*20, 0));

        PrintUtils.sendMessage(event.getPlayer(), "You feel invigorated.");
        
        return new Tuple2<>(false, false);
    }
}