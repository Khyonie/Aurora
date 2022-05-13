package com.yukiemeralis.blogspot.aurora;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.permissions.PlayerData;
import fish.yukiemeralis.eden.utils.PrintUtils;

public class AccountListener implements Listener
{
	static Map<String, Object> defaultData = new HashMap<>() 
	{{
		put("treecapEnabled", true);
	}};

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event)
	{
		//AuroraModule.PLAYER_DATA.put(event.getPlayer(), new AuroraPlayerData());

		PlayerData account = Eden.getPermissionsManager().getPlayerData(event.getPlayer()); 
		if (!account.hasModuleData("Aurora"))
		{
			account.createModuleData("Aurora", new HashMap<>(defaultData));
			
			PrintUtils.sendMessage(event.getPlayer(), "§6Hi §e" + event.getPlayer().getDisplayName() + "§6! You look to be new here.");
			PrintUtils.sendMessage(event.getPlayer(), "§6In case you aren't part of our little community, we're a laid-back group of folks who play games here and there.");
			PrintUtils.sendMessage(event.getPlayer(), "§6So kick back and take a load off, you're welcome here! -Yuki_emeralis");
			PrintUtils.sendMessage(event.getPlayer(), "§a§o(We also have a discord! https://discord.gg/NTd99kJsvg)");
		}
	}
}
