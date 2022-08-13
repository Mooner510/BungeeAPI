package org.mooner.moonerbungeeapi;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.moonerbungeeapi.api.BungeeListener;
import org.mooner.moonerbungeeapi.db.ChatDB;
import org.mooner.moonerbungeeapi.db.KeyWordDB;
import org.mooner.moonerbungeeapi.db.PlayerDB;

import java.util.List;

import static org.mooner.moonerbungeeapi.api.Rank.chat;

public final class MoonerBungee extends JavaPlugin implements Listener {
    public static MoonerBungee plugin;
    public static int port;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        port = Bukkit.getServer().getPort();
        this.getLogger().info("Plugin Enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "bungee:chat");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "bungee:chat", new BungeeListener());
        PlayerDB.init = new PlayerDB();
        ChatDB.init = new ChatDB();
        KeyWordDB.init = new KeyWordDB();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDB.init.savePlayTime(player);
        }
        this.getLogger().info("Plugin Disabled!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerDB.init.recordPlayTime(e.getPlayer());
        PlayerDB.init.savePlayTimeAsync(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerDB.init.savePlayTime(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName()) {
            case "keyword" -> {
                if(!(sender instanceof Player p)) return true;
                if (args.length == 0) {
                    sender.sendMessage(chat("&c사용법: /keyword <키워드>"));
                    final List<String> keyWords = KeyWordDB.init.getKeyWords(p);
                    sender.sendMessage(chat("&e====== &a등록된 키워드 &7("+keyWords.size()+"개) &e======"));
                    for (String key : keyWords) sender.sendMessage(chat("&a  - &6" + key));
                } else {
                    final String s = String.join(" ", args);
                    if(KeyWordDB.init.removeKeyWord(p, s)) {
                        sender.sendMessage(chat("&3[ &f키워드 &3] &f성공적으로 &6"+s+" &f키워드를 &c제거&7했습니다."));
                    } else if (KeyWordDB.init.addKeyWord(p, s)) {
                        sender.sendMessage(chat("&3[ &f키워드 &3] &f성공적으로 &6"+s+" &f키워드를 &a추가&7했습니다."));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
