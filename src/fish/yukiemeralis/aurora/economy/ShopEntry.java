package fish.yukiemeralis.aurora.economy;

import org.bukkit.Material;

public class ShopEntry 
{
    public ShopEntry(Material item, int buyValue, int sellValue, float sellModifier, float inflationModifier, int maxBeforeLock)
    {
        this(item, 1, buyValue, sellValue, sellModifier, inflationModifier, maxBeforeLock);
    }    

    public ShopEntry(Material item, int amount, int buyValue, int sellValue, float sellModifier, float inflationModifier, int maxBeforeLock)
    {

    }
}
