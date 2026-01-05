package com.tbfmc.tbfmp.sit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitSettingsStorage {
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Boolean> chairEnabled = new HashMap<>();
    private final Map<UUID, Boolean> playerEnabled = new HashMap<>();

    public SitSettingsStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "sit-settings.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                chairEnabled.put(uuid, data.getBoolean(key + ".chair", true));
                playerEnabled.put(uuid, data.getBoolean(key + ".player", true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isChairEnabled(UUID uuid) {
        return chairEnabled.getOrDefault(uuid, true);
    }

    public boolean isPlayerEnabled(UUID uuid) {
        return playerEnabled.getOrDefault(uuid, true);
    }

    public void setChairEnabled(UUID uuid, boolean enabled) {
        chairEnabled.put(uuid, enabled);
        data.set(uuid + ".chair", enabled);
        save();
    }

    public void setPlayerEnabled(UUID uuid, boolean enabled) {
        playerEnabled.put(uuid, enabled);
        data.set(uuid + ".player", enabled);
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
