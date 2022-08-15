package org.mooner.moonerbungeeapi.db;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

import static org.mooner.moonerbungeeapi.db.ChatDB.getKey;

public class PlayerDB {
    public static PlayerDB init;
    private final HashMap<Long, Long> lastJoin;

    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "playerData.db";

    public PlayerDB() {
        lastJoin = new HashMap<>();

        new File(dbPath).mkdirs();
        File db = new File(dbPath, "playerData.db");
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
                                "player INTEGER NOT NULL UNIQUE," +
                                "main INTEGER," +
                                "sv INTEGER," +
                                "spawn INTEGER," +
                                "PRIMARY KEY(player))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 PlayerTimeDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS Tutorial (" +
                                "player INTEGER NOT NULL UNIQUE," +
                                "done INTEGER," +
                                "PRIMARY KEY(player))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 TutorialDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long playTime(Player p) {
        final long time = System.currentTimeMillis();
        final long key = getKey(p.getUniqueId());
        final long delayed = time - lastJoin.get(key);
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM PlayTime WHERE player=?")
        ) {
            s.setLong(1, key);
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    return r.getInt("main") + r.getInt("sv") + r.getInt("spawn") + delayed;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayed;
    }

    public void recordPlayTime(Player p) {
        lastJoin.put(getKey(p.getUniqueId()), System.currentTimeMillis());
    }

    private void save(long key, int playTime) {
        switch (MoonerBungee.serverType) {
            case MAIN_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET main=? WHERE player=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (player, main) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setLong(2, key);
                    if (s2.executeUpdate() == 0) {
                        s2.setLong(1, key);
                        s.setInt(2, playTime);
                        s.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case SURVIVAL_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET sv=? WHERE player=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (player, sv) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setLong(2, key);
                    if (s2.executeUpdate() == 0) {
                        s2.setLong(1, key);
                        s.setInt(2, playTime);
                        s.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case SPAWN_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET spawn=? WHERE player=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (player, spawn) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setLong(2, key);
                    if (s2.executeUpdate() == 0) {
                        s2.setLong(1, key);
                        s.setInt(2, playTime);
                        s.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void savePlayTime(Player p) {
        save(getKey(p), p.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    public void savePlayTimeAsync(Player p) {
        final long key = getKey(p);
        final int playTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
        Bukkit.getScheduler().runTaskLaterAsynchronously(MoonerBungee.plugin, () -> save(key, playTime), 5);
    }

    public boolean isTutorial(Player p) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT done FROM Tutorial WHERE player=?")
        ) {
            s.setLong(1, getKey(p));
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    return r.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setTutorial(Player p, boolean v) {
        final long key = getKey(p);
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s2 = c.prepareStatement("UPDATE Tutorial SET done=? WHERE player=?");
                PreparedStatement s = c.prepareStatement("INSERT INTO Tutorial VALUES(?, ?)")
        ) {
            s2.setBoolean(1, v);
            s2.setLong(2, key);
            if (s2.executeUpdate() == 0) {
                s.setLong(1, key);
                s.setBoolean(2, v);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
