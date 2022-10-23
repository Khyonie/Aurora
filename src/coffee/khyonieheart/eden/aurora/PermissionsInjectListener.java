package coffee.khyonieheart.eden.aurora;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.checkpoint.EdenPermissionManager;
import coffee.khyonieheart.eden.checkpoint.PermissionGroup;
import coffee.khyonieheart.eden.module.event.EdenFinishLoadingEvent;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.logging.Logger.InfoType;

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
                if (!pm.getGroup("default").unwrap(PermissionGroup.class).hasPermission(perm))
                    pm.getGroup("default").unwrap(PermissionGroup.class).addPermission(perm);
            }

            for (String perm : AuroraModule.ADMIN_COMMAND_PERMISSIONS)
            {
                if (!pm.getGroup("administrator").unwrap(PermissionGroup.class).hasPermission(perm))
                    pm.getGroup("administrator").unwrap(PermissionGroup.class).addPermission(perm);
            }
        }
    }
}
