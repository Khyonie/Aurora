package fish.yukiemeralis.aurora.rpg;

import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public class SkillResult 
{
    private final Tuple2<Boolean, Boolean> data;    

    public SkillResult(boolean cancel, boolean ret)
    {
        data = new Tuple2<Boolean,Boolean>(cancel, ret);
    }

    public boolean getCancel()
    {
        return data.getA();
    }

    public boolean getReturn()
    {
        return data.getB();
    }

    public Tuple2<Boolean, Boolean> getHost()
    {
        return this.data;
    }
}