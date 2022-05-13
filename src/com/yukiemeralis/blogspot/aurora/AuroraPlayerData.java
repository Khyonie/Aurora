package com.yukiemeralis.blogspot.aurora;

/**
 * Old handler for Aurora player data.
 * @deprecated Use {@link com.yukiemeralis.blogspot.zenith.permissions.ModulePlayerData} instead. This class has been marked for removal.
 */
@Deprecated(forRemoval = true)
public class AuroraPlayerData
{
	private boolean treeCapEnabled = true;

	public boolean isTreeCapEnabled()
	{
		return treeCapEnabled;
	}

	public void setTreeCapEnabled(boolean treeCapEnabled)
	{
		this.treeCapEnabled = treeCapEnabled;
	}
}
