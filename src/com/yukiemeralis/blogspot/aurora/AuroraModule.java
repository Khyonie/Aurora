package com.yukiemeralis.blogspot.aurora;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.yukiemeralis.blogspot.aurora.pylons.Pylon;
import com.yukiemeralis.blogspot.aurora.pylons.PylonNetwork;
import com.yukiemeralis.blogspot.aurora.pylons.item.Pylporter;

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
    description = "Various fun/useful things for Segue SMP.",
    maintainer = "Yuki_emeralis",
    modIcon = Material.SALMON,
    version = "1.1",
    supportedApiVersions = {"v1_16_R3", "v1_17_R1", "v1_18_R1", "v1_18_R2"}
)
@LoadBefore(loadBefore = "Surface")
@EdenConfig
@DefaultConfig(keys = {"1.17_weather_fix"}, values = "false")
@ModuleFamily(name = "Aurora", icon = Material.SALMON)
public class AuroraModule extends EdenModule
{
	//static Map<Player, AuroraPlayerData> PLAYER_DATA = new HashMap<>();
    private static AuroraModule instance;
	
    public AuroraModule()
    {
        instance = this;
        try {
            CompletionsManager.registerCompletion("ALL_PYLONS", new ObjectMethodPair(PylonNetwork.getInstance(), "getAllPylonNames"), true);
        } catch (NoSuchMethodException e) {
            PrintUtils.printPrettyStacktrace(e);
        }
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
