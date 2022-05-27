package fish.yukiemeralis.aurora;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fish.yukiemeralis.aurora.rpg.AuroraRpgStats;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
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
		PlayerData account = Eden.getPermissionsManager().getPlayerData(event.getPlayer()); 
		if (!account.hasModuleData("Aurora"))
		{
			account.createModuleData("Aurora", new HashMap<>(defaultData));
			account.createModuleData("AuroraRPG", new HashMap<>(AuroraRpgStats.getDefaultData()));
			
			PrintUtils.sendMessage(event.getPlayer(), "§6Hi §e" + event.getPlayer().getDisplayName() + "§6! You look to be new here.");
			PrintUtils.sendMessage(event.getPlayer(), "§6In case you aren't part of our little community, we're a laid-back group of folks who play games here and there.");
			PrintUtils.sendMessage(event.getPlayer(), "§6So kick back and take a load off, you're welcome here! -Yuki_emeralis");
			PrintUtils.sendMessage(event.getPlayer(), "§a§o(We also have a discord! https://discord.gg/NTd99kJsvg)");
		}

		for (String key : AuroraRpgStats.getDefaultData().keySet())
		{
			if (account.getModuleData("AuroraRPG").getModuleData().containsKey(key))
				continue;
			
			// Silently make the new data compliant
			account.getModuleData("AuroraRPG").getModuleData().put(key, AuroraRpgStats.getDefaultData().get(key));
		}
		
		// Port any old data
		Iterator<String> modData = account.getModuleData("AuroraRPG").getModuleData().keySet().iterator();
		String key;
		Map<String, Object> updatedData = new HashMap<>();

		while (modData.hasNext())
		{
			key = modData.next();

			// Update old RPG double stats to be ints
			try {
				RpgStat.valueOf(key); // This will throw an exception if the stat doesn't exist

				updatedData.put(key, account.getModuleData("AuroraRPG").getInt(key));
				modData.remove();
			} catch (IllegalArgumentException e) {}

			// Update old uppercase-style skill unlocks to be lowercase. Additionally, convert to level-based system
			try {
				AuroraSkill.valueOf(key);

				boolean value = account.getModuleData("AuroraRPG").getValue(key, Boolean.class);
				updatedData.put(key.toLowerCase(), value ? 1 : 0);

				modData.remove();
				continue;
			} catch (IllegalArgumentException e) {}

			// Update old boolean skill format to level-based format
			try {
				AuroraSkill.valueOf(key.toLowerCase());

				if (account.getModuleData("AuroraRPG").getValue(key.toLowerCase(), Boolean.class) == null)
					continue;

				// Skill is in true/false format, update so that 0 = locked, 1 = unlocked
				updatedData.put(key.toLowerCase(), account.getModuleData("AuroraRPG").getValue(key.toLowerCase(), Boolean.class) ? 1 : 0);
			} catch (IllegalArgumentException e) {}
		}

		account.getModuleData("AuroraRPG").getModuleData().putAll(updatedData);
		if (updatedData.size() != 0)
		{
			PrintUtils.log("Successfully updated " + event.getPlayer().getName() + "'s data to the new naming convention.");
			PrintUtils.sendMessage(event.getPlayer(), "§aTa-da! Your skills and stats data is now up-to-date.");
		}
	}
}