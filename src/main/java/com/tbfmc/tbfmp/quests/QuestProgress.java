package com.tbfmc.tbfmp.quests;

public class QuestProgress {
    private final int progress;
    private final boolean completed;
    private final boolean claimed;
    private final long startTime;

    public QuestProgress(int progress, boolean completed, boolean claimed, long startTime) {
        this.progress = progress;
        this.completed = completed;
        this.claimed = claimed;
        this.startTime = startTime;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public long getStartTime() {
        return startTime;
    }
}
