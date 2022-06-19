package fish.yukiemeralis.aurora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fish.yukiemeralis.aurora.pylons.Pylon;
import fish.yukiemeralis.aurora.pylons.PylonNetwork;
import fish.yukiemeralis.aurora.rpg.AuroraRpgStats;
import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.command.EdenCommand;
import fish.yukiemeralis.eden.module.EdenModule;
import fish.yukiemeralis.eden.permissions.ModulePlayerData;
import fish.yukiemeralis.eden.utils.ChatUtils;
import fish.yukiemeralis.eden.utils.ChatUtils.ChatAction;
import fish.yukiemeralis.eden.utils.ItemUtils;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.PrintUtils.InfoType;

public class AuroraCommand extends EdenCommand
{
	public AuroraCommand(EdenModule parent_module)
	{
		super("aur", parent_module);

		addBranch("trees", "pylons", "item", "mob", "skills", "addsp", "stats", "track", "autolight", "clean", "^forremoval");

		getBranch("item").addBranch("name", "lore");

		getBranch("pylons").addBranch("name", "material", "password", "clearpassword", "add", "remove");
		getBranch("pylons").getBranch("name").addBranch("<ALL_PYLONS>").addBranch("<NEW_NAME>");
		getBranch("pylons").getBranch("material").addBranch("<ALL_PYLONS>").addBranch("<MATERIAL>");
		getBranch("pylons").getBranch("password").addBranch("<ALL_PYLONS>");
		getBranch("pylons").getBranch("clearpassword").addBranch("<ALL_PYLONS>");
		getBranch("pylons").getBranch("add").addBranch("<ONLINE_PLAYERS>");
		getBranch("pylons").getBranch("remove").addBranch("<ONLINE_PLAYERS>");

		getBranch("track").addBranch("<ALL_STATS>");

		getBranch("autolight").addBranch("<RANGE>");

		getBranch("addsp").addBranch("<VALUE>");
	}

