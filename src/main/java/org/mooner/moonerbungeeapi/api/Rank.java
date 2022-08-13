package org.mooner.moonerbungeeapi.api;

import org.bukkit.ChatColor;

public enum Rank {
    ADMIN("&a[ &f관리자 &a] &f", "rank.admin"),
    DEVELOP("&d[ &f개발자 &d] &f", "rank.develop"),
    MANAGER("&b[ &f매니저 &b] &f", "rank.manager"),
    CHAT("&3[ &f채팅 &3] &f", "rank.chat"),
    DEFAULT("&e[ &f유저 &e] &f", null);

    private final String prefix;
    private final String permission;

    Rank(String s, String permission) {
        this.prefix = chat(s);
        this.permission = permission;
    }

    public static String chat(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }
}
