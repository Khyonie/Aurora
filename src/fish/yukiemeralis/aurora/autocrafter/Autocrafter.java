package fish.yukiemeralis.aurora.autocrafter;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.module.java.annotations.Unimplemented;

@Unimplemented
@SuppressWarnings("unused")
public class Autocrafter 
{
    private Player registrar; // The player to "craft" items with
    private Block host; // The chest block to act as an autocrafter
    private int index = 0; // The index to insert items at

    public Autocrafter(Player player, Block host)
    {

    }

    private ItemStack craft(ItemStack[] matrix)
    {
        return Eden.getInstance().getServer().craftItem(matrix, host.getWorld(), registrar);
    }  

    public void registerPlayer(Player registrar)
    {
        this.registrar = registrar;
    }

    public void incrementIndex()
    {
        index++;
        if (index == 9)
            index = 0;
    }
}