	@EdenCommandHandler(argsCount = 1, description = "Toggles treecapitator.", usage = "aur trees")
	public void edencommand_trees(CommandSender sender, String commandLabel, String[] args)
	{
		ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) sender).getModuleData("Aurora");
		//AuroraModule.getPlayerData((Player) sender).setTreeCapEnabled(!AuroraModule.getPlayerData((Player) sender).isTreeCapEnabled());
		
		if (data.toggleValue("treecapEnabled"))
		{
			PrintUtils.sendMessage(sender, "Enabled treecapitator.");
			return;
		}
			
		PrintUtils.sendMessage(sender, "Disabled treecapitator.");
	}

	@EdenCommandHandler(argsCount = 1, description = "/!\\ Do not use.", usage = "aur forremoval")
	public void edencommand_forremoval(CommandSender sender, String commandLabel, String[] args)
	{
		ItemStack held = ((Player) sender).getEquipment().getItemInMainHand();

		held.removeEnchantment(Enchantment.MENDING);
		held.addUnsafeEnchantment(Enchantment.MENDING, 1);

		PrintUtils.sendMessage(sender, "§c/!\\ This command is set for removal.");
	}

	@EdenCommandHandler(usage = "aur clean", description = "Removes any issues caused by swansong.", argsCount = 1)
	public void edencommand_clean(CommandSender sender, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
			return;

		Player player = (Player) sender;
		player.setInvulnerable(false);

		PrintUtils.sendMessage(sender, "Clean success.");
	}
	
	@EdenCommandHandler(usage = "aur pylons [name | material | password | clearpassword | add | remove]", description = "Modify a pylon's information.", argsCount = 3)
	public void edencommand_pylons(CommandSender sender, String commandLabel, String[] args)
	{
		Pylon pylon;
		Player player;
		switch (args[1])
		{
			case "name":
				if (args.length < 4)
				{
					PrintUtils.sendMessage(sender, "Usage: /aur pylons name <current name> <new name>");
					return;
				}

				pylon = PylonNetwork.getPylonByNameExact(args[2]);
				
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				// Passwords
				if (pylon.hasPassword())
				{
					if (!pylon.isAllowedPlayer((Player) sender))
					{
						ChatAction pylonRename = new ChatAction()
						{
							@Override
							public void run()
							{
								String input = ChatUtils.receiveResult(sender);
								ChatUtils.deleteResult(sender);

								if (!pylon.comparePassword(input))
								{
									PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
									PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
									return;
								}

								pylon.setName(args[3]);
								PrintUtils.sendMessage(sender, "Updated pylon name.");
							}
						};

						ChatUtils.expectChat(sender, pylonRename);
						PrintUtils.sendMessage(sender, "Please enter the password for this pylon. It is now safe to enter your password.");
						return;
					}
				}
				
				pylon.setName(args[3]);
				PrintUtils.sendMessage(sender, "Updated pylon name.");
					
				break;
			case "material":
				if (args.length < 4)
				{
					PrintUtils.sendMessage(sender, "Usage: /aur pylons material <name> <material>");
					return;
				}

				pylon = PylonNetwork.getPylonByNameExact(args[2]);
				
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				// Passwords
				if (pylon.hasPassword())
				{
					if (!pylon.isAllowedPlayer((Player) sender))
					{
						ChatAction pylonMaterial = new ChatAction()
						{
							@Override
							public void run()
							{
								String input = ChatUtils.receiveResult(sender);
								ChatUtils.deleteResult(sender);

								if (!pylon.comparePassword(input))
								{
									PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
									PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
									return;
								}

								try {
									Material material = Material.valueOf(args[3].toUpperCase());
									if (!material.isItem())
										throw new IllegalArgumentException();
									
									pylon.setMaterial(material);
									
									PrintUtils.sendMessage(sender, "Updated pylon icon. It is no longer safe to enter your password.");
								} catch (IllegalArgumentException e) {
									PrintUtils.sendMessage(sender, "Invalid material type. See https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of valid materials. It is no longer safe to enter your password.");
									return;
								}
							}
						};

						ChatUtils.expectChat(sender, pylonMaterial);
						PrintUtils.sendMessage(sender, "Please enter the password for this pylon. It is now safe to enter your password.");
						return;
					}
				}
				
				try {
					Material material = Material.valueOf(args[3].toUpperCase());
					if (!material.isItem())
						throw new IllegalArgumentException();
					
					pylon.setMaterial(material);
					
					PrintUtils.sendMessage(sender, "Updated pylon icon.");
				} catch (IllegalArgumentException e) {
					PrintUtils.sendMessage(sender, "Invalid material type. See https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of valid materials.");
					return;
				}
				break;
			case "password":
				pylon = PylonNetwork.getPylonByNameExact(args[2]);
					
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				ChatAction pylonNewpass = new ChatAction()
				{
					@Override
					public void run()
					{
						String input = ChatUtils.receiveResult(sender);
						ChatUtils.deleteResult(sender);

						pylon.setPassword(input);
						PrintUtils.sendMessage(sender, "Password set. It is no longer safe to enter your password.");
					}
				};

				if (pylon.hasPassword())
				{
					ChatAction pylonCurrentpass = new ChatAction()
					{
						@Override
						public void run()
						{
							String input = ChatUtils.receiveResult(sender);
							ChatUtils.deleteResult(sender);	

							if (!pylon.comparePassword(input))
							{
								PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
								PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
								return;
							}

							PrintUtils.sendMessage(sender, "Enter a new password:");
							ChatUtils.expectChat(sender, pylonNewpass);
						}
					};

					PrintUtils.sendMessage(sender, "Please enter the current password for this pylon. It is now safe to enter your password.");
					ChatUtils.expectChat(sender, pylonCurrentpass);
					return;
				}

				PrintUtils.sendMessage(sender, "Enter a new password:");
				ChatUtils.expectChat(sender, pylonNewpass);

				break;
			case "clearpassword":
				pylon = PylonNetwork.getPylonByNameExact(args[2]);
					
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				ChatAction pylonCurrentpass = new ChatAction()
				{
					@Override
					public void run()
					{
						String input = ChatUtils.receiveResult(sender);
						ChatUtils.deleteResult(sender);	

						if (!pylon.comparePassword(input))
						{
							PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
							PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
							return;
						}

						pylon.clearPassword();
						PrintUtils.sendMessage(sender, "Cleared password. It is no longer safe to enter your password.");
					}
				};

				if (sender instanceof Player)
					if (Eden.getPermissionsManager().isElevated((Player) sender))
					{
						pylon.clearPassword();
						PrintUtils.sendMessage(sender, "Cleared password for pylon due to elevation.");
						return;
					}

				PrintUtils.sendMessage(sender, "Please enter the current password for this pylon. It is now safe to enter your password.");
				ChatUtils.expectChat(sender, pylonCurrentpass);
				break;
			case "add":
				pylon = PylonNetwork.getPylonByNameExact(args[2]);
						
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				if (args.length < 4)
				{
					PrintUtils.sendMessage(sender, "A player must be specified.");
					return;
				}

				player = Bukkit.getPlayerExact(args[3]);

				if (player == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a player by that name.");
					return;
				}

				if (!pylon.hasPassword())
				{
					PrintUtils.sendMessage(sender, "This pylon is public, players cannot be whitelisted.");
					return;
				}

				if (!pylon.isAllowedPlayer((Player) sender))
				{
					ChatAction pylonCurrentpass_ = new ChatAction()
					{
						@Override
						public void run()
						{
							String input = ChatUtils.receiveResult(sender);
							ChatUtils.deleteResult(sender);	

							if (!pylon.comparePassword(input))
							{
								PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
								PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
								return;
							}

							pylon.addAllowedPlayer(player);
							PrintUtils.sendMessage(sender, "Whitelisted player. It is no longer safe to enter your password.");
						}
					};

					ChatUtils.expectChat(sender, pylonCurrentpass_);
					PrintUtils.sendMessage(sender, "Please enter the password for this pylon. It is now safe to enter your password.");
					return;
				}

				pylon.addAllowedPlayer(player);
				PrintUtils.sendMessage(sender, "Whitelisted player. It is no longer safe to enter your password.");
				break;
			case "remove":
				pylon = PylonNetwork.getPylonByNameExact(args[2]);
							
				if (pylon == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a pylon by that name.");
					return;
				}

				if (args.length < 4)
				{
					PrintUtils.sendMessage(sender, "A player must be specified.");
					return;
				}

				player = Bukkit.getPlayerExact(args[3]);

				if (player == null)
				{
					PrintUtils.sendMessage(sender, "Could not find a player by that name.");
					return;
				}

				if (!pylon.hasPassword())
				{
					PrintUtils.sendMessage(sender, "This pylon is public, players cannot be blacklisted.");
					return;
				}

				if (!pylon.isAllowedPlayer((Player) sender))
				{
					ChatAction pylonCurrentpass_ = new ChatAction()
					{
						@Override
						public void run()
						{
							String input = ChatUtils.receiveResult(sender);
							ChatUtils.deleteResult(sender);	

							if (!pylon.comparePassword(input))
							{
								PrintUtils.sendMessage(sender, "Incorrect password for pylon. If you do not remember your password, please contact Yuki_emeralis.");
								PrintUtils.sendMessage(sender, "It is no longer safe to enter your password.");
								return;
							}

							pylon.removeAllowedPlayer(player);
							PrintUtils.sendMessage(sender, "Removed player from whitelist. It is no longer safe to enter your password.");
						}
					};

					ChatUtils.expectChat(sender, pylonCurrentpass_);
					PrintUtils.sendMessage(sender, "Please enter the password for this pylon. It is now safe to enter your password.");
					return;
				}

				pylon.removeAllowedPlayer(player);
				PrintUtils.sendMessage(sender, "Removed player from whitelist. It is no longer safe to enter your password.");
				break;
			default:
				this.sendErrorMessage(sender, args[1], "pylons");
		}
	}

	// TODO This
	@EdenCommandHandler(usage = "aur item [name | lore] data <formatting>", description = "Item meta changing. This command is unsafe.", argsCount = 4)
	public void edencommand_item(CommandSender sender, String commandLabel, String[] args)
	{
		String data, formatting = "";

		switch (args[1])
		{
			case "name":
				// Single formatting:                          /aur item name Item_name lo
				// Gradient formatting w/o regular formatting: /aur item name Item_name 00FF00 FF00FF
				// Gradient formatting w/ regular formatting:  /aur item name Item_name lo 00FF00 FF00FF
				// Replace _ with spaces, and & with §

				data = args[2].replaceAll("_", " ").replaceAll("&", "§");

				if (args.length == 5) // Gradient formatting
				{
					data = "§r" + ChatUtils.of(data, args[3], args[4]);
				} else if (args.length == 6) { // Gradient formatting + regular formatting
					for (String str : args[3].split(""))
						formatting = formatting + "§" + str;

					data = "§r" + ChatUtils.of(data, args[4], args[5], formatting);
				} else { // Regular formatting
					for (String str : args[3].split(""))
						formatting = formatting + "§" + str;
					
					data = "§r" + formatting + data;
				}

				ItemUtils.applyName(((Player) sender).getInventory().getItemInMainHand(), data);
				break;
			case "lore":
				// Single formatting:            /aur item lore slot lore_lore_lore formatting
				// Gradient formatting:          /aur item lore slot lore_lore_lore hex1 hex2
				// Gradient + normal formatting: /aur item lore slot lore_lore_lore formatting hex1 hex2
				data = args[3].replaceAll("_", " ").replaceAll("&", "§");

				if (args.length == 5) { // Regular formatting
					for (String str : args[4].split(""))
						formatting = formatting + "§" + str;

					data = "§r" + formatting + data;
				} else if (args.length == 6) { // Gradient formatting
					data = "§r" + ChatUtils.of(data, args[4], args[5]);
				} else { // Gradient formatting + regular formatting
					for (String str : args[4].split(""))
						formatting = formatting + "§" + str;

					data = "§r" + ChatUtils.of(data, args[5], args[6], formatting);
				}

				ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
				List<String> lore = meta.getLore();

				if (lore == null)
					lore = new ArrayList<>();
				int slot = Integer.parseInt(args[2]);

				if (lore.size() < slot)
					for (int i = 0; i <= slot - lore.size(); i++)
						lore.add(" ");

				lore.set(slot - 1, data);
				meta.setLore(lore);
				((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
				break;
			default:
				this.sendErrorMessage(sender, args[1], "item");
				return;
		}
	}

	@EdenCommandHandler(usage = "aur mobs <mobclass>", description = "Summons a custom mob.", argsCount = 2)
	public void edencommand_mob(CommandSender sender, String commandLabel, String[] args)
	{
		ItemStack held = ((Player) sender).getInventory().getItemInMainHand();
		ItemUtils.applyEnchantment(held, Enchantment.MENDING, 1);
		/**
		Class<? extends EntityInsentient> mobClass;
		try {
			mobClass = (Class<? extends EntityInsentient>) Class.forName("fish.yukiemeralis.aurora.mobs." + args[1]);
		} catch (ClassNotFoundException e) {
			PrintUtils.sendMessage(sender, "Could not find a custom mob named \"" + args[1] + "\".");
			return;
		}

		Location target = ((Player) sender).getTargetBlock(null, 30).getLocation().add(0, 1, 0);
		try {
			Constructor<? extends EntityInsentient> constructor = mobClass.getConstructor(Location.class);
			EntityInsentient mob = constructor.newInstance(target);

			((CraftWorld) target.getWorld()).getHandle().addEntity(mob);
			mob.setLocation(target.getX(), target.getY(), target.getZ(), 0.0f, 0.0f);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			PrintUtils.sendMessage(sender, "Invalid constructor given to this mob.");
		}
		*/
	}

	@EdenCommandHandler(usage = "aur skills", description = "Opens the skills GUI.", argsCount = 1)
	public void edencommand_skills(CommandSender sender, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
			return;

		AuroraRpgStats.genSkillsGui((Player) sender).display((Player) sender);
	}

	@EdenCommandHandler(usage = "aur addsp", description = "/!\\ Add a skill point.", argsCount = 1)
	public void edencommand_addsp(CommandSender sender, String commandLabel, String[] args)
	{
		ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) sender).getModuleData("AuroraRPG");

		int value = 1;

		if (args.length > 1)
		{
			try {
				value = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				PrintUtils.sendMessage(sender, "§cInvalid integer. Input must be any integer between -2147483648 and 2147483647.");
				return;
			}
		}
	
		data.incrementInt("skillpoints", value);
		PrintUtils.sendMessage(sender, "Success.");
	}

	@EdenCommandHandler(usage = "aur stats", description = "View your RPG stats.", argsCount = 1)
	public void edencommand_stats(CommandSender sender, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
			return;

		ModulePlayerData data = Eden.getPermissionsManager().getPlayerData((Player) sender).getModuleData("AuroraRPG");

		PrintUtils.sendMessage(sender, "§e---§6=§c[ §bYour stats §c]§6=§e---");

		for (RpgStat stat : RpgStat.values())
			PrintUtils.sendMessage(sender, "§e" + stat.getFriendlyName() + "§7 lv. " + data.getInt(stat.dataName()) + " | §aExp§7: " + data.getInt(stat.expName()) + "/" + stat.getRequiredExpAtLevel(data.getInt(stat.dataName())));
	}

	@EdenCommandHandler(usage = "aur track <stat>", description = "Tracks stat progression.", argsCount = 1)
	public void edencommand_track(CommandSender sender, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
			return;

		if (args.length == 1)
		{
			if (!AuroraRpgStats.hasBar((Player) sender))
			{
				PrintUtils.sendMessage(sender, "§cYou aren't tracking and stats right now.");
				return;
			}

			PrintUtils.sendMessage(sender, "Turned off tracking for stat §e" + AuroraRpgStats.getBarData((Player) sender).getA().getFriendlyName() + "§7.");
			AuroraRpgStats.removeBar((Player) sender);
			return;
		}

		// Clean up the old bar
		if (AuroraRpgStats.hasBar((Player) sender))
			AuroraRpgStats.removeBar((Player) sender);

		try {
			RpgStat stat = RpgStat.valueOf(args[1].toUpperCase());

			AuroraRpgStats.registerNewBar((Player) sender, stat);

			PrintUtils.sendMessage(sender, "Now tracking stat \"§e" + stat.getFriendlyName() + "§7\".");
		} catch (IllegalArgumentException e) {
			PrintUtils.sendMessage(sender, "§cInvalid stat name \"" + args[1] + "\".");
		}
	}

	private static final Map<Material, Object> REPLACABLE_BLOCKS = new HashMap<>() {{
		put(Material.AIR, null);
		put(Material.WATER, null);
		put(Material.GRASS, null);
		put(Material.SNOW, null);
		put(Material.TALL_GRASS, null);
	}};

	@EdenCommandHandler(usage = "aur autolight <range>", description = "Automatically places torches to light up the surrounding area.", argsCount = 2)
	public void edencommand_autolight(CommandSender sender, String commandLabel, String[] args)
	{
		int range;

		try {
			range = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			PrintUtils.sendMessage(sender, "Invalid input. Expected number, received \"" + args[1] + "\"");
			return;
		}

		World world = ((Player) sender).getWorld();
		Location center = ((Player) sender).getLocation().clone();

		//
		// Threading
		//
		Thread t = new Thread() 
		{
			int placed = 0;

			public void run()
			{
				long time = System.currentTimeMillis();

				boolean glowstoneWarned = false;

				for (int y = center.getBlockY() - range; y < center.getBlockY() + range; y++)
				{
					if (y < -63)
						continue;
						
					for (int x = (range * -1); x < range; x++)
						loop: for (int z = (range * -1); z < range; z++)
						{
							Block block = world.getBlockAt(center.getBlockX() + x, y, center.getBlockZ() + z);

							if (block == null)
								continue loop;

							// Logic
							if (!block.getType().isSolid())
								continue loop;

							if (block.getRelative(BlockFace.UP).getLightFromBlocks() != 0)
								continue loop;

							if (!isReplacable(block.getRelative(BlockFace.UP)))
								continue loop;

							// Water/glowstone
							if (block.getRelative(BlockFace.UP).getType().equals(Material.WATER))
							{
								if (!removeItem((Player) sender, Material.GLOWSTONE))
								{
									if (!glowstoneWarned)
									{
										glowstoneWarned = true;
										PrintUtils.sendMessage(sender, "Your inventory has no glowstone. Water will remain unlit.");
									}

									continue loop;
								}

								// If the player has gotten more glowstone since they ran out, continue to place glowstone
								if (glowstoneWarned)
								{
									PrintUtils.sendMessage(sender, "Continuing to place glowstone. There may be dark patches, perform another run to light them.");
									glowstoneWarned = false;
								}

								z += 3;
								placeBlockAndWaitTick(block, Material.GLOWSTONE);
								continue loop;
							}
							
							// Air/torches
							if (!removeItem((Player) sender, Material.TORCH))
							{
								PrintUtils.sendMessage(sender, "Your inventory has no more torches to place.");
								return;
							}

							z += 3;
							placeBlockAndWaitTick(block, Material.TORCH);
						}
				}

				long elapsed = System.currentTimeMillis() - time;
				PrintUtils.sendMessage(sender, "Autolight completed in " + (elapsed / 1000d) + "s, with " + placed + " lights placed. (" + (placed/(elapsed / 1000f)) + " lights/s)");
			}

			private void placeBlockAndWaitTick(Block block, Material material)
			{
				Bukkit.getScheduler().runTask(Eden.getInstance(), () -> {
					block.getRelative(BlockFace.UP).setType(material);

					// Wake up
					synchronized (this)
					{
						this.notify();
					}
				});

				placed++;

				// Sleep
				synchronized (this)
				{
					try {
						this.wait();
					} catch (InterruptedException e) {
						PrintUtils.log("Autolight thread encountered an error starting or during wait cycle!", InfoType.WARN);
					}
				}
			}
		};
		
		PrintUtils.sendMessage(sender, "Beginning autolight...");
		t.start();
	}

	/**
	 * Removes an item from the target's inventory. If the player doesn't have an instance of that item, returns false.
	 * @param player
	 */
	private static boolean removeItem(Player player, Material material)
	{
		int slot = player.getInventory().first(material);

		if (slot == -1)
			return false;

		player.getInventory().getItem(slot).setAmount(player.getInventory().getItem(slot).getAmount() - 1);
		return true;
	}

	private static boolean isReplacable(Block block)
	{
		if (block == null)
			return false;
		return REPLACABLE_BLOCKS.containsKey(block.getType());
	}
}