package org.mooner.moonerbungeeapi;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeListener;
import org.mooner.moonerbungeeapi.db.PlayTimeDB;

public final class MoonerBungee extends JavaPlugin implements Listener {
    public static MoonerBungee plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "api:bungee", new BungeeListener());
        PlayTimeDB.init = new PlayTimeDB();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Plugin Disabled!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayTimeDB.init.join(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayTimeDB.init.quit(e.getPlayer());
    }
}
