package org.mooner.moonerbungeeapi.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class BungeeMessageEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final String channel;
    private final Player player;
    private final UUID uuid;
    private final String cleanMessage;
    private final String message;

    public BungeeMessageEvent(String channel, Player player, UUID uuid, String cleanMessage, String message) {
        this.channel = channel;
        this.player = player;
        this.uuid = uuid;
        this.cleanMessage = cleanMessage;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public String getCleanMessage() {
        return cleanMessage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
