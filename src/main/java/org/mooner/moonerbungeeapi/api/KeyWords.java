package org.mooner.moonerbungeeapi.api;

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

    public boolean addKeyWord(String s) {
        if(s.isBlank() || keywords.size() >= 8 || s.contains(":") || s.length() > 12) return false;
        this.keywords.add(s);
        return true;
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
