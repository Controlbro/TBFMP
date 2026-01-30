package com.tbfmc.tbfmp.sit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitSettingsStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> chairEnabled = new HashMap<>();
    private final Map<UUID, Boolean> playerEnabled = new HashMap<>();

    public SitSettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "sit-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("sit-settings");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    chairEnabled.put(uuid, section.getBoolean(key + ".chair", true));
                    playerEnabled.put(uuid, section.getBoolean(key + ".player", true));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                chairEnabled.put(uuid, legacyData.getBoolean(key + ".chair", true));
                playerEnabled.put(uuid, legacyData.getBoolean(key + ".player", true));
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
        setValue(uuid + ".chair", enabled);
        save();
    }

    public void setPlayerEnabled(UUID uuid, boolean enabled) {
        playerEnabled.put(uuid, enabled);
        setValue(uuid + ".player", enabled);
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

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (UUID uuid : chairEnabled.keySet()) {
            data.set("sit-settings." + uuid + ".chair", chairEnabled.get(uuid));
            data.set("sit-settings." + uuid + ".player", playerEnabled.getOrDefault(uuid, true));
        }
        for (UUID uuid : playerEnabled.keySet()) {
            if (chairEnabled.containsKey(uuid)) {
                continue;
            }
            data.set("sit-settings." + uuid + ".chair", chairEnabled.getOrDefault(uuid, true));
            data.set("sit-settings." + uuid + ".player", playerEnabled.get(uuid));
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("sit-settings." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    public void reloadFromUnifiedData() {
        chairEnabled.clear();
        playerEnabled.clear();
        load();
    }
}
