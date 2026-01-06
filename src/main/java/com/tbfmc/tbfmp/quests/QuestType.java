package com.tbfmc.tbfmp.quests;

import java.time.Duration;

public enum QuestType {
    DAILY(Duration.ofDays(1), "Daily"),
    WEEKLY(Duration.ofDays(7), "Weekly"),
    MONTHLY(Duration.ofDays(30), "Monthly");

    private final Duration duration;
    private final String displayName;

    QuestType(Duration duration, String displayName) {
        this.duration = duration;
        this.displayName = displayName;
    }

    public long getDurationMillis() {
        return duration.toMillis();
    }

    public String getDisplayName() {
        return displayName;
    }

    public static QuestType fromString(String value) {
        for (QuestType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return DAILY;
    }
}
