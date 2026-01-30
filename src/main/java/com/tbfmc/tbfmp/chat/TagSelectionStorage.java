package com.tbfmc.tbfmp.chat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagSelectionStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, String> selections = new HashMap<>();

    public TagSelectionStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "tag-selections.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("tag-selections");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    selections.put(uuid, section.getString(key, ""));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                selections.put(uuid, legacyData.getString(key, ""));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public String getSelection(UUID uuid) {
        return selections.getOrDefault(uuid, "");
    }

    public void setSelection(UUID uuid, String tagId) {
        selections.put(uuid, tagId);
        setValue(uuid.toString(), tagId);
        save();
    }

    public void save() {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.save();
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            legacyData.save(file);
        } catch (IOException ignored) {
        }
    }

    public void reloadFromUnifiedData() {
        selections.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, String> entry : selections.entrySet()) {
            data.set("tag-selections." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, String value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("tag-selections." + key, value);
            return;
        }
        legacyData.set(key, value);
    }
}
