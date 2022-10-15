package coffee.khyonieheart.aurora.rpg.skill;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillSwanSong extends AbstractSkill<EntityDamageEvent> implements Listener
{
    public SkillSwanSong() 
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
            return new Tuple2<>(false, false);

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
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 199, 0 + skill.getLevel(player)));
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
        }.runTaskLater(Eden.getInstance(), (1200*20) - ((skill.getLevel(player) - 1) * (300 * 20))); // Cooldown = 20m - (level * 5)

        return new Tuple2<>(true, false);
    }
    
    // Handle players disconnecting while under swansong, so they aren't invulnerable. 
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
       
        synchronized (SWANSONG_PLAYERS)
        {
            if (SWANSONG_PLAYERS.contains(player))
            {
                player.setInvulnerable(false);
                player.setHealth(0.0);
            }

            SWANSONG_PLAYERS.remove(player);
        }
    }
}
