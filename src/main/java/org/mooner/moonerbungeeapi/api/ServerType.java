package org.mooner.moonerbungeeapi.api;

public enum ServerType {
    MAIN_SERVER("메인서버"),
    SURVIVAL_SERVER("야생서버"),
    OTHER("알 수 없음");

    private final String tag;

    ServerType(String s) {
        this.tag = s;
    }

    public String getTag() {
        return tag;
    }
}
