package org.mooner.moonerbungeeapi.db;

import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class ChatDB {
    public static ChatDB init;
    private final HashMap<UUID, Long> key;
    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "chat.db";

    public ChatDB() {
        key = new HashMap<>();

        new File(dbPath).mkdirs();
        File db = new File(dbPath, "chat.db");
        if(!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Player (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "uuid TEXT NOT NULL UNIQUE," +
                                "name TEXT," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 PlayerDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Chat (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "playerId INTEGER NOT NULL," +
                                "server TEXT NOT NULL," +
                                "message TEXT NOT NULL," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 ChatDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Command (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "playerId INTEGER NOT NULL," +
                                "server TEXT NOT NULL," +
                                "prefix TEXT NOT NULL," +
                                "cmd TEXT NOT NULL," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 ChatDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Whisper (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "sender INTEGER NOT NULL," +
                                "receiver INTEGER NOT NULL," +
                                "server TEXT NOT NULL," +
                                "message TEXT NOT NULL," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 WhisperDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static long getKey(Player p) {
        return ChatDB.init.getPlayerKey(p.getUniqueId());
    }

    public static long getKey(UUID uuid) {
        return ChatDB.init.getPlayerKey(uuid);
    }

    private long getPlayerKey(UUID uuid) {
        Long i = key.get(uuid);
        if(i != null) return i;
        return getPlayerKeyFromDB(uuid);
    }

    private long getPlayerKeyFromDB(UUID uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "SELECT id FROM Player WHERE uuid=?")
        ) {
            s.setString(1, uuid.toString());
            try (
                    ResultSet r = s.executeQuery()
            ) {
                if(r.next()) {
                    long i = r.getLong(1);
                    key.put(uuid, i);
                    return i;
                } else {
                    try (
                            Connection c2 = DriverManager.getConnection(CONNECTION);
                            PreparedStatement s2 = c2.prepareStatement("INSERT INTO Player (uuid, name) VALUES(?)")
                    ) {
                        s2.setString(1, uuid.toString());
                        s2.setString(2, uuid.toString());
                        s2.executeUpdate();
                        return getPlayerKeyFromDB(uuid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(new NullPointerException("Can't create key from DB."));
    }

    public void chat(Player p, String message) {
        final long id = getPlayerKey(p.getUniqueId());
        final long time = System.currentTimeMillis();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO Chat (playerId, server, message, timestamp) VALUES(?, ?, ?, ?)")
        ) {
            s.setLong(1, id);
            s.setString(2, MoonerBungee.serverType.getTag());
            s.setString(3, message);
            s.setLong(4, time);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void command(Player p, String cmd) {
        final long time = System.currentTimeMillis();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO Command (playerId, server, prefix, cmd, timestamp) VALUES(?, ?, ?, ?, ?)")
        ) {
            s.setLong(1, getPlayerKey(p.getUniqueId()));
            s.setString(2, MoonerBungee.serverType.getTag());
            s.setString(3, cmd.split(" ")[0]);
            s.setString(4, cmd);
            s.setLong(5, time);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void whisper(Player from, Player to, String msg) {
        final long time = System.currentTimeMillis();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("INSERT INTO Whisper (sender, receiver, server, message, timestamp) VALUES(?, ?, ?, ?, ?)")
        ) {
            s.setLong(1, getPlayerKey(from.getUniqueId()));
            s.setLong(2, getPlayerKey(to.getUniqueId()));
            s.setString(3, MoonerBungee.serverType.getTag());
            s.setString(4, msg);
            s.setLong(5, time);
            s.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
