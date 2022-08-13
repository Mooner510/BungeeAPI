package org.mooner.moonerbungeeapi.api;

import org.mooner.moonerbungeeapi.db.KeyWordDB;

import java.util.HashSet;
import java.util.List;

public class KeyWords {
    private final HashSet<String> keywords;

    public KeyWords() {
        this.keywords = new HashSet<>();
    }

    public KeyWords(String keys) {
        this.keywords = keys.isEmpty() ? new HashSet<>() : new HashSet<>(List.of(keys.split(":")));
    }

    public KeywordResponse addKeyWord(String s) {
        if(s.isBlank()) return KeywordResponse.BLANK;
        if(keywords.size() >= KeyWordDB.maxKeyword) return KeywordResponse.MAX_KEYWORD;
        if(s.length() > 12) return KeywordResponse.TOO_MANY;
        if(s.contains(":")) return KeywordResponse.BAD_WORD;
        this.keywords.add(s);
        return KeywordResponse.COMPLETE;
    }

    public void removeKeyWord(String s) {
        this.keywords.remove(s);
    }

    public boolean check(String s) {
        if(keywords.isEmpty()) return false;
        return keywords.stream().anyMatch(s::contains);
    }

    public boolean contains(String s) {
        if(keywords.isEmpty()) return false;
        return keywords.contains(s);
    }

    public List<String> getKeyWords() {
        return keywords.stream().sorted().toList();
    }

    public String keys() {
        return String.join(":", keywords);
    }
}
