package org.mooner.moonerbungeeapi.db;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;
import org.mooner.moonerbungeeapi.api.BungeeAPI;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class PlayerDB {
    public static PlayerDB init;
    private final HashMap<UUID, Long> lastJoin;
    private final HashMap<UUID, Boolean> tutorial;

    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "playerData.db";

    public PlayerDB() {
        lastJoin = new HashMap<>();
        tutorial = new HashMap<>();

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
                                "uuid TEXT NOT NULL UNIQUE," +
                                "main INTEGER," +
                                "sv INTEGER," +
                                "spawn INTEGER," +
                                "PRIMARY KEY(uuid))")
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
                                "uuid TEXT NOT NULL UNIQUE," +
                                "done INTEGER," +
                                "PRIMARY KEY(uuid))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 TutorialDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long playTime(Player p) {
        final long time = System.currentTimeMillis();
        final long delayed = time - lastJoin.get(p.getUniqueId());
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM PlayTime WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    MoonerBungee.plugin.getLogger().info(r.getInt("main") + ", " + r.getInt("sv") + ", " + r.getInt("spawn"));
                    return r.getInt("main") + r.getInt("sv") + r.getInt("spawn") + delayed;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return delayed;
    }

    public void recordPlayTime(Player p) {
        lastJoin.put(p.getUniqueId(), System.currentTimeMillis());
    }

    private void save(Player p, int playTime, String uuid) {
        switch (BungeeAPI.getServerType(MoonerBungee.port)) {
            case MAIN_SERVER -> {
                try (
                        Connection c = DriverManager.getConnection(CONNECTION);
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET main=? WHERE uuid=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (uuid, main) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setString(2, uuid);
                    if (s2.executeUpdate() == 0) {
                        s.setString(1, uuid);
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
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET sv=? WHERE uuid=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (uuid, sv) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setString(2, uuid);
                    if (s2.executeUpdate() == 0) {
                        s.setString(1, uuid);
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
                        PreparedStatement s2 = c.prepareStatement("UPDATE PlayTime SET spawn=? WHERE uuid=?");
                        PreparedStatement s = c.prepareStatement("INSERT INTO PlayTime (uuid, spawn) VALUES(?, ?)")
                ) {
                    s2.setInt(1, playTime);
                    s2.setString(2, uuid);
                    if (s2.executeUpdate() == 0) {
                        s.setString(1, uuid);
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
        save(p, p.getStatistic(Statistic.PLAY_ONE_MINUTE), p.getUniqueId().toString());
    }

    public void savePlayTimeAsync(Player p) {
        final String uuid = p.getUniqueId().toString();
        final int playTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
        Bukkit.getScheduler().runTaskLaterAsynchronously(MoonerBungee.plugin, () -> save(p, playTime, uuid), 5);
    }

    public boolean isTutorial(Player p) {
        final Boolean b = tutorial.get(p.getUniqueId());
        if(b != null) return b;
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT done FROM Tutorial WHERE uuid=?")
        ) {
            s.setString(1, p.getUniqueId().toString());
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    final boolean value = r.getBoolean(1);
                    tutorial.put(p.getUniqueId(), value);
                    return value;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tutorial.put(p.getUniqueId(), false);
        return false;
    }

    public void setTutorial(Player p, boolean v) {
        tutorial.put(p.getUniqueId(), v);
        String uuid = p.getUniqueId().toString();
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s2 = c.prepareStatement("UPDATE Tutorial SET done=? WHERE uuid=?");
                PreparedStatement s = c.prepareStatement("INSERT INTO Tutorial VALUES(?, ?)")
        ) {
            s2.setBoolean(1, v);
            s2.setString(2, uuid);
            if (s2.executeUpdate() == 0) {
                s.setString(1, uuid);
                s.setBoolean(2, v);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}