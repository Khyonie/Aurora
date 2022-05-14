package com.yukiemeralis.blogspot.aurora;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.auth.EdenPermissionManager;
import fish.yukiemeralis.eden.module.event.EdenFinishLoadingEvent;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.PrintUtils.InfoType;

public class PermissionsInjectListener implements Listener
{
    @EventHandler
    public void onLoad(EdenFinishLoadingEvent event)
    {
        if (Eden.getPermissionsManager() instanceof EdenPermissionManager)
        {
            PrintUtils.logVerbose("Injecting aurora permissions into EdenPermissionManager...", InfoType.INFO);
            // Inject default permissions
            EdenPermissionManager pm = (EdenPermissionManager) Eden.getPermissionsManager();
            for (String perm : AuroraModule.DEFAULT_COMMAND_PERMISSIONS)
            {
                if (!pm.getGroup("default").hasPermission(perm))
                    pm.getGroup("default").addPermission(perm);
            }

            for (String perm : AuroraModule.ADMIN_COMMAND_PERMISSIONS)
            {
                if (!pm.getGroup("administrator").hasPermission(perm))
                    pm.getGroup("administrator").addPermission(perm);
            }
        }
    }
}
