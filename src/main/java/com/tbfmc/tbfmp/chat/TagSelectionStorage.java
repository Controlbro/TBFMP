package com.tbfmc.tbfmp.chat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagSelectionStorage {
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, String> selections = new HashMap<>();

    public TagSelectionStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "tag-selections.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                selections.put(uuid, data.getString(key, ""));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public String getSelection(UUID uuid) {
        return selections.getOrDefault(uuid, "");
    }

    public void setSelection(UUID uuid, String tagId) {
        selections.put(uuid, tagId);
        data.set(uuid.toString(), tagId);
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
}
