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

public class EventSettingsStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> eventEnabled = new HashMap<>();

    public EventSettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "event-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("event-settings");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    eventEnabled.put(uuid, section.getBoolean(key, true));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                eventEnabled.put(uuid, legacyData.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled(UUID uuid) {
        return eventEnabled.getOrDefault(uuid, true);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        eventEnabled.put(uuid, enabled);
        setValue(uuid.toString(), enabled);
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

    public boolean toggle(UUID uuid) {
        boolean enabled = !isEnabled(uuid);
        setEnabled(uuid, enabled);
        return enabled;
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Boolean> entry : eventEnabled.entrySet()) {
            data.set("event-settings." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("event-settings." + key, value);
            return;
        }
        legacyData.set(key, value);
    }
}
