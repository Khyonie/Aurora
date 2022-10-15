package coffee.khyonieheart.aurora.rpg;

import java.util.Arrays;
import java.util.List;

import coffee.khyonieheart.aurora.rpg.enums.RpgStat;
import coffee.khyonieheart.eden.utils.DataUtils;

public class RpgStatCompletions 
{
    private static final List<String> data = DataUtils.mapList(Arrays.asList(RpgStat.values()), (stat) -> stat.name());

    public List<String> getRpgStats()
    {
        return data;
    }    
}
