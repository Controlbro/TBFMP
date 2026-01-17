package com.tbfmc.tbfmp.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventSettingsStorage {
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Boolean> eventEnabled = new HashMap<>();

    public EventSettingsStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "event-settings.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                eventEnabled.put(uuid, data.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled(UUID uuid) {
        return eventEnabled.getOrDefault(uuid, true);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        eventEnabled.put(uuid, enabled);
        data.set(uuid.toString(), enabled);
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
