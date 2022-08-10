package org.mooner.moonerbungeeapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeListener;

public final class MoonerBungee extends JavaPlugin {
    public static MoonerBungee plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.getLogger().info("Plugin Enabled!");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "api:bungee", new BungeeListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("Plugin Disabled!");
    }
}
