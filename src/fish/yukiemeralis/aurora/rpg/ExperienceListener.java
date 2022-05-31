package fish.yukiemeralis.aurora.rpg;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.aurora.rpg.lookups.RpgBlockLookups;
import fish.yukiemeralis.aurora.rpg.lookups.RpgItemLookups;

public class ExperienceListener implements Listener
{ 
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player))
            return;

        ItemStack held = ((Player) event.getDamager()).getEquipment().getItemInMainHand();

        if (held == null)
        {
            RpgStat.BRAWLING.increaseExp((Player) event.getDamager());
            return;
        }

        if (held.getType().equals(Material.AIR))
        {
            RpgStat.BRAWLING.increaseExp((Player) event.getDamager());
            return;
        }

        if (RpgItemLookups.isOfType("SWORD", held.getType()))
        {
            RpgStat.SWORDS.increaseExp((Player) event.getDamager());
            return;
        }

        if (RpgItemLookups.isOfType("AXE", held.getType()))
        {
            RpgStat.AXES.increaseExp((Player) event.getDamager());
            return;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        ItemStack held = event.getPlayer().getEquipment().getItemInMainHand();

        if (held == null)
            return;

        if (RpgItemLookups.isOfType("PICKAXE", held.getType()) && RpgBlockLookups.isStone(event.getBlock().getType()))
        {
            RpgStat.MINING.increaseExp(event.getPlayer());
            return;
        }

        if (RpgItemLookups.isOfType("SHOVEL", held.getType()) && RpgBlockLookups.isDirt(event.getBlock().getType()))
        {
            RpgStat.DIGGING.increaseExp(event.getPlayer());
            return;
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (event.getHitEntity() == null)
            return;

        if (!(event.getEntity() instanceof Arrow))
            return;

        if (event.getHitEntity().equals((Player) event.getEntity().getShooter()))
            return;

        RpgStat.ARCHERY.increaseExp((Player) event.getEntity().getShooter());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        if (event.getPlayer().isClimbing() || event.getPlayer().isGliding() || event.getPlayer().isFlying() || event.getPlayer().isInWater() || event.getPlayer().isInsideVehicle() || event.getPlayer().isRiptiding() || event.getPlayer().isSwimming())
            return;

        if (event.getPlayer().isSneaking())
        {
            RpgStat.STEALTH.increaseExp(event.getPlayer());
            return;
        }

        RpgStat.WALKING.increaseExp(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        AuroraRpgStats.removeBar(event.getPlayer());
    }
}
