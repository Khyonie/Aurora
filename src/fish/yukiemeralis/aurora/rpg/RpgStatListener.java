package fish.yukiemeralis.aurora.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;
import fish.yukiemeralis.aurora.rpg.skill.AbstractSkill;
import fish.yukiemeralis.aurora.rpg.skill.SkillRefundArrow;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.permissions.ModulePlayerData;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class RpgStatListener implements Listener
{
    private static final Random random = new Random();
    private static Map<Class<?>, List<AbstractSkill<? extends Event>>> REGISTERED_SKILLS = new HashMap<>();

    public static void register(AbstractSkill<? extends Event> skill, Class<?> event)
    {
        if (!REGISTERED_SKILLS.containsKey(event))
            REGISTERED_SKILLS.put(event, new ArrayList<>());
        REGISTERED_SKILLS.get(event).add(skill);
    }

    //
    // PvE events
    //

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        // Entity hits player
        if (event.getEntity() instanceof Player)
        {
            // This is already ported over to the SkillNinjaTraining skill, so here it remains
            if (AuroraSkill.NINJA_TRAINING.isUnlocked((Player) event.getEntity()))
            {
                if (AuroraSkill.NINJA_TRAINING.proc())
                {
                    event.setCancelled(true);
                    return;
                }
            }

            return;
        }

        // Player hits entity
        if (event.getDamager() instanceof Player)
        {
            ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) event.getDamager()).getModuleData("AuroraRPG");
            ItemStack held = ((Player) event.getDamager()).getInventory().getItemInMainHand(); 

            // Apply damage modifiers

            if (held.getType() == null)
            {
                return;
            }

            if (held.getType().equals(Material.AIR))
            {
                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.BRAWLING.dataName()) / 10d)));
            }

            if (RpgItemLookups.isOfType("AXE", held.getType()))
            {
                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.AXES.dataName()) / 10d)));
            }

            if (RpgItemLookups.isOfType("SWORD", held.getType()))
            {
                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.SWORDS.dataName()) / 10d)));
            }

            if (trySkill(event, (Player) event.getDamager(), AuroraSkill.DAZE_MOB, AuroraSkill.CLEAN_BLOW))
                return;
        }
    }

    
    //
    // Projectile events
    //

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof Arrow))
            return;

        if (trySkill(event, (Player) event.getEntity().getShooter(), AuroraSkill.ARROW_REFUND))
            return;
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof Arrow))
            return;

        if (SkillRefundArrow.isTrackedArrow((Arrow) event.getEntity()))
        {
            SkillRefundArrow.refundArrow((Arrow) event.getEntity());
        }
    }

    @EventHandler
    public void onPotionThrow(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof ThrownPotion))
            return;

        if (trySkill(event, (Player) event.getEntity().getShooter(), AuroraSkill.ALCHEMIST))
            return;
    }

    @EventHandler
    public void onConnect(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Arrow))
            return;

        Projectile projectile = (Projectile) event.getDamager();
        
        if (!(projectile.getShooter() instanceof Player))
            return;

        ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) ((Projectile) event.getDamager()).getShooter()).getModuleData("AuroraRPG");

        // Damage = damage * (1.0 + 0.level)
        event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.ARCHERY.dataName()) / 10d)) * (AuroraSkill.ARROW_REFUND.isUnlocked((Player) ((Projectile) event.getDamager()).getShooter()) ? 1.2 : 1.0));

        if (trySkill(event, (Player) projectile.getShooter())) // Jester, probably
            return;    
    }


    //
    // Entity targeting events
    //

    private static Map<Mob, Player> BLIND_MOB_TRACKER = new HashMap<>(); // Mobs that cannot target a player

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (!(event.getEntity() instanceof Mob))
            return;

        if (!(event.getTarget() instanceof Player))
            return;

        if (BLIND_MOB_TRACKER.containsKey(event.getEntity()))
            if (BLIND_MOB_TRACKER.get(event.getEntity()).equals(event.getTarget()))
            {
                event.setCancelled(true);
                return;
            }

        ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) event.getTarget()).getModuleData("AuroraRPG");

        // Skill
        if (trySkill(event, (Player) event.getTarget(), AuroraSkill.NINJA_TRAINING))
            return;

        // Stealth stat
        if (!((Player) event.getTarget()).isSneaking())
            return;

        // p% = 2*level/100
        if (random.nextInt(100) <= 2 * data.getInt(RpgStat.STEALTH.dataName()))
            blindMob((Mob) event.getEntity(), (Player) event.getTarget(), event.getReason(), 40);
    }

    
    //
    // Death events
    //

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathEvent(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        if (!(((Player) event.getEntity()).getHealth() - event.getDamage() <= 0.0))
        {
            if (trySkill(event, (Player) event.getEntity(), AuroraSkill.SWANSONG))
                return;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (trySkill(event, event.getPlayer(), AuroraSkill.WAKING_RUSH))
            return;
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event)
    {
        if (!AuroraSkill.WELL_RESTED.isUnlocked(event.getPlayer()))
            return;

        if (event.getPlayer().getSleepTicks() >= 20) // TODO Test this
        {
            PrintUtils.sendMessage(event.getPlayer(), "You feel invigorated.");
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 900*20, 1));
        }
    }

    //
    // Block effects
    //

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (trySkill(event, event.getPlayer(), AuroraSkill.QUADRUPLE_ORES, AuroraSkill.ARCHAEOLOGIST, AuroraSkill.EMERALD_HILL, AuroraSkill.FARMHAND))
            return;
    }


    //
    // Misc
    //

    @EventHandler
    public void onMend(PlayerItemMendEvent event)
    {
        if (trySkill(event, event.getPlayer(), AuroraSkill.DOUBLE_MEND))
            return;
    }

    //
    // Helpers
    //

    private static boolean trySkill(Event event, Player target, AuroraSkill... applicableSkills)
    {
        if (!REGISTERED_SKILLS.containsKey(event.getClass()))
            return false;

        for (AbstractSkill<?> skill : REGISTERED_SKILLS.get(event.getClass()))
        {
            PrintUtils.log("Trying for skill " + skill.getEnum().getName() + " as event " + event.getClass().getSimpleName());

            boolean isApplicable = false;
            a: for (AuroraSkill s : applicableSkills)
                if (skill.getEnum().equals(s))
                {
                    isApplicable = true;
                    break a;
                }

            PrintUtils.log("Is applicable? " + isApplicable);

            if (!isApplicable)
                continue;

            PrintUtils.log("Trying for activation");
            Tuple2<Boolean, Boolean> data = skill.tryActivate(event, target);

            if (data.getA())
                if (event instanceof Cancellable)
                    ((Cancellable) event).setCancelled(true);

            if (data.getB())
                return true;
        }

        return false;
    }

    public static void blindMob(Mob mob, Player player, TargetReason reason, long duration)
    {
        if (reason.equals(TargetReason.TEMPT))
            return;

        synchronized (BLIND_MOB_TRACKER)
        {
            BLIND_MOB_TRACKER.put(mob, player);
        }
         
        mob.getWorld().playEffect(mob.getLocation(), Effect.COPPER_WAX_OFF, 0);

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                synchronized (BLIND_MOB_TRACKER)
                {
                    BLIND_MOB_TRACKER.remove(mob);
                }
            }
        }.runTaskLater(Eden.getInstance(), duration);
    }
}