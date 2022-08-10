package org.mooner.moonerbungeeapi.api;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Rank {
    DEFAULT("&e[ &f유저 &e] &f", null),
    CHAT("&b[ &f채팅 &b] &f", "rank.chat"),
    DEVELOP("&d[ &f개발자 &d] &f", "rank.develop"),
    ADMIN("&a[ &f관리자 &a] &f", "rank.admin");

    private final String prefix;
    private final String permission;

    Rank(String s, String permission) {
        this.prefix = chat(s);
        this.permission = permission;
    }

    private static String chat(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }
}
