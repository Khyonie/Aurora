package fish.yukiemeralis.aurora.rpg.skill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import fish.yukiemeralis.aurora.rpg.SkillResult;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.Eden;
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
            {
                // PrintUtils.log("§eFORGE \\>§f First or second item is null.");
                return data.some(SkillResult.of(false, false));
            }
            // PrintUtils.log("§6FORGE \\>§f Neither item is null.");

            if (first.getType().equals(Material.ENCHANTED_BOOK) && second.getType().equals(Material.ENCHANTED_BOOK))
            {
                // PrintUtils.log("§eFORGE \\>§f Both items are enchanted books.");
                return data.some(SkillResult.of(false, false));
            }
            // PrintUtils.log("§6FORGE \\>§f At least one item is not an enchanted book.");

            if (!first.getType().equals(Material.ENCHANTED_BOOK) && !second.getType().equals(Material.ENCHANTED_BOOK))
            {
                // PrintUtils.log("§eFORGE \\>§f Neither items are enchanted books.");
                return data.some(new SkillResult(false, false));
            }
            // PrintUtils.log("§6FORGE \\>§f Only one item is an enchanted book.");

            // Set the items in a constant place so we can safely do logic with them
            ItemStack 
                book = first, 
                target = second;

            if (second.getType().equals(Material.ENCHANTED_BOOK))
            {
                // PrintUtils.log("§cFORGE \\>§f Book is in second position. (slot 1)");
                target = first;
                book = second;
            }

            Enchantment bookEnchant = ((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchants().keySet().toArray(new Enchantment[0])[0];
            // PrintUtils.log("§cFORGE \\>§f Book enchantment is " + bookEnchant.getKey().getKey());

            if (target.getEnchantments().size() == 0)
            {
                // PrintUtils.log("§dFORGE \\>§f Target has no enchantments, thus no need to modify output.");
                return data.some(new SkillResult(false, false)); // No enchantments active on target, forge is safe
            }

            // Check for incompatibilities, if no incompatibilites exist, no need to modify output
            boolean compatible = true;
            a: for (Enchantment e : target.getEnchantments().keySet())
            {
                // PrintUtils.log("§eFORGE \\>§f Checking enchantment " + e.getKey().getKey() + " on target");
                if (!e.conflictsWith(bookEnchant))
                {
                    // PrintUtils.log("§eFORGE \\>§f No confliction.");
                    continue;
                }

                // PrintUtils.log("§cFORGE \\>§f Conflict between " + e.getKey().getKey() + " and " + bookEnchant.getKey().getKey() + " found. Compatibility turned on.");
                compatible = false;
                break a;
            }

            if (compatible)
            {
                // PrintUtils.log("§6FORGE \\>§f Enchantments are naturally compatible, thus no need to modify output.");
                return data.some(SkillResult.of(false, false));
            }
            // PrintUtils.log("§eFORGE \\>§f Performing custom compatibility check...");

            // Do a more thorough check with compatibility exceptions added
            if (!checkCompatible(bookEnchant, target.getEnchantments().keySet().toArray(new Enchantment[target.getEnchantments().size()])))
            {
                // PrintUtils.log("§dFORGE \\>§f Failed custom compatibility check.");
                return data.some(SkillResult.of(false, false)); // Incompatible with no exceptions. Forge is not safe
            }

            // PrintUtils.log("§dFORGE \\>§f Succeeded custom compatibility check. Proceeding with activation...");

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
        Map<Enchantment, Integer> ench = ((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchants();

        // Simulate combining existing enchantments
        List<Enchantment> toRemove = new ArrayList<>();
        List<Enchantment> toCombine = new ArrayList<>();

        ench.forEach((e, lvl) -> {
            if (!result.containsEnchantment(e))
                return;

            // Target level equals book level = combine + 1
            if (result.getEnchantmentLevel(e) == lvl)
            {
                toCombine.add(e);
                return;
            }
            
            // Target level is greater than book level = remove from book
            if (result.getEnchantmentLevel(e) > lvl)
                toRemove.add(e);
        });

        // Perform modification
        for (Enchantment e : toCombine)
        {
            if (ench.get(e) == e.getMaxLevel())
                continue;
            ench.put(e, ench.get(e) + 1);
        }
        for (Enchantment e : toRemove)
            ench.remove(e);

        result.addUnsafeEnchantments(ench);
        
        //Bukkit.getScheduler().runTask(Eden.getInstance(), () -> event.getInventory().setRepairCost(event.getInventory().getRepairCost()));
        Bukkit.getScheduler().runTask(Eden.getInstance(), () -> event.getInventory().setRepairCost(30));
        event.setResult(result);

        return new Tuple2<Boolean,Boolean>(false, false);
    }

    private static boolean checkCompatible(Enchantment toBeApplied, Enchantment... current)
    {
        for (Enchantment e : current)
        {
            if (!toBeApplied.conflictsWith(e))
                continue;

            if (COMPATIBILITY_EXCEPTIONS.containsKey(toBeApplied))
                if (COMPATIBILITY_EXCEPTIONS.get(toBeApplied).equals(e))
                    continue;

            return false;
        }
        return true;
    }
}
