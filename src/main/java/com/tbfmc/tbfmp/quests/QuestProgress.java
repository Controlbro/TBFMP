package com.tbfmc.tbfmp.quests;

public class QuestProgress {
    private final int progress;
    private final boolean completed;
    private final long startTime;

    public QuestProgress(int progress, boolean completed, long startTime) {
        this.progress = progress;
        this.completed = completed;
        this.startTime = startTime;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getStartTime() {
        return startTime;
    }
}
