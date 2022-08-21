package org.mooner.moonerbungeeapi.db.entity;

import java.util.UUID;

public class WhisperData {
    private final UUID sender;
    private final UUID receiver;
    private final String chat;
    private final long time;

    public WhisperData(UUID sender, UUID receiver, String chat, long time) {
        this.sender = sender;
        this.receiver = receiver;
        this.chat = chat;
        this.time = time;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public String getChat() {
        return chat;
    }

    public long getTime() {
        return time;
    }
}
