package fish.yukiemeralis.aurora;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import fish.yukiemeralis.aurora.pylons.Pylon;
import fish.yukiemeralis.aurora.pylons.PylonNetwork;
import fish.yukiemeralis.aurora.pylons.item.Pylporter;
import fish.yukiemeralis.aurora.rpg.RpgStatCompletions;
import fish.yukiemeralis.aurora.rpg.RpgStatListener;

import org.bukkit.Material;

import fish.yukiemeralis.eden.Eden;
import fish.yukiemeralis.eden.core.CompletionsManager;
import fish.yukiemeralis.eden.core.CompletionsManager.ObjectMethodPair;
import fish.yukiemeralis.eden.module.EdenModule;
import fish.yukiemeralis.eden.module.EdenModule.EdenConfig;
import fish.yukiemeralis.eden.module.EdenModule.LoadBefore;
import fish.yukiemeralis.eden.module.EdenModule.ModInfo;
import fish.yukiemeralis.eden.module.annotation.ModuleFamily;
import fish.yukiemeralis.eden.module.java.annotations.DefaultConfig;
import fish.yukiemeralis.eden.utils.JsonUtils;
import fish.yukiemeralis.eden.utils.PrintUtils;

@ModInfo(
    modName = "Aurora",
    description = "Various fun/useful things for my SMPs.",
    maintainer = "Yuki_emeralis",
    modIcon = Material.SALMON,
    version = "1.3.2",
    supportedApiVersions = {"v1_16_R3", "v1_17_R1", "v1_18_R1", "v1_18_R2"}
)
@LoadBefore(loadBefore = {"Surface2", "Checkpoint"})
@EdenConfig
@DefaultConfig(
    keys =   {"1.17_weather_fix", "enforce_security"}, 
    values = {"false",            "true"}
)
@ModuleFamily(name = "Aurora", icon = Material.SALMON)
public class AuroraModule extends EdenModule
{
    private static AuroraModule instance;

    static List<String> DEFAULT_COMMAND_PERMISSIONS = new ArrayList<>() {{
        add("Aurora.aur");
        add("Aurora.aur.trees");
        add("Aurora.aur.pylons");
        add("Aurora.aur.pylons.name");
        add("Aurora.aur.pylons.material");
        add("Aurora.aur.pylons.password");
        add("Aurora.aur.pylons.clearpassword");
        add("Aurora.aur.pylons.add");
        add("Aurora.aur.pylons.remove");
        add("Aurora.aur.stats");
        add("Aurora.aur.skills");
        add("Aurora.aur.track");
    }};

    static List<String> ADMIN_COMMAND_PERMISSIONS = new ArrayList<>() {{
        add("Aurora.aur.item");
        add("Aurora.aur.item.name");
        add("Aurora.aur.item.lore");
        add("Aurora.aur.addsp");
    }};
	
    public AuroraModule()
    {
        instance = this;
        try {
            CompletionsManager.registerCompletion("ALL_PYLONS", new ObjectMethodPair(PylonNetwork.getInstance(), "getAllPylonNames"), true);
            CompletionsManager.registerCompletion("ALL_STATS", new ObjectMethodPair(new RpgStatCompletions(), "getRpgStats"), true);
        } catch (NoSuchMethodException e) {
            PrintUtils.printPrettyStacktrace(e);
        }

        RpgStatListener.initRegister();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() 
    { 
        // Re-register pylons
        File pylonfile = new File("./plugins/Eden/AuroraPylons.json");
        
        if (!pylonfile.exists())
        	JsonUtils.toJsonFile(pylonfile.getAbsolutePath(), PylonNetwork.getActivePylons());
        
        Type listType = new TypeToken<ArrayList<Pylon>>() {}.getType();
        
        List<Pylon> pylons = (List<Pylon>) JsonUtils.fromJsonFile(pylonfile.getAbsolutePath(), listType);
        PylonNetwork.updatePylonList(pylons);

        // If the module was hotloaded, handle data
        Eden.getInstance().getServer().getOnlinePlayers().forEach(p -> {
            if (!Eden.getPermissionsManager().getPlayerData(p).hasModuleData("Aurora"))
                Eden.getPermissionsManager().getPlayerData(p).createModuleData(modName, AccountListener.defaultData);
        });

        Pylporter.register();
    }

    @Override
    public void onDisable() 
    {
    	File pylonfile = new File("./plugins/Eden/AuroraPylons.json");
    	JsonUtils.toJsonFile(pylonfile.getAbsolutePath(), PylonNetwork.getActivePylons());
    	
    	Pylporter.deregister();
    }

    public static EdenModule getModuleInstance()
    {
        return instance;
    }  
}
