package fish.yukiemeralis.aurora.mobs;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

public class MobEquipmentListener implements Listener
{
    private static final Map<EntityType, Material[]> EQUIPMENT_OPTIONS = Map.of(
        EntityType.SKELETON, new Material[] {
            Material.WOODEN_SWORD,
            Material.IRON_HOE,
            Material.BONE,
            Material.QUARTZ,
            Material.FISHING_ROD
        },
        EntityType.ZOMBIE, new Material[] {
            Material.COOKED_BEEF,
            Material.STONE_HOE,
            Material.WOODEN_HOE,
            Material.BONE,
            Material.TORCH,
            Material.FISHING_ROD,
            Material.CHICKEN,
            Material.PORKCHOP,
            Material.GOLDEN_SWORD
        }
    );
    
    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        switch (event.getEntityType())
        {
            case CREEPER:
                if (random.nextInt(50) == 10)
                    ((Creeper) event.getEntity()).setMaxFuseTicks(15); // 2% chance to spawn a creeper with a half-fuse
                if (random.nextInt(1000) == 0)
                    ((Creeper) event.getEntity()).setPowered(true); // 0.1% chance to spawn a charged creeper
                break;
            case SKELETON:
                if (random.nextInt(3) != 0) // 33.34% chance to replace a skeleton's bow with an item
                    return;

                setHeldItem(event.getEntity(), generateEntityItem(event.getEntity()));
                break;
            case ZOMBIE:
                if (random.nextInt(15) != 0) // 6.67% chance to set a zombie's item
                    return;

                setHeldItem(event.getEntity(), generateEntityItem(event.getEntity()));
                break;
            default:
                return;
        }
    }

    private static ItemStack generateEntityItem(Entity ent)
    {
        if (!EQUIPMENT_OPTIONS.containsKey(ent.getType()))
            return null;
        
        Material mat = EQUIPMENT_OPTIONS.get(ent.getType())[random.nextInt(EQUIPMENT_OPTIONS.get(ent.getType()).length)];

        return new ItemStack(mat);
    }

    private static void setHeldItem(Entity ent, ItemStack item)
    {
        if (!(ent instanceof LivingEntity))
            throw new IllegalArgumentException("Cannot set held item of non-living entity " + ent.getType().name());

        ((LivingEntity) ent).getEquipment().setItemInMainHand(item);
    }
}
