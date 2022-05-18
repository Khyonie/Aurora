package fish.yukiemeralis.aurora.rpg.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillSwanSong extends AbstractSkill<EntityDamageEvent>
{
    protected SkillSwanSong() 
    {
        super(AuroraSkill.SWANSONG, EntityDamageEvent.class);
    }

    private static List<Player> 
        SWANSONG_PLAYERS = new ArrayList<>(),
        SWANSONG_COOLDOWN = new ArrayList<>();

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(EntityDamageEvent event) 
    {
        Player player = (Player) event.getEntity();

        if (SWANSONG_COOLDOWN.contains(player) || SWANSONG_PLAYERS.contains(player))
            return new Tuple2<>(true, false);

        PrintUtils.sendMessage(player, "§cSwan song activated! You have 10 seconds before death.");
        
        synchronized (SWANSONG_PLAYERS)
        {
            SWANSONG_PLAYERS.add(player);
        }

        synchronized (SWANSONG_COOLDOWN)
        {
            SWANSONG_COOLDOWN.add(player);
        }
        
        player.setHealth(1.0);
        player.setInvulnerable(true);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 199, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 199, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 199, 0));

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                player.setInvulnerable(false);
                player.setLastDamageCause(event);
                player.setHealth(0.0);

                synchronized (SWANSONG_PLAYERS)
                {
                    SWANSONG_PLAYERS.remove(player);
                }
            }
        }.runTaskLater(Eden.getInstance(), 10*20);

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                synchronized (SWANSONG_COOLDOWN)
                {
                    SWANSONG_COOLDOWN.remove(player);
                    PrintUtils.sendMessage(player, "Swansong has finished cooldown.");
                }
            }
        }.runTaskLater(Eden.getInstance(), 300*20);

        return new Tuple2<>(true, false);
    }
    
}
