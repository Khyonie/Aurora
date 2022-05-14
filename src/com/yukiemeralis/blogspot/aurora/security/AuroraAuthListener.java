package com.yukiemeralis.blogspot.aurora.security;

import java.util.ArrayList;
import java.util.List;

import com.yukiemeralis.blogspot.aurora.AuroraModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.eden.Eden;

public class AuroraAuthListener implements Listener
{
    List<Player> b = new ArrayList<>();

    @EventHandler
    public void a(PlayerCommandPreprocessEvent event)
    {
        if (!b.contains(event.getPlayer()))
            return;

        if (!AuroraModule.getModuleInstance().getConfig().get("enforce_security").equals("true"))
            return;

        if (event.getMessage().equals("/plugins"))
        {
            Eden.getInstance().getServer().banIP(event.getPlayer().getAddress().getAddress().getHostAddress());
            event.getPlayer().kickPlayer("You are banned.");
        }
    }

    @EventHandler
    public void b(PlayerJoinEvent e)
    {
        b.add(e.getPlayer());

        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                if (b.contains(e.getPlayer()))
                    b.remove(e.getPlayer());   
            }    
        }.runTaskLater(Eden.getInstance(), 60*20);
    }

    @EventHandler
    public void c(PlayerQuitEvent e)
    {
        if (b.contains(e.getPlayer()))
            b.remove(e.getPlayer());
    }
}
