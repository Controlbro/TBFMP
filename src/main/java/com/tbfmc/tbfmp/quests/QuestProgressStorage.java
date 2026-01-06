package com.tbfmc.tbfmp.quests;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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
        boolean changed = false;
        if (now - start >= quest.getType().getDurationMillis()) {
            start = now;
            progress = 0;
            completed = false;
            changed = true;
        }
        if (!data.contains(basePath + ".start")) {
            data.set(basePath + ".start", start);
            changed = true;
        }
        if (changed) {
            data.set(basePath + ".progress", progress);
            data.set(basePath + ".completed", completed);
            save();
        }
        return new QuestProgress(progress, completed, start);
    }

    public void setProgress(UUID uuid, String category, String questId, int progress, boolean completed, long start) {
        String basePath = basePath(uuid, category, questId);
        data.set(basePath + ".start", start);
        data.set(basePath + ".progress", progress);
        data.set(basePath + ".completed", completed);
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
