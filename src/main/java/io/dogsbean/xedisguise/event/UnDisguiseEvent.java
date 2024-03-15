package io.dogsbean.xedisguise.event;

import io.dogsbean.xedisguise.disguise.Disguise;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class UnDisguiseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Disguise disguise;

    public UnDisguiseEvent(Player player, Disguise disguise) {
        this.player = player;
        this.disguise = disguise;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
