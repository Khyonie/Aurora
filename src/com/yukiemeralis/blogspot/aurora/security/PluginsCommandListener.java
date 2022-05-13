package com.yukiemeralis.blogspot.aurora.security;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fish.yukiemeralis.eden.Eden;

public class PluginsCommandListener implements Listener
{
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        // If the player executes /plugins...
        if (event.getMessage().equals("/plugins"))
        {
            // Add their IP to the blacklist,
            Eden.getInstance().getServer().banIP(event.getPlayer().getAddress().getAddress().getHostAddress());
            // And give them the boot
            event.getPlayer().kickPlayer("You are banned.");
        }
    }
}
