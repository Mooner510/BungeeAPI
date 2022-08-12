package org.mooner.moonerbungeeapi.api;

import java.util.HashSet;
import java.util.List;

public class KeyWords {
    private final HashSet<String> keywords;

    public KeyWords() {
        this.keywords = new HashSet<>();
    }

    public KeyWords(String keys) {
        this.keywords = new HashSet<>(List.of(keys.split(":")));
    }

    public boolean addKeyWord(String s) {
        if(keywords.size() >= 8 || s.contains(":") || s.length() > 12) return false;
        this.keywords.add(s);
        return true;
    }

    public void removeKeyWord(String s) {
        this.keywords.remove(s);
    }

    public boolean check(String s) {
        return keywords.stream().anyMatch(s::contains);
    }

    public boolean contains(String s) {
        return keywords.contains(s);
    }

    public List<String> getKeyWords() {
        return keywords.stream().sorted().toList();
    }

    public String keys() {
        return String.join(":", keywords);
    }
}
