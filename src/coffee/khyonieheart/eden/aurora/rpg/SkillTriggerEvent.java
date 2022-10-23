package coffee.khyonieheart.eden.aurora.rpg;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import coffee.khyonieheart.eden.aurora.rpg.skill.AbstractSkill;

public class SkillTriggerEvent extends Event
{
    private static final HandlerList handlerList = new HandlerList();

    private final AbstractSkill<?> skill;
    private final Event event;
    
    public SkillTriggerEvent(AbstractSkill<?> skill, Event event)
    {
        this.skill = skill;
        this.event = event;
    }

    public AbstractSkill<?> getSkill()
    {
        return this.skill;
    }

    public Event getEvent()
    {
        return this.event;
    }

    /** @return HandlerList */
    public static HandlerList getHandlerList()
    {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() 
    {
        return handlerList;
    }
    
}
