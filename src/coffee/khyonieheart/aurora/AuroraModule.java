package coffee.khyonieheart.aurora;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.google.gson.reflect.TypeToken;

import coffee.khyonie.eden.rosetta.CompletionsManager;
import coffee.khyonie.eden.rosetta.CompletionsManager.ObjectMethodPair;
import coffee.khyonieheart.aurora.pylons.Pylon;
import coffee.khyonieheart.aurora.pylons.PylonNetwork;
import coffee.khyonieheart.aurora.pylons.item.Pylporter;
import coffee.khyonieheart.aurora.rpg.RpgStatCompletions;
import coffee.khyonieheart.aurora.rpg.RpgStatListener;
import coffee.khyonieheart.eden.Eden;
import coffee.khyonieheart.eden.module.EdenModule;
import coffee.khyonieheart.eden.module.EdenModule.LoadBefore;
import coffee.khyonieheart.eden.module.EdenModule.ModInfo;
import coffee.khyonieheart.eden.module.annotation.EdenConfig;
import coffee.khyonieheart.eden.module.annotation.ModuleFamily;
import coffee.khyonieheart.eden.utils.JsonUtils;
import coffee.khyonieheart.eden.utils.PrintUtils;

@ModInfo(
    modName = "Aurora",
    description = "Various fun/useful things for my SMPs.",
    maintainer = "Yuki_emeralis",
    modIcon = Material.SALMON,
    version = "1.4.1",
    supportedApiVersions = {"v1_19_R1"}
)
@LoadBefore(loadBefore = {"Surface2", "Checkpoint"})
@EdenConfig
@ModuleFamily(name = "Aurora", icon = Material.SALMON)
public class AuroraModule extends EdenModule
{
    private static AuroraModule instance;

    @SuppressWarnings("unused")
    private static final Map<String, Object> EDEN_DEFAULT_CONFIG = Map.of(
        "1.17_weather_fix", false,
        "enforce_security", true,
        "free_home_pylporter", true,
        "enable_economy", false
    );

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
