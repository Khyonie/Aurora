package coffee.khyonieheart.aurora.rpg.skill;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public class SkillRefundArrow extends AbstractSkill<ProjectileLaunchEvent>
{
    public SkillRefundArrow() 
    {
        super(AuroraSkill.ARROW_REFUND, ProjectileLaunchEvent.class);
    }

    private static Map<Arrow, Player> REFUND_TRACKER = new HashMap<>(); 

    @Override
    protected Option shouldActivate(ProjectileLaunchEvent event, Player player)
    {
        ItemStack mainHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInMainHand();
        ItemStack offHeld = ((Player) event.getEntity().getShooter()).getInventory().getItemInOffHand();

        if (mainHeld != null)
            if (mainHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return Option.some(new SkillResult(false, false));
        if (offHeld != null)
            if (offHeld.containsEnchantment(Enchantment.ARROW_INFINITE))
                return Option.some(new SkillResult(false, false));
        
        return Option.none();
    }

    @Override
    protected Tuple2<Boolean, Boolean> onActivate(ProjectileLaunchEvent event) 
    {
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

        PrintUtils.sendMessage((Player) arrow.getShooter(), "The arrow returned to you!");
        synchronized (REFUND_TRACKER)
        {
            REFUND_TRACKER.remove(arrow);
        }
    }
}
