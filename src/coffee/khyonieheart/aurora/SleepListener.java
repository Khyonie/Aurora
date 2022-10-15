package coffee.khyonieheart.aurora;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.utils.PrintUtils;

public class SleepListener implements Listener
{
	private static int sleepingPlayersCount = 0;
	private static BukkitTask sleepThread = null;
	private static List<Player> sleepingPlayers = new ArrayList<>();
	
	@EventHandler
	public void onSleep(PlayerBedEnterEvent event)
	{
		if (!event.getBedEnterResult().equals(BedEnterResult.OK))
			return;
			
		sleepingPlayersCount++;
		sleepingPlayers.add(event.getPlayer());

		int overworldCount = event.getPlayer().getWorld().getPlayers().size();

		if (overworldCount == 1)
			return;
		
		if ((sleepingPlayersCount/(double) overworldCount) >= 0.50)
		{
			if (sleepThread == null)
			{
				Bukkit.getOnlinePlayers().forEach(player -> {
					PrintUtils.sendMessage(player, "Skipping night in 5 seconds...");
				});
				sleepThread = new BukkitRunnable()
				{
					@Override
					public void run()
					{
						if (!this.isCancelled() && sleepingPlayers.size() > 0)
						{
							sleepingPlayers.get(0).getWorld().setTime(0);
							sleepThread = null;
							Bukkit.getOnlinePlayers().forEach(player -> {
								PrintUtils.sendMessage(player, "Skipped night.");
							});
						}
					}
				}.runTaskLater(Eden.getInstance(), 100);
			}
				
		}
	}
	
	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event)
	{
		sleepingPlayersCount--;
		sleepingPlayers.remove(event.getPlayer());

		int overworldCount = event.getPlayer().getWorld().getPlayers().size();
		
		if (sleepThread != null && sleepingPlayersCount/overworldCount < 0.50)
		{
			sleepThread.cancel();
			sleepThread = null;
			Bukkit.getOnlinePlayers().forEach(player -> {
				PrintUtils.sendMessage(player, "Not enough players are sleeping. The night will continue.");
			});
		}
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event)
	{
		if (sleepingPlayers.contains(event.getPlayer()))
			onBedLeave(new PlayerBedLeaveEvent(event.getPlayer(), event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation()), false));
	}
}
