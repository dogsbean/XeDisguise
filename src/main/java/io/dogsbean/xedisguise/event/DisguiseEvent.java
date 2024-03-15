package io.dogsbean.xedisguise.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class DisguiseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String originalName;
    private final String disguisedName;
    private final boolean disguised;

    public DisguiseEvent(Player player, String originalName, String disguisedName, boolean disguised) {
        this.player = player;
        this.originalName = originalName;
        this.disguisedName = disguisedName;
        this.disguised = disguised;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
