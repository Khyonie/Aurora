package fish.yukiemeralis.aurora.rpg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.permissions.ModulePlayerData;
import fish.yukiemeralis.eden.utils.PrintUtils;

@SuppressWarnings("unused")
public class RpgStatListener implements Listener
{
    private static final Random random = new Random();

    private static final List<Material> ORES = ExperienceListener.getValidMaterials("ORE");

    //
    // PvE events
    //

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event)
    {
        // Entity hits player
        if (event.getEntity() instanceof Player)
        {
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
                if (AuroraSkill.DAZE_MOB.isUnlocked((Player) event.getDamager()))
                    if (AuroraSkill.DAZE_MOB.proc())
                    {
                        if (event.getEntity() instanceof Mob)
                        {
                            blindMob((Mob) event.getEntity(), (Player) event.getEntity(), 100);
                            PrintUtils.sendMessage(event.getDamager(), "Enemy dazed!");
                        }
                    }

                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.BRAWLING.name().toLowerCase()) / 10d)));
                return;
            }

            if (ExperienceListener.AXES.contains(held.getType()))
            {
                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.BRAWLING.name().toLowerCase()) / 10d)));
                return;
            }

            if (ExperienceListener.SWORDS.contains(held.getType()))
            {
                if (AuroraSkill.CLEAN_BLOW.isUnlocked((Player) event.getDamager()))
                    if (AuroraSkill.CLEAN_BLOW.proc())
                        event.setDamage(event.getDamage() * 2);

                event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.SWORDS.name().toLowerCase()) / 10d)));
                return;
            }

            return;
        }
    }

    
    //
    // Projectile events
    //

    private static List<Arrow> REFUND_ARROW_TRACKER = new ArrayList<>();

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof Arrow))
            return;

        ItemStack mainHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInMainHand();
        ItemStack offHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInOffHand();

        if (mainHeld != null)
            if (mainHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return;
        if (offHeld != null)
            if (offHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return;

        if (AuroraSkill.ARROW_REFUND.isUnlocked((Player) event.getEntity().getShooter()))
            if (AuroraSkill.ARROW_REFUND.proc())
                REFUND_ARROW_TRACKER.add((Arrow) event.getEntity());
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof Arrow))
            return;

        if (REFUND_ARROW_TRACKER.contains(event.getEntity()))
        {
            PrintUtils.sendMessage((Player) event.getEntity().getShooter(), "The arrow returned to you!");

            REFUND_ARROW_TRACKER.remove(event.getEntity());

            ((Player) event.getEntity().getShooter()).getInventory().addItem(new ItemStack(Material.ARROW));
            ((Arrow) event.getEntity()).setPickupStatus(PickupStatus.DISALLOWED);
        }
    }

    @EventHandler
    public void onPotionThrow(ProjectileLaunchEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getEntity() instanceof ThrownPotion))
            return;

        ItemStack item = ((ThrownPotion) event.getEntity()).getItem();

        if (AuroraSkill.ALCHEMIST.isUnlocked((Player) event.getEntity().getShooter()))
            new BukkitRunnable() 
            {
                @Override
                public void run() 
                {
                    ((Player) event.getEntity().getShooter()).getInventory().addItem(item);
                }
            }.runTaskLater(Eden.getInstance(), 5*20);
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
        event.setDamage(event.getDamage() * (1.0 + (data.getInt(RpgStat.ARCHERY.name().toLowerCase()) / 10d)));

        if (AuroraSkill.TIPPED_ARROWS.isUnlocked((Player) ((Projectile) event.getDamager()).getShooter()))
        {
            if (AuroraSkill.TIPPED_ARROWS.proc())
            {
                // TODO This
            }
        }
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
        if (data.getValue(AuroraSkill.NINJA_TRAINING.name(), Boolean.class))
        {
            if (random.nextInt(2) == 0) // 50% chance
            {
                blindMob((Mob) event.getEntity(), (Player) event.getTarget(), 40);
                return;
            }
        }

        // Stealth stat
        if (!((Player) event.getTarget()).isSneaking())
            return;

        // p% = 2*level/100
        if (random.nextInt(100) <= 2 * data.getInt(RpgStat.STEALTH.name().toLowerCase()))
            blindMob((Mob) event.getEntity(), (Player) event.getTarget(), 40);
    }

    
    //
    // Death events
    //

    private static List<Player> SWANSONG_PLAYERS = new ArrayList<>();
    private static List<Player> SWANSONG_COOLDOWN = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeathEvent(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player))
            return;

        if (((Player) event.getEntity()).getHealth() - event.getDamage() <= 0.0)
        {
            if (!AuroraSkill.SWANSONG.isUnlocked((Player) event.getEntity()))
                return;

            if (SWANSONG_PLAYERS.contains((Player) event.getEntity()))
                return;

            if (SWANSONG_COOLDOWN.contains((Player) event.getEntity()))
                return;

            event.setCancelled(true);
            onDeath((Player) event.getEntity());
        }
    }

    public void onDeath(Player player)
    { 
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 199, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 199, 0));

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                player.setInvulnerable(false);
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
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        if (!AuroraSkill.WAKING_RUSH.isUnlocked(event.getPlayer()))
            return;

        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 0));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300*20, 0));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300*20, 0));
        PrintUtils.sendMessage(event.getPlayer(), "You feel invigorated.");
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
        if (ORES.contains(event.getBlock().getType()))
        {
            if (AuroraSkill.QUADRUPLE_ORES.isUnlocked(event.getPlayer()))
                if (AuroraSkill.QUADRUPLE_ORES.proc())
                    for (int i = 0; i < 3; i++)
                        for (ItemStack item : event.getBlock().getDrops())
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
            return;
        }

        if (RpgBlockTypes.DIRT_LOOKUPS.containsKey(event.getBlock().getType()))
        {
            if (AuroraSkill.ARCHAEOLOGIST.isUnlocked(event.getPlayer()))
                if (AuroraSkill.ARCHAEOLOGIST.proc())
                    event.getPlayer().getInventory().addItem(new ItemStack(RpgBlockTypes.getRandomRarity()));
            return;
        }

        if (RpgBlockTypes.CROPS_LOOKUPS.containsKey(event.getBlock().getType()))
        {
            if (AuroraSkill.EMERALD_HILL.isUnlocked(event.getPlayer()))
                if (AuroraSkill.EMERALD_HILL.proc())
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD, 1 + random.nextInt(3)));

            if (AuroraSkill.FARMHAND.isUnlocked(event.getPlayer()))
                if (AuroraSkill.FARMHAND.proc())
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(RpgBlockTypes.CROPS_LOOKUPS.get(event.getBlock().getType()), 3));

            return;
        }
    }


    //
    // Misc
    //

    @EventHandler
    public void onMend(PlayerItemMendEvent event)
    {
        if (!AuroraSkill.DOUBLE_MEND.isUnlocked(event.getPlayer()))
            return;

        if (AuroraSkill.DOUBLE_MEND.proc())
            event.setRepairAmount(event.getRepairAmount() * 2);
    }

    //
    // Helpers
    //

    private void blindMob(Mob mob, Player player, long duration)
    {
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