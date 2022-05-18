package fish.yukiemeralis.aurora.rpg.skill;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillRefundArrow extends AbstractSkill<ProjectileLaunchEvent>
{
    protected SkillRefundArrow() 
    {
        super(AuroraSkill.ARROW_REFUND, ProjectileLaunchEvent.class);
    }

    private static Map<Arrow, Player> REFUND_TRACKER = new HashMap<>(); 

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(ProjectileLaunchEvent event) 
    {
        ItemStack mainHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInMainHand();
        ItemStack offHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInOffHand();

        if (mainHeld != null)
            if (mainHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return new Tuple2<>(false, false);
        if (offHeld != null)
            if (offHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return new Tuple2<>(false, false);

        synchronized (REFUND_TRACKER)
        {
            REFUND_TRACKER.put((Arrow) event.getEntity(), (Player) event.getEntity().getShooter());
        }
        ((Arrow) event.getEntity()).setPickupStatus(PickupStatus.DISALLOWED);

        return new Tuple2<>(false, false);
    }

    public static boolean isTrackedArrow(Arrow arrow)
    {
        return REFUND_TRACKER.containsKey(arrow);
    }

    public static void refundArrow(Arrow arrow)
    {
        REFUND_TRACKER.get(arrow).getInventory().addItem(new ItemStack(Material.ARROW));
        synchronized (REFUND_TRACKER)
        {
            REFUND_TRACKER.remove(arrow);
        }
    }
}
