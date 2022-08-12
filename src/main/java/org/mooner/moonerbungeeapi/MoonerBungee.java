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
import org.mooner.moonerbungeeapi.api.KeyWordDB;
import org.mooner.moonerbungeeapi.db.PlayTimeDB;

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
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener());
        PlayTimeDB.init = new PlayTimeDB();
        KeyWordDB.init = new KeyWordDB();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayTimeDB.init.savePlayTime(player);
        }
        this.getLogger().info("Plugin Disabled!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayTimeDB.init.recordPlayTime(e.getPlayer());
        PlayTimeDB.init.savePlayTimeAsync(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayTimeDB.init.savePlayTime(e.getPlayer());
    }

//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        switch (command.getName()) {
//            case "keyword" -> {
//                if(!(sender instanceof Player p)) return true;
//                if (args.length == 0) {
//                    sender.sendMessage(chat("&c사용법: /keyword <키워드>"));
//                    sender.sendMessage(chat("&e======== &a등록된 키워드 &e========"));
//                    for (String key : KeyWordDB.init.getKeyWords(p)) sender.sendMessage(chat("&a  - &6" + key));
//                    sender.sendMessage(chat("&e======== &a등록된 키워드 &e========"));
//                } else {
//                    StringBuilder builder = new StringBuilder();
//                    for (int i = 1; i < args.length; i++) builder.append(args[i]);
//                    final String s = builder.toString();
//                    if(KeyWordDB.init.removeKeyWord(p, s)) {
//                        sender.sendMessage(chat("&a성공적으로 해당 키워드를 제거했습니다."));
//                    } else if (KeyWordDB.init.addKeyWord(p, s)) {
//                        sender.sendMessage(chat("&a성공적으로 키워드를 추가했습니다."));
//                    }
//                }
//                return true;
//            }
//        }
//        return false;
//    }
}
