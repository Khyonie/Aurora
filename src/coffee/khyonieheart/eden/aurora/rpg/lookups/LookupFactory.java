package coffee.khyonieheart.eden.aurora.rpg.lookups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public class LookupFactory 
{
    public static Map<String, List<Material>> generateValidMaterials(String... query)
    {
        Map<String, List<Material>> data = new HashMap<>();

        for (String s : query)
            data.put(s, new ArrayList<>());

        for (Material m : Material.values())
        {
            if (m.name().contains("LEGACY"))
                continue;
                
            for (String s : query)
                if (m.name().contains(s))
                    data.get(s).add(m);
        }
        
        return data;
    }    
}
