package com.yukiemeralis.blogspot.aurora.pylons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.yukiemeralis.blogspot.aurora.pylons.item.Pylporter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.surface2.component.GuiComponent;
import fish.yukiemeralis.eden.surface2.component.GuiItemStack;
import fish.yukiemeralis.eden.utils.ChatUtils;
import fish.yukiemeralis.eden.utils.ChatUtils.ChatAction;
import fish.yukiemeralis.eden.utils.HashUtils;
import fish.yukiemeralis.eden.utils.ItemUtils;
import fish.yukiemeralis.eden.utils.PrintUtils;

public class Pylon implements GuiComponent
{
	private GuiItemStack icon;
	
	@Expose(serialize = true, deserialize = true)
	private String worldName, pylonName, password, salt;
	@Expose(serialize = true, deserialize = true)
	private double x, y, z;
	@Expose(serialize = true, deserialize = true)
	private Material material = null;
	@Expose(serialize = true, deserialize = true)
	private List<String> allowedPlayers;
	
	private List<Block> associatedBlocks = new ArrayList<>();
	
	public Pylon(Location location, String pylonName)
	{
		this.pylonName = pylonName;
		this.worldName = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}
	
	public Pylon() {}
	
	static final String[] CARDINAL_DIRECTIONS = {"NORTH", "SOUTH", "EAST", "WEST"};
	private static final Map<Environment, Material> ENVIRONMENT_MATERIALS = new HashMap<>()
	{{
		put(Environment.NORMAL, Material.GRASS_BLOCK);
		put(Environment.NETHER, Material.NETHERRACK);
		put(Environment.THE_END, Material.END_STONE);
	}};
	
	public void init()
	{
		// Assuming we start at the bell
		Block bell = getLocation().getWorld().getBlockAt(getLocation());
		associatedBlocks.add(bell);
		associatedBlocks.add(bell.getRelative(BlockFace.DOWN)); // Purpur pillar
		
		// End stone bricks
		Block bricks = bell.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
		associatedBlocks.add(bricks);
		for (String dir : CARDINAL_DIRECTIONS)
			associatedBlocks.add(bricks.getRelative(BlockFace.valueOf(dir)));
	}
	
	public List<Block> getAssociatedBlocks()
	{
		return this.associatedBlocks;
	}
	
	public Location getLocation()
	{
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}
	
	public String getName()
	{
		return this.pylonName;
	}
	
	public void setName(String name)
	{
		icon = null;
		this.pylonName = name;
	}
	
	public Material getMaterial()
	{
		return this.material;
	}
	
	public void setMaterial(Material material)
	{
		this.material = material;
		icon = null;
	}

	// Passwords

	public boolean hasPassword()
	{
		return password != null;
	}

	public void setPassword(String password)
	{
		this.salt = HashUtils.genererateSalt(64);
		this.icon = null;
		this.password = HashUtils.hexToString(HashUtils.hashStringSHA256(password, salt));
	}

	public void clearPassword()
	{
		this.icon = null;
		this.allowedPlayers = null;
		this.password = null;
	}

	public boolean comparePassword(String input)
	{
		return this.password.equals(HashUtils.hexToString(HashUtils.hashStringSHA256(input, salt)));
	}

	public boolean isAllowedPlayer(Player player)
	{
		if (Eden.getPermissionsManager().isElevated(player))
			return true;
		if (allowedPlayers == null)
			return false;
		return allowedPlayers.contains(player.getUniqueId().toString());
	}

	public void addAllowedPlayer(Player player)
	{
		if (allowedPlayers == null)
			allowedPlayers = new ArrayList<>();
		allowedPlayers.add(player.getUniqueId().toString());
	}

	public void removeAllowedPlayer(Player player)
	{
		if (allowedPlayers == null)
			return;
		allowedPlayers.remove(player.getUniqueId().toString());
	}
	
	@Override
	public void onInteract(InventoryClickEvent event)
	{
		generate().onInteract(event);
	}

