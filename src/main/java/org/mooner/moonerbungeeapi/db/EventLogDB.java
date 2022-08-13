package org.mooner.moonerbungeeapi.db;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;
import org.mooner.moonerbungeeapi.listener.EventType;

import java.io.File;
import java.io.IOException;
import java.sql.*;

import static org.mooner.moonerbungeeapi.db.ChatDB.getKey;

@Deprecated
public class EventLogDB {
    public static EventLogDB init;
    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "eventLog.db";

    public EventLogDB() {
        new File(dbPath).mkdirs();
        File db = new File(dbPath, "eventLog.db");
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
                        "CREATE TABLE IF NOT EXISTS Event (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "player INTEGER NOT NULL," +
                                "type TEXT NOT NULL," +
                                "server TEXT," +
                                "x REAL NOT NULL," +
                                "y REAL NOT NULL," +
                                "z REAL NOT NULL," +
                                "world TEXT NOT NULL," +
                                "data TEXT," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 EventDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS InteractEvent (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "player INTEGER NOT NULL," +
                                "type TEXT NOT NULL," +
                                "server TEXT NOT NULL," +
                                "x REAL NOT NULL," +
                                "y REAL NOT NULL," +
                                "z REAL NOT NULL," +
                                "world TEXT NOT NULL," +
                                "entity TEXT," +
                                "data TEXT," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 EventDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS InventoryEvent (" +
                                "id INTEGER NOT NULL UNIQUE," +
                                "player INTEGER NOT NULL," +
                                "type TEXT NOT NULL," +
                                "server TEXT NOT NULL," +
                                "x REAL NOT NULL," +
                                "y REAL NOT NULL," +
                                "z REAL NOT NULL," +
                                "world TEXT NOT NULL," +
                                "itemType TEXT NOT NULL," +
                                "item TEXT NOT NULL," +
                                "slot INTEGER NOT NULL," +
                                "data TEXT," +
                                "timestamp INTEGER NOT NULL," +
                                "PRIMARY KEY(id AUTOINCREMENT))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 EventDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void log(Player p, EventType type) {
        log(p, type, p.getLocation(), null);
    }

    public void log(Player p, EventType type, Block b) {
        log(p, type, b.getLocation(), b.getType().toString());
    }

    public void log(Player p, EventType type, Location loc, String data) {
        final long id = getKey(p);
        final long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO Event (player, type, server, x, y, z, world, data, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                s.setLong(1, id);
                s.setString(2, type.toString());
                s.setString(3, MoonerBungee.serverType.toString());
                s.setDouble(4, loc.getX());
                s.setDouble(5, loc.getY());
                s.setDouble(6, loc.getZ());
                s.setString(7, p.getWorld().getName());
                if(data == null) s.setNull(8, Types.NULL);
                else s.setString(8, data);
                s.setLong(9, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void log(Player p, EventType type, Entity to) {
        log(p, type, to, null);
    }

    public void log(Player p, EventType type, Entity to, String data) {
        final long id = getKey(p);
        final long time = System.currentTimeMillis();
        final Location loc = to.getLocation().clone();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO InteractEvent (player, type, server, x, y, z, world, data, entity, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                s.setLong(1, id);
                s.setString(2, type.toString());
                s.setString(3, MoonerBungee.serverType.toString());
                s.setDouble(4, loc.getX());
                s.setDouble(5, loc.getY());
                s.setDouble(6, loc.getZ());
                s.setString(7, p.getWorld().getName());
                s.setString(8, to.getType().toString() + (to.getType() == EntityType.PLAYER ? getKey(to.getUniqueId()) : ""));
                if(data == null) s.setNull(9, Types.NULL);
                else s.setString(9, data);
                s.setLong(10, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void log(Player p, EventType type, Location loc, int slot, Material item, byte[] i) {
        log(p, type, loc, slot, item, i, null);
    }

    public void log(Player p, EventType type, Location loc, int slot, Material item, byte[] i, String data) {
        final long id = getKey(p);
        final long time = System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s = c.prepareStatement("INSERT INTO InventoryEvent (player, type, server, x, y, z, world, itemType, item, slot, data, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                s.setLong(1, id);
                s.setString(2, type.toString());
                s.setString(3, MoonerBungee.serverType.toString());
                s.setDouble(4, loc.getX());
                s.setDouble(5, loc.getY());
                s.setDouble(6, loc.getZ());
                s.setString(7, p.getWorld().getName());
                s.setString(8, item.toString());
                s.setBytes(9, i);
                s.setInt(10, slot);
                if(data == null) s.setNull(11, Types.NULL);
                else s.setString(11, data);
                s.setLong(12, time);
                s.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
