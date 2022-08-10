package org.mooner.moonerbungeeapi.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.mooner.moonerbungeeapi.api.events.BungeeMessageEvent;

public class BungeeListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Bukkit.getPluginManager().callEvent(new BungeeMessageEvent(channel, player, message));
    }
}
