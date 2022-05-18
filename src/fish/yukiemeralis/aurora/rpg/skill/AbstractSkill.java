package fish.yukiemeralis.aurora.rpg.skill;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import fish.yukiemeralis.aurora.rpg.RpgStatListener;
import fish.yukiemeralis.aurora.rpg.enums.AuroraSkill;
import fish.yukiemeralis.eden.utils.tuple.Tuple2;

public abstract class AbstractSkill<E extends Event>
{
    protected AuroraSkill skill;
    protected Class<E> handler;

    protected AbstractSkill(AuroraSkill skill, Class<E> handler)
    {
        this.skill = skill;
        RpgStatListener.register(this, handler);
    }

    /**
     * Tuple A = cancel event
     * Tuple B = return
     * @param event
     * @param skillOwner
     * @return
     */
    public Tuple2<Boolean, Boolean> tryActivate(Event event, Player skillOwner)
    {
        if (!skill.isUnlocked(skillOwner))
            return new Tuple2<>(false, false);
        if (!skill.proc())
            return new Tuple2<>(false, false);
        if (!handler.isAssignableFrom(event.getClass()))
            throw new IllegalArgumentException("Given event " + event.getClass().getSimpleName() + " is not applicable for skill expecting an instance of " + handler.getSimpleName());
        return onActivate(handler.cast(event));
    }

    protected abstract Tuple2<Boolean, Boolean> onActivate(E event);    

    public AuroraSkill getEnum()
    {
        return this.skill;
    }
}
