package org.mooner.moonerbungeeapi.db;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mooner.moonerbungeeapi.MoonerBungee;
import org.mooner.moonerbungeeapi.api.KeyWords;
import org.mooner.moonerbungeeapi.api.KeywordResponse;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KeyWordDB {
    public static KeyWordDB init;
    private final HashMap<UUID, KeyWords> keyWords;
    public static final int maxKeyword = 8;

    private static final String dbPath = "../db/";
    private static final String CONNECTION = "jdbc:sqlite:" + dbPath + "keyword.db";

    public KeyWordDB() {
        keyWords = new HashMap<>();

        new File(dbPath).mkdirs();
        File db = new File(dbPath, "keyword.db");
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
                        "CREATE TABLE IF NOT EXISTS KeyWorld (" +
                                "uuid TEXT NOT NULL UNIQUE," +
                                "keyWords TEXT," +
                                "PRIMARY KEY(uuid))")
        ) {
            s.execute();
            MoonerBungee.plugin.getLogger().info("성공적으로 KeyWorldDB 를 생성했습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private KeyWords get(String uuid) {
        try (
                Connection c = DriverManager.getConnection(CONNECTION);
                PreparedStatement s = c.prepareStatement("SELECT * FROM KeyWorld WHERE uuid=?")
        ) {
            s.setString(1, uuid);
            try (
                    final ResultSet r = s.executeQuery()
            ) {
                if (r.next()) {
                    return new KeyWords(r.getString("keyWords"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void save(String uuid, String keys) {
        Bukkit.getScheduler().runTaskAsynchronously(MoonerBungee.plugin, () -> {
            try (
                    Connection c = DriverManager.getConnection(CONNECTION);
                    PreparedStatement s2 = c.prepareStatement("UPDATE KeyWorld SET keyWords=? WHERE uuid=?");
                    PreparedStatement s = c.prepareStatement("INSERT INTO KeyWorld (uuid, keyWords) VALUES(?, ?)")
            ) {
                s2.setString(1, keys);
                s2.setString(2, uuid);
                if (s2.executeUpdate() == 0) {
                    s.setString(1, uuid);
                    s.setString(2, keys);
                    s.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public KeyWords getKeyWord(Player p) {
        final KeyWords w = keyWords.get(p.getUniqueId());
        if(w != null) return w;
        final String uuid = p.getUniqueId().toString();
        final KeyWords w2 = get(uuid);
        if(w2 != null) {
            keyWords.put(p.getUniqueId(), w2);
            return w2;
        }
        save(uuid, "");
        final KeyWords w3 = new KeyWords();
        keyWords.put(p.getUniqueId(), w3);
        return w3;
    }

    public boolean check(Player p, String s) {
        return getKeyWord(p).check(s);
    }

    public KeywordResponse addKeyWord(Player p, String keyWorld) {
        final KeyWords words = getKeyWord(p);
        KeywordResponse r;
        if((r = words.addKeyWord(keyWorld)) != KeywordResponse.COMPLETE) return r;
        save(p.getUniqueId().toString(), words.keys());
        return r;
    }

    public boolean removeKeyWord(Player p, String keyWorld) {
        final KeyWords words = getKeyWord(p);
        if(!words.contains(keyWorld)) return false;
        words.removeKeyWord(keyWorld);
        save(p.getUniqueId().toString(), words.keys());
        return true;
    }

    public List<String> getKeyWords(Player p) {
        return getKeyWord(p).getKeyWords();
    }
}
