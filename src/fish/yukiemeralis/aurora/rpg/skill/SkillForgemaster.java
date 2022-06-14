package fish.yukiemeralis.aurora.rpg.skill;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.Option;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillForgemaster extends AbstractSkill<PrepareAnvilEvent> 
{
    private static Map<Enchantment, Enchantment> COMPATIBILITY_EXCEPTIONS = Map.of(
        Enchantment.MENDING, Enchantment.ARROW_INFINITE,
        Enchantment.ARROW_INFINITE, Enchantment.MENDING,
        Enchantment.PIERCING, Enchantment.MULTISHOT,
        Enchantment.MULTISHOT, Enchantment.PIERCING
    );

    public SkillForgemaster() 
    {
        super(AuroraSkill.FORGEMASTER, PrepareAnvilEvent.class);
    }

    @Override
    protected Option<SkillResult> shouldActivate(PrepareAnvilEvent event, Player player)
    {
        Option<SkillResult> data = new Option<>(SkillResult.class);
        try {

            AnvilInventory inv = event.getInventory();
            ItemStack 
                first = inv.getItem(0), 
                second = inv.getItem(1);

            if (first == null || second == null)
                return data.some(SkillResult.of(false, false));

            if (!first.getType().equals(Material.ENCHANTED_BOOK) && !second.getType().equals(Material.ENCHANTED_BOOK))
                return data.some(new SkillResult(false, false));

            // Set the items in a constant place so we can safely do logic with them
            ItemStack 
                book = first, 
                target = second;

            if (second.getType().equals(Material.ENCHANTED_BOOK))
            {
                target = first;
                book = second;
            }

            Enchantment bookEnchant = ((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchants().keySet().toArray(new Enchantment[0])[0];

            if (target.getEnchantments().size() == 0)
                return data.none(); // No enchantments active on target, forge is safe

            // Check for incompatibilities, if no incompatibilites exist, no need to modify output
            boolean compatible = true;
            for (Enchantment e : target.getEnchantments().keySet())
            {
                if (!e.conflictsWith(bookEnchant))
                    continue;

                compatible = false;
            }

            if (compatible)
                return data.some(SkillResult.of(false, false));

            // Do a more thorough check with compatibility exceptions added
            if (!checkCompatible(bookEnchant, target.getEnchantments().keySet().toArray(new Enchantment[target.getEnchantments().size()])))
                return data.some(SkillResult.of(false, false)); // Incompatible with no exceptions. Forge is not safe

            return data.none();
        } catch (Exception e) {
            PrintUtils.sendMessage((Player) event.getViewers().get(0), "§cAn issue has occurred in the enchantment override process. Please send Yuki_emeralis a screenshot of this message. (F2)");
            PrintUtils.sendMessage((Player) event.getViewers().get(0), "§cTimestamp is " + new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis())));
            e.printStackTrace();
            return data.some(SkillResult.of(false, false));
        }
        
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(PrepareAnvilEvent event) 
    {
        AnvilInventory inv = event.getInventory();
        ItemStack 
            first = inv.getItem(0), 
            second = inv.getItem(1);

        ItemStack
            book = first,
            target = second;

        if (second.getType().equals(Material.ENCHANTED_BOOK))
        {
            book = second;
            target = first;
        }

        ItemStack result = target.clone();

        result.addUnsafeEnchantments(((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchants());
        
        event.setResult(result);

        return new Tuple2<Boolean,Boolean>(false, false);
    }

    private static boolean checkCompatible(Enchantment toBeApplied, Enchantment... current)
    {
        for (Enchantment e : current)
        {
            if (toBeApplied.conflictsWith(e))
                if (COMPATIBILITY_EXCEPTIONS.containsKey(toBeApplied))
                    if (COMPATIBILITY_EXCEPTIONS.get(toBeApplied).equals(e))
                        continue;

            return false;
        }
        return true;
    }
}
