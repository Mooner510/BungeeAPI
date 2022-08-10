package org.mooner.moonerbungeeapi.db;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;
import org.mooner.moonerbungeeapi.api.BungeeAPI;

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
                                "main INTEGER," +
                                "sv INTEGER," +
                                "spawn INTEGER" +
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
                PreparedStatement s = c.prepareStatement("SELECT (main, sv, spawn) FROM PlayTime WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    return r.getLong(1) + r.getLong(2) + r.getLong(3) + delayed;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayed;
    }

    public void join(Player p) {
        lastJoin.put(p.getUniqueId(), System.currentTimeMillis());
    }

    public void quit(Player p) {
        switch (BungeeAPI.getServerType(MoonerBungee.port)) {
            case MAIN_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET main=? WHERE uuid=?");
                ) {
                    s2.setLong(1, p.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    s2.setString(2, p.getUniqueId().toString());
                    s2.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case SURVIVAL_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET sv=? WHERE uuid=?");
                ) {
                    s2.setLong(1, p.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    s2.setString(2, p.getUniqueId().toString());
                    s2.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case SPAWN_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET spawn=? WHERE uuid=?");
                ) {
                    s2.setLong(1, p.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    s2.setString(2, p.getUniqueId().toString());
                    s2.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
