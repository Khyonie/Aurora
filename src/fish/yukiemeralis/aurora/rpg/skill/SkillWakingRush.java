package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillWakingRush extends AbstractSkill<PlayerRespawnEvent> 
{
    protected SkillWakingRush() 
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