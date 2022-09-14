package org.mooner.moonerbungeeapi.api;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collection;

public class BungeeAPI {
    public static void send(byte[] data, boolean async) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(players.isEmpty()) return;
        Player p = players.iterator().next();
        if(p == null) return;
        if(async) {
            MoonerBungee.plugin.getServer().getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () ->
                    p.sendPluginMessage(MoonerBungee.plugin, "BungeeCord", data)
            );
        } else {
            p.sendPluginMessage(MoonerBungee.plugin, "BungeeCord", data);
        }
    }

    public static void send(Player p, byte[] data, boolean async) {
        if(p == null) return;
        if(async) {
            MoonerBungee.plugin.getServer().getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () ->
                    p.sendPluginMessage(MoonerBungee.plugin, "BungeeCord", data)
            );
        } else {
            p.sendPluginMessage(MoonerBungee.plugin, "BungeeCord", data);
        }
    }

    public static void sendBungeeMessage(String message) {
        sendBungeeMessage(message, true);
    }

    public static void sendBungeeMessage(String message, boolean async) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("Message");
            out.writeUTF("ALL");
            out.writeUTF(message);

            send(b.toByteArray(), async);
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send BungeeCord all message. " + e.getMessage());
        }
    }

    public static void sendBungeeMessage(Player player, String message) {
        sendBungeeMessage(player, message, true);
    }

    public static void sendBungeeMessage(Player player, String message, boolean async) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("Message");
            out.writeUTF(player.getName());
            out.writeUTF(message);

            send(player, b.toByteArray(), async);
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send BungeeCord message. " + e.getMessage());
        }
    }

    public static void sendBungeeMessage(String player, String message) {
        sendBungeeMessage(player, message, true);
    }

    public static void sendBungeeMessage(String player, String message, boolean async) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("Message");
            out.writeUTF(player);
            out.writeUTF(message);

            send(b.toByteArray(), async);
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send BungeeCord message. " + e.getMessage());
        }
    }

    public static void sendBungeePlayer(Player player, ServerType type) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("ConnectOther");
            out.writeUTF(player.getName());
            out.writeUTF(type.getTag());

            send(player, b.toByteArray(), false);
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send BungeeCord player. " + e.getMessage());
        }
    }

    public static void sendBungeePlayer(String player, ServerType type) {
        sendBungeePlayer(player, type, false);
    }

    public static void sendBungeePlayer(String player, ServerType type, boolean async) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("ConnectOther");
            out.writeUTF(player);
            out.writeUTF(type.getTag());

            send(b.toByteArray(), async);
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send BungeeCord player. " + e.getMessage());
        }
    }

    @Deprecated
    public static void sendForward(String channel, String message) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF("ALL");
            out.writeUTF(channel); // The channel name to check if this your data

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            msgout.writeUTF(message); // You can do anything you want with msgout
            msgout.writeShort(123);

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            if (!MoonerBungee.plugin.getServer().getOnlinePlayers().isEmpty()) {
                MoonerBungee.plugin.getServer().getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
                    Player p = MoonerBungee.plugin.getServer().getOnlinePlayers().iterator().next();
                    p.sendPluginMessage(MoonerBungee.plugin, "bungee:chat", b.toByteArray());
                });
            }
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send Forward. " + e.getMessage());
        }
    }

    @Deprecated
    public static void sendForward(String player, String channel, String message) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF("Forward"); // So BungeeCord knows to forward it
            out.writeUTF(player);
            out.writeUTF(channel); // The channel name to check if this your data
//
//            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
//            DataOutputStream msgout = new DataOutputStream(msgbytes);
//            msgout.writeUTF(message); // You can do anything you want with msgout
            out.writeUTF(message);

            if (!MoonerBungee.plugin.getServer().getOnlinePlayers().isEmpty()) {
                MoonerBungee.plugin.getServer().getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
                    Player p = MoonerBungee.plugin.getServer().getOnlinePlayers().iterator().next();
                    p.sendPluginMessage(MoonerBungee.plugin, "bungee:chat", b.toByteArray());
                });
            }
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send Forward. " + e.getMessage());
        }
    }

    public static void sendMessage(Player p, String channel, String message) {
        try(
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b)
        ) {
            out.writeUTF(channel);
            out.writeUTF(p.getUniqueId().toString());
            out.writeUTF(message);
            out.writeUTF(ChatColor.stripColor(message));
            p.sendPluginMessage(MoonerBungee.plugin, "bungee:chat", b.toByteArray());
        } catch (Exception e) {
            MoonerBungee.plugin.getLogger().warning("Failed to send Forward. " + e.getMessage());
        }
    }

    public static ServerType getServerType(int port) {
        if(port == 1568) {
            return ServerType.MAIN_SERVER;
        } else if(port == 1511) {
            return ServerType.SURVIVAL_SERVER;
        } else if(port == 1599) {
            return ServerType.SPAWN_SERVER;
        }
        return ServerType.OTHER;
    }

    public static Rank getPlayerRank(Player p) {
        if(p.getName().equals("Mooner510")) return Rank.DEVELOP;
        if(p.isOp()) return Rank.ADMIN;
        for (Rank value : Rank.values()) {
            if (value.getPermission() != null && p.hasPermission(value.getPermission())) {
                return value;
            }
        }
        return Rank.DEFAULT;
    }
}
