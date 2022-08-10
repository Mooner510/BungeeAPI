package org.mooner.moonerbungeeapi.db;

import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class PlayTimeDB {
    public static PlayTimeDB init;
    private final HashMap<UUID, Long> lastJoin;

    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "playtime.db";

    public PlayTimeDB() {
        lastJoin = new HashMap<>();

        new File(dbPath).mkdirs();
        File db = new File(dbPath, "playtime.db");
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
                        "CREATE TABLE IF NOT EXISTS PlayTime (" +
                                "uuid TEXT NOT NULL UNIQUE," +
                                "time INTEGER NOT NULL," +
                                "lastJoin INTEGER," +
                                "lastLeave INTEGER," +
                                "PRIMARY KEY(uuid))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 PlayerTimeDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long playTime(Player p) {
        final long time = System.currentTimeMillis();
        final long delayed = time - lastJoin.get(p.getUniqueId());
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT time FROM PlayTime WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    return r.getLong(1) + delayed;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayed;
    }

    public void join(Player p) {
        final long time = System.currentTimeMillis();
        lastJoin.put(p.getUniqueId(), time);
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET lastJoin=? WHERE uuid=?");
                PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime VALUES(?, ?, ?, ?)")
        ) {
            s2.setLong(1, time);
            s2.setString(2, p.getUniqueId().toString());
            if(s2.executeUpdate() == 0) {
                s.setString(1, p.getUniqueId().toString());
                s.setLong(2, 0);
                s.setLong(3, time);
                s.setNull(4, Types.INTEGER);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void quit(Player p) {
        final long time = System.currentTimeMillis();
        final long delayed = time - lastJoin.get(p.getUniqueId());
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET time=time+?, lastLeave=? WHERE uuid=?");
        ) {
            s2.setLong(1, delayed);
            s2.setLong(2, time);
            s2.setString(3, p.getUniqueId().toString());
            s2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
