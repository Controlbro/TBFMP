package com.tbfmc.tbfmp.quests;

import java.util.List;

public class QuestAssignments {
    private final long startTime;
    private final List<String> questKeys;

    public QuestAssignments(long startTime, List<String> questKeys) {
        this.startTime = startTime;
        this.questKeys = questKeys;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<String> getQuestKeys() {
        return questKeys;
    }
}
