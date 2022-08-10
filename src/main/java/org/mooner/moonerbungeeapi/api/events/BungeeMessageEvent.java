package org.mooner.moonerbungeeapi.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeMessageEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final String channel;
    private final Player player;
    private final String message;

    public BungeeMessageEvent(String channel, Player player, byte[] message) {
        this.channel = channel;
        this.player = player;
        this.message = new String(message);
    }

    public String getChannel() {
        return channel;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
