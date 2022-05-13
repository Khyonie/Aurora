package com.yukiemeralis.blogspot.aurora;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener implements Listener
{
	private static Random random = new Random();
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event)
	{
		if (AuroraModule.getModuleInstance().getConfig().get("1.17_weather_fix") != "true")
			return;

		if (random.nextInt(20) != 0)
			event.setCancelled(true);
	}
}
