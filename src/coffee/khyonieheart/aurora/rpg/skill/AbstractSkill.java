package coffee.khyonieheart.aurora.rpg.skill;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import coffee.khyonieheart.aurora.rpg.SkillResult;
import coffee.khyonieheart.aurora.rpg.enums.AuroraSkill;
import coffee.khyonieheart.eden.utils.option.Option;
import coffee.khyonieheart.eden.utils.tuple.Tuple2;

public abstract class AbstractSkill<E extends Event>
{
    protected AuroraSkill skill;
    protected Class<E> handler;

    @SuppressWarnings("unused")
    private static AbstractSkill<?> register;

    public AbstractSkill(AuroraSkill skill, Class<E> handler)
    {
        this.skill = skill;
        this.handler = handler;
    }

    /**
     * Tuple A = cancel event
     * Tuple B = return
     * @param event
     * @param skillOwner
     * @return
     */
    @SuppressWarnings("unchecked") // Cast is checked by handler#isAssignableFrom
    public Tuple2<Boolean, Boolean> tryActivate(Event event, Player skillOwner) throws IllegalArgumentException
    {
        if (!handler.isAssignableFrom(event.getClass()))
            throw new IllegalArgumentException("Given event " + event.getClass().getSimpleName() + " is not applicable for skill expecting an instance of " + handler.getSimpleName());

        if (!skill.isUnlocked(skillOwner))
            return new Tuple2<>(false, false);

        // TODO Java 17 preview feature
        Option opt = shouldActivate((E) event, skillOwner);
        switch (opt.getState())
        {
            case SOME:
                return opt.unwrap(SkillResult.class).getHost();
            default:
                break;
        }

        if (!skill.proc(skillOwner))
            return new Tuple2<>(false, false);
            
        return onActivate(handler.cast(event));
    }

    protected abstract Tuple2<Boolean, Boolean> onActivate(E event);    

    /**
     * Custom test for a skill to activate itself. Must be overridden, however usage is optional.
     * @param event
     * @param player
     * @return
     */
    protected Option shouldActivate(E event, Player player)
    {
        return Option.none();
    }

    public AuroraSkill getEnum()
    {
        return this.skill;
    }
}