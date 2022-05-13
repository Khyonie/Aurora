package com.yukiemeralis.blogspot.aurora.pylons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fish.yukiemeralis.eden.utils.PrintUtils;

public class PylonListener implements Listener
{
	private static final List<Material> PYLON_MATERIALS = new ArrayList<>()
	{{
		add(Material.BELL);
		add(Material.PURPUR_PILLAR);
		add(Material.END_STONE_BRICKS);
	}};
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event)
	{
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		
		if (!event.getClickedBlock().getType().equals(Material.BELL))
			return;
		
		if (!PylonNetwork.isValidPylon(event.getClickedBlock()))
			return;
		
		event.setCancelled(true);
		
		if (PylonNetwork.getPylonAssociatedWith(event.getClickedBlock()) == null) // Pylon isn't registered
		{
			String name = toCamelCase(event.getClickedBlock().getWorld().getEnvironment().name());
			for (int i = 0; true; i++)
			{
				if (PylonNetwork.getPylonByNameExact(name + (PylonNetwork.getPylonsByName(name).size() + i)) == null)
				{
					name = name + (PylonNetwork.getPylonsByName(name).size() + i);
					break;
				}
			}
			
			Pylon pylon = new Pylon(event.getClickedBlock().getLocation(), name);
			pylon.init();
			
			PylonNetwork.register(pylon);
			PrintUtils.sendMessage(event.getPlayer(), "Registered a new pylon here.");
		}
		
		PylonGui gui = new PylonGui(event.getPlayer());
		gui.display();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (!PYLON_MATERIALS.contains(event.getBlock().getType()))
			return;
		
		if (PylonNetwork.getPylonAssociatedWith(event.getBlock()) == null)
			return;
		
		PylonNetwork.unregister(PylonNetwork.getPylonAssociatedWith(event.getBlock()));
		PrintUtils.sendMessage(event.getPlayer(), "You have broken the pylon here.");
	}

	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent event)
	{
		event.blockList().removeIf(block -> {
			return PylonNetwork.getPylonAssociatedWith(block) != null;
		});
	}

	@EventHandler
	public void onBlockExplosion(BlockExplodeEvent event)
	{
		event.blockList().removeIf(block -> {
			return PylonNetwork.getPylonAssociatedWith(block) != null;
		});
	}
	
	private static String toCamelCase(String input)
	{
		String base = input.toLowerCase().substring(1);
		return new String(new char[] {input.charAt(0)}).toUpperCase() + base;
	}
}
