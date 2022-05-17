package fish.yukiemeralis.aurora;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.eden.Eden;

public class ElevatorListener implements Listener
{
	private static List<Player> TP_DELAYED_PLAYERS = new ArrayList<>();
	
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event)
	{
		if (!event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().subtract(0, 1, 0)).getType().equals(Material.EMERALD_BLOCK))
			return;
		
		if (TP_DELAYED_PLAYERS.contains(event.getPlayer()) || !event.isSneaking())
			return;
		
		Block current = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().subtract(0, 2, 0));
		
		while (current.getLocation().getY() > getMinWorldHeight())
		{
			current = current.getRelative(BlockFace.DOWN);
			
			if (!current.getType().equals(Material.EMERALD_BLOCK))
				continue;
			
			final float
				pitch = event.getPlayer().getLocation().getPitch(),
				yaw = event.getPlayer().getLocation().getYaw();
			
			Location target = current.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
			target.setPitch(pitch);
			target.setYaw(yaw);
			
			event.getPlayer().teleport(target);
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			
			synchronized(TP_DELAYED_PLAYERS)
			{
				TP_DELAYED_PLAYERS.add(event.getPlayer());
			}
			
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					synchronized(TP_DELAYED_PLAYERS)
					{
						TP_DELAYED_PLAYERS.remove(event.getPlayer());
					}
				}
			}.runTaskLater(Eden.getInstance(), 5);
			
			return;
		}
		return;
	}

	private int getMaxWorldHeight()
	{
		switch (Eden.getNMSVersion())
		{
			case "v1_18_R1":
			case "v1_18_R2":
				return 319;
			default:
				return 255;
		}
	}

	private int getMinWorldHeight()
	{
		switch (Eden.getNMSVersion())
		{
			case "v1_18_R1":
			case "v1_18_R2": 
				return -64;
			default:
				return 0;
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event)
	{
		if (!event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().subtract(0, 1, 0)).getType().equals(Material.EMERALD_BLOCK))
			return;
		
		if (event.getTo().getY() > event.getFrom().getY() && !TP_DELAYED_PLAYERS.contains(event.getPlayer())) // Teleport up
		{
			Block current = event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation());
			
			while (current.getLocation().getY() < getMaxWorldHeight())
			{
				current = current.getRelative(BlockFace.UP);
				
				if (!current.getType().equals(Material.EMERALD_BLOCK))
					continue;
				
				final float
					pitch = event.getPlayer().getLocation().getPitch(),
					yaw = event.getPlayer().getLocation().getYaw();
				
				Location target = current.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
				target.setPitch(pitch);
				target.setYaw(yaw);
				
				event.getPlayer().teleport(target);
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				
				synchronized(TP_DELAYED_PLAYERS)
				{
					TP_DELAYED_PLAYERS.add(event.getPlayer());
				}
				
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						synchronized(TP_DELAYED_PLAYERS)
						{
							TP_DELAYED_PLAYERS.remove(event.getPlayer());
						}
					}
				}.runTaskLater(Eden.getInstance(), 5);
				
				return;
			}
			return;
		}
	}
}
