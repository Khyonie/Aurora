package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.ItemUtils;
import fish.yukiemeralis.eden.utils.Option;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillSilkSpawners extends AbstractSkill<BlockBreakEvent>
{
    public SkillSilkSpawners() 
    {
        super(AuroraSkill.SILK_SPAWNERS, BlockBreakEvent.class);
    }

    @Override
    public Option<SkillResult> shouldActivate(BlockBreakEvent event, Player player)
    {
        Option<SkillResult> data = new Option<>(SkillResult.class);
        ItemStack held = player.getEquipment().getItemInMainHand();

        if (!event.getBlock().getType().equals(Material.SPAWNER))
            return data.some(new SkillResult(false, false));

        if (held.getType().equals(Material.AIR))
            return data.some(new SkillResult(false, false));

        if (!held.containsEnchantment(Enchantment.SILK_TOUCH))
            return data.some(new SkillResult(false, false));  

        return data.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(BlockBreakEvent event) 
    {
        CreatureSpawner spawnerMeta = (CreatureSpawner) event.getBlock().getState();
        ItemStack item = new ItemStack(Material.SPAWNER);

        ItemUtils.saveToNamespacedKey(item, "spawnerType", spawnerMeta.getSpawnedType().name());
        ItemUtils.applyName(item, "§e" + processName(spawnerMeta.getSpawnedType().name()) + " Spawner");

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        
        return new Tuple2<>(false, false);
    }

    private String processName(String name)
    {
        if (name.length() == 0)
            throw new IllegalArgumentException("Name cannot be empty.");

        StringBuilder builder = new StringBuilder();
        builder.append(Character.toUpperCase(name.charAt(0)));
        builder.append(name.toLowerCase().substring(1));

        return builder.toString();
    }
}
