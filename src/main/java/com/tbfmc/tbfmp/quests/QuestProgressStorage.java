package com.tbfmc.tbfmp.quests;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class QuestProgressStorage {
    private final File file;
    private final FileConfiguration data;

    public QuestProgressStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "quest-progress.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public QuestProgress getProgress(UUID uuid, String category, QuestDefinition quest) {
        String basePath = basePath(uuid, category, quest.getId());
        long now = System.currentTimeMillis();
        long start = data.getLong(basePath + ".start", now);
        int progress = data.getInt(basePath + ".progress", 0);
        boolean completed = data.getBoolean(basePath + ".completed", false);
        boolean claimed = data.getBoolean(basePath + ".claimed", false);
        boolean changed = false;
        if (now - start >= quest.getType().getDurationMillis()) {
            start = now;
            progress = 0;
            completed = false;
            claimed = false;
            changed = true;
        }
        if (!data.contains(basePath + ".start")) {
            data.set(basePath + ".start", start);
            changed = true;
        }
        if (changed) {
            data.set(basePath + ".progress", progress);
            data.set(basePath + ".completed", completed);
            data.set(basePath + ".claimed", claimed);
            save();
        }
        return new QuestProgress(progress, completed, claimed, start);
    }

    public void setProgress(UUID uuid, String category, String questId, int progress, boolean completed, boolean claimed, long start) {
        String basePath = basePath(uuid, category, questId);
        data.set(basePath + ".start", start);
        data.set(basePath + ".progress", progress);
        data.set(basePath + ".completed", completed);
        data.set(basePath + ".claimed", claimed);
        save();
    }

    public QuestAssignments getAssignments(UUID uuid, QuestType type) {
        String basePath = uuid + ".assignments." + type.name();
        long now = System.currentTimeMillis();
        long start = data.getLong(basePath + ".start", now);
        if (!data.contains(basePath + ".start")) {
            data.set(basePath + ".start", start);
            save();
        }
        return new QuestAssignments(start, data.getStringList(basePath + ".quests"));
    }

    public void setAssignments(UUID uuid, QuestType type, List<String> questKeys, long start) {
        String basePath = uuid + ".assignments." + type.name();
        data.set(basePath + ".start", start);
        data.set(basePath + ".quests", questKeys);
        save();
    }

    public void setClaimed(UUID uuid, String category, String questId, boolean claimed) {
        String basePath = basePath(uuid, category, questId);
        data.set(basePath + ".claimed", claimed);
        save();
    }

    public void save() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            data.save(file);
        } catch (IOException ignored) {
        }
    }

    private String basePath(UUID uuid, String category, String questId) {
        return uuid + "." + category + "." + questId;
    }
}
