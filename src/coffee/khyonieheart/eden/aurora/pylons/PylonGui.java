package coffee.khyonieheart.eden.aurora.pylons;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import coffee.khyonieheart.eden.surface.GuiUtils;
import coffee.khyonieheart.eden.surface.SimpleComponentBuilder;
import coffee.khyonieheart.eden.surface.SurfaceGui;
import coffee.khyonieheart.eden.surface.component.GuiComponent;
import coffee.khyonieheart.eden.surface.component.GuiItemStack;
import coffee.khyonieheart.eden.surface.enums.DefaultClickAction;
import coffee.khyonieheart.eden.surface.special.PagedSurfaceGui;
import coffee.khyonieheart.eden.utils.ItemUtils;

public class PylonGui
{
	private final SurfaceGui gui;
	private final HumanEntity target;

	public PylonGui(HumanEntity e)
	{
		this.gui = new PagedSurfaceGui(
			54, // Inv size
			"Pylon network", // Inv name
			e,
			0, // Top bar row
			PylonNetwork.getActivePylons(), // List data
			Arrays.asList(
				new GuiComponent[] {
					closeItem(),
					(((Player) e).getBedSpawnLocation() == null ? GuiUtils.BLACK_PANE_GUI : spawnTeleportItem((Player) e))
				}
			), // Top bar data
			DefaultClickAction.CANCEL,
			InventoryAction.PICKUP_ALL,
			InventoryAction.PICKUP_HALF
		);
		this.target = e;
	}

	private static GuiItemStack closeItem()
	{
		return SimpleComponentBuilder.build(Material.BARRIER, "§r§c§lClose", (event) -> event.getWhoClicked().closeInventory(), "§7Close this GUI");
	}

	private static GuiItemStack spawnTeleportItem(Player player)
	{
		GuiItemStack item = new GuiItemStack(ItemUtils.build(Material.RED_BED, "§r§a§lTeleport to bed")) 
		{
			@Override
			public void onInteract(InventoryClickEvent event) 
			{
				player.teleport(player.getBedSpawnLocation());
				player.playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
		};

		return item;
	}

	public void display()
	{
		this.gui.display(target);
	}
}
