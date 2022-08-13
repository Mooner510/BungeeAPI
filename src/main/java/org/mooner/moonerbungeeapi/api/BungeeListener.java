package org.mooner.moonerbungeeapi.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.mooner.moonerbungeeapi.api.events.BungeeMessageEvent;

import java.util.UUID;

public class BungeeListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
//        MoonerBungee.plugin.getLogger().info(player.getName() + ": [" + channel + "] " + new String(message, StandardCharsets.UTF_8));
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String sub = in.readUTF();
        final UUID uuid = UUID.fromString(in.readUTF());
        final String msg = in.readUTF();
        Bukkit.getPluginManager().callEvent(new BungeeMessageEvent(sub, player, uuid, msg));
    }
}