	@Override
	public GuiItemStack generate()
	{
		if (icon != null)
			return icon;
		
		if (material == null)
			material = ENVIRONMENT_MATERIALS.get(getLocation().getWorld().getEnvironment());
		icon = new GuiItemStack(material)
		{
			@Override
			public void onInteract(InventoryClickEvent event)
			{
				float 
					pitch = event.getWhoClicked().getLocation().getPitch(), 
					yaw = event.getWhoClicked().getLocation().getYaw();
				
				Location target = getLocation().clone().add(0.5, 1, 0.5);
				target.setPitch(pitch);
				target.setYaw(yaw);

				if (hasPassword())
				{
					if (!isAllowedPlayer((Player) event.getWhoClicked()))
					{
						PrintUtils.sendMessage(event.getWhoClicked(), "This pylon is private. Please enter your password. It is now safe to enter your password.");
						PrintUtils.sendMessage(event.getWhoClicked(), "Type \"cancel\" to leave password entry mode.");

						event.getWhoClicked().closeInventory();

						ChatAction action = new ChatAction()
						{
							@Override
							public void run()
							{
								String input = ChatUtils.receiveResult(event.getWhoClicked());
								ChatUtils.deleteResult(event.getWhoClicked());	

								if (input.toLowerCase().equals("cancel"))
								{
									PrintUtils.sendMessage(event.getWhoClicked(), "Left password entry mode.");
									return;
								}

								if (!comparePassword(input))
								{
									PrintUtils.sendMessage(event.getWhoClicked(), "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
									PrintUtils.sendMessage(event.getWhoClicked(), "It is no longer safe to enter your password.");
									return;
								}
								
								new BukkitRunnable()
								{
									@Override
									public void run()
									{
										ItemStack mainhand = event.getWhoClicked().getEquipment().getItemInMainHand();
										ItemStack offhand = event.getWhoClicked().getEquipment().getItemInOffHand();
										boolean reducedItem = false;
										
										if (Pylporter.isPylporterItem(mainhand))
										{
											mainhand.setAmount(mainhand.getAmount() - 1);
											reducedItem = true;
										}

										if (Pylporter.isPylporterItem(offhand) && !reducedItem)
										{
											offhand.setAmount(offhand.getAmount() - 1);
										}
										
										event.getWhoClicked().teleport(target);
									}
								}.runTask(Eden.getInstance());
								
								((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
							}
						};

						ChatUtils.expectChat(event.getWhoClicked(), action);
						return;
					}
				}

				ItemStack mainhand = event.getWhoClicked().getEquipment().getItemInMainHand();
				ItemStack offhand = event.getWhoClicked().getEquipment().getItemInOffHand();
				boolean reducedItem = false;
				
				if (Pylporter.isPylporterItem(mainhand))
				{
					mainhand.setAmount(mainhand.getAmount() - 1);
					reducedItem = true;
				}

				if (Pylporter.isPylporterItem(offhand) && !reducedItem)
				{
					offhand.setAmount(offhand.getAmount() - 1);
				}
				
				event.getWhoClicked().teleport(target);
				((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
		};

		if (this.hasPassword())
		{
			ItemUtils.applyName(icon, "§r" + ChatUtils.of("F78981") + "§l" + pylonName);
			ItemUtils.applyLore(icon, "§r" + ChatUtils.of("78C2FF") + "This pylon is password-protected.");
		} else {
			ItemUtils.applyName(icon, "§r" + ChatUtils.of("B0DBFF") + "§l" + pylonName);
			ItemUtils.applyLore(icon, 
				"§r" + ChatUtils.of("78C2FF") + "X: " + this.x,
				"§r" + ChatUtils.of("78C2FF") + "Y: " + this.y,
				"§r" + ChatUtils.of("78C2FF") + "Z: " + this.z,
				"§r" + ChatUtils.of("78C2FF") + "World: " + this.worldName
			);
		}
		
		return icon;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
			return false;
		if (!(obj instanceof Pylon))
			return false;
		return ((Pylon) obj).getLocation().equals(this.getLocation());
	}
}
