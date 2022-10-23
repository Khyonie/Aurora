package coffee.khyonieheart.eden.aurora.pylons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PylonNetwork
{
	private static final List<Pylon> ACTIVE_PYLONS = new ArrayList<>();
	private static PylonNetwork instance;

	public PylonNetwork()
	{
		instance = this;
	}

	public static PylonNetwork getInstance()
	{
		if (instance == null)
		{
			new PylonNetwork();
		}

		return instance;
	}
	
	public static List<Pylon> getActivePylons()
	{
		return ACTIVE_PYLONS;
	}
	
	public static void updatePylonList(List<Pylon> pylons)
	{
		ACTIVE_PYLONS.addAll(pylons);
		pylons.forEach(p -> p.init());
	}
	
	public static void register(Pylon pylon)
	{
		ACTIVE_PYLONS.add(pylon);
	}
	
	public static void unregister(Pylon pylon)
	{
		ACTIVE_PYLONS.remove(pylon);
	}
	
	public static Pylon getPylonAssociatedWith(Block block)
	{
		for (Pylon p : ACTIVE_PYLONS)
		{
			if (p.getAssociatedBlocks().contains(block))
				return p;
		}
		return null;
	}
	
	public static Pylon getPylonByNameExact(String name)
	{
		for (Pylon p : ACTIVE_PYLONS)
			if (p.getName().equals(name))
				return p;
		return null;
	}
	
	public static List<Pylon> getPylonsByName(String name)
	{
		List<Pylon> pylons = new ArrayList<>();
		for (Pylon p : ACTIVE_PYLONS)
			if (p.getName().startsWith(name))
				pylons.add(p);
		
		return pylons;
	}

	public static List<String> getAllPylonNames()
	{
		List<String> buffer = new ArrayList<>();

		for (Pylon p : ACTIVE_PYLONS)
			buffer.add(p.getName());

		return buffer;
	}
	
	public static boolean isValidPylon(Block block)
	{
		if (!block.getType().equals(Material.BELL))
			return false;
		if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.PURPUR_PILLAR))
			return false;
		if (!block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType().equals(Material.END_STONE_BRICKS))
			return false;
		for (String dir : Pylon.CARDINAL_DIRECTIONS)
			if (!block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(BlockFace.valueOf(dir)).getType().equals(Material.END_STONE_BRICKS))
				return false;
		return true;
	}
}
