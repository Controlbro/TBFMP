package com.tbfmc.tbfmp.settings;

public enum SettingsOption {
    PAY("pay"),
    SIT_CHAIR("sit-chair"),
    SIT_PLAYER("sit-player"),
    AUTO_MESSAGES("auto-messages");

    private final String key;

    SettingsOption(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
