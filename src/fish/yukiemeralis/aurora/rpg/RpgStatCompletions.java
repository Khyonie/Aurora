package fish.yukiemeralis.aurora.rpg;

import java.util.Arrays;
import java.util.List;

import fish.yukiemeralis.aurora.rpg.enums.RpgStat;
import fish.yukiemeralis.eden.utils.DataUtils;

public class RpgStatCompletions 
{
    private static final List<String> data = DataUtils.mapList(Arrays.asList(RpgStat.values()), (stat) -> stat.name());

    public List<String> getRpgStats()
    {
        return data;
    }    
}
