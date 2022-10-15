package coffee.khyonieheart.aurora;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.checkpoint.EdenPermissionManager;
import coffee.khyonieheart.eden.checkpoint.PermissionGroup;
import coffee.khyonieheart.eden.module.event.EdenFinishLoadingEvent;
import coffee.khyonieheart.eden.utils.PrintUtils;
import coffee.khyonieheart.eden.utils.logging.Logger.InfoType;
import coffee.khyonieheart.eden.utils.option.Option;

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

            loop: for (String perm : AuroraModule.DEFAULT_COMMAND_PERMISSIONS)
            {
                // TODO Java 17 preview feature
                Option opt = pm.getGroup("default");
                switch (opt.getState())
                {
                    case SOME:
                        if (!opt.unwrap(PermissionGroup.class).hasPermission(perm))
                            opt.unwrap(PermissionGroup.class).addPermission(perm);
                        continue loop;
                    default:
                        PrintUtils.log("Aurora failed to find default permissions group. Injection failed.", InfoType.ERROR);
                        break loop;
                }
            }

            // TODO Java 17 preview feature
            loop: for (String perm : AuroraModule.ADMIN_COMMAND_PERMISSIONS)
            {
                Option opt = pm.getGroup("administrator");
            	switch (opt.getState())
                {
                    case SOME:
                        if (!opt.unwrap(PermissionGroup.class).hasPermission(perm))
                            opt.unwrap(PermissionGroup.class).addPermission(perm);
                        continue loop;
                    default:
                    	PrintUtils.log("Aurora failed to find administator permissions group. Injection failed.", InfoType.ERROR);
                        break loop;
                }
            }
        }
    }
}
