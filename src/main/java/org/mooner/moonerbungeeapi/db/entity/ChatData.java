package org.mooner.moonerbungeeapi.db.entity;

import java.util.UUID;

public class ChatData {
    private final UUID sender;
    private final String chat;
    private final long time;

    public ChatData(UUID sender, String chat, long time) {
        this.sender = sender;
        this.chat = chat;
        this.time = time;
    }

    public UUID getSender() {
        return sender;
    }

    public String getChat() {
        return chat;
    }

    public long getTime() {
        return time;
    }
}
