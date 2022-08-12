package fish.yukiemeralis.aurora;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.auth.EdenPermissionManager;
import fish.yukiemeralis.eden.auth.PermissionGroup;
import fish.yukiemeralis.eden.module.event.EdenFinishLoadingEvent;
import fish.yukiemeralis.eden.utils.PrintUtils;
import fish.yukiemeralis.eden.utils.PrintUtils.InfoType;
import fish.yukiemeralis.eden.utils.option.Some;

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
                switch (pm.getGroup("default"))
                {
                    case Some some:
                    	PrintUtils.log("Aurora failed to find default permissions group. Injection failed.", InfoType.ERROR);
                        if (some.unwrap(PermissionGroup.class).hasPermission(perm))
                            some.unwrap(PermissionGroup.class).addPermission(perm);
                        continue;
                    case default:
                        break;
                }
            }

            for (String perm : AuroraModule.ADMIN_COMMAND_PERMISSIONS)
            {
            	switch (pm.getGroup("administrator"))
                {
                    case Some some:
                        if (some.unwrap(PermissionGroup.class).hasPermission(perm))
                            some.unwrap(PermissionGroup.class).addPermission(perm);
                        continue;
                    case default:
                    	PrintUtils.log("Aurora failed to find administator permissions group. Injection failed.", InfoType.ERROR);
                        break;
                }
            }
        }
    }
}
