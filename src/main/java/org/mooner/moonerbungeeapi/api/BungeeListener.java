package org.mooner.moonerbungeeapi.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.mooner.moonerbungeeapi.api.events.BungeeMessageEvent;

public class BungeeListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String sub = in.readUTF();
        Bukkit.getPluginManager().callEvent(new BungeeMessageEvent(sub, player, in.readUTF()));
    }
}
