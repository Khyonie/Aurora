package fish.yukiemeralis.aurora.rpg;

import java.util.ArrayList;
import java.util.List;

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

public class ExperienceListener implements Listener
{
    static List<Material> 
        SWORDS = getValidMaterials("SWORD"),
        AXES = getValidMaterials("AXE"),
        PICKAXES = getValidMaterials("PICKAXE"),
        SHOVELS = getValidMaterials("SHOVEL");

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

        if (SWORDS.contains(held.getType()))
        {
            RpgStat.SWORDS.increaseExp((Player) event.getDamager());
            return;
        }

        if (AXES.contains(held.getType()))
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

        if (PICKAXES.contains(held.getType()) && RpgBlockTypes.STONE_LOOKUPS.containsKey(event.getBlock().getType()))
        {
            RpgStat.MINING.increaseExp(event.getPlayer());
            return;
        }

        if (SHOVELS.contains(held.getType()) && RpgBlockTypes.DIRT_LOOKUPS.containsKey(event.getBlock().getType()))
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
        if (event.getPlayer().isClimbing() || event.getPlayer().isFlying() || event.getPlayer().isInWater() || event.getPlayer().isInsideVehicle() || event.getPlayer().isRiptiding() || event.getPlayer().isSwimming())
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

    static List<Material> getValidMaterials(String name)
    {
        List<Material> data = new ArrayList<>();

        for (Material m : Material.values())
        {
            if (m.name().contains("LEGACY"))
                continue;

            if (m.name().contains(name))
                data.add(m);
        }

        return data;
    }
}
