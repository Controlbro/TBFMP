package com.tbfmc.tbfmp.quests;

public enum QuestObjectiveType {
    BLOCK_BREAK,
    MOB_KILL,
    BREED;

    public static QuestObjectiveType fromString(String value) {
        for (QuestObjectiveType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return BLOCK_BREAK;
    }
}
