package com.tbfmc.tbfmp.settings;

public enum SettingsOption {
    PAY("pay"),
    SIT_CHAIR("sit-chair"),
    SIT_PLAYER("sit-player"),
    AUTO_MESSAGES("auto-messages"),
    KEEP_INVENTORY("keep-inventory"),
    PVP("pvp"),
    EVENT_LEADERBOARD("toggleleaderboard"),
    TPA("tpa");

    private final String key;

    SettingsOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
