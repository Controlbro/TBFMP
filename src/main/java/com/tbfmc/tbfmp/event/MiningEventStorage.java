package com.tbfmc.tbfmp.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningEventStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Integer> counts = new HashMap<>();

    public MiningEventStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "mining-event.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("mining-event");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    counts.put(uuid, section.getInt(key, 0));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                counts.put(uuid, legacyData.getInt(key, 0));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public int getCount(UUID uuid) {
        return counts.getOrDefault(uuid, 0);
    }

    public void addCount(UUID uuid, int amount) {
        int updated = getCount(uuid) + amount;
        counts.put(uuid, updated);
        setValue(uuid.toString(), updated);
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

    public void reset() {
        counts.clear();
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("mining-event", null);
        } else {
            for (String key : legacyData.getKeys(false)) {
                legacyData.set(key, null);
            }
        }
        save();
    }

    public Map<UUID, Integer> getAllCounts() {
        return Map.copyOf(counts);
    }

    public void reloadFromUnifiedData() {
        counts.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Integer> entry : counts.entrySet()) {
            data.set("mining-event." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, int value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("mining-event." + key, value);
            return;
        }
        legacyData.set(key, value);
    }
}
