package com.tbfmc.tbfmp.settings;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpSettingsStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> pvpEnabled = new HashMap<>();

    public PvpSettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "pvp-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("pvp-settings");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    pvpEnabled.put(uuid, section.getBoolean(key, true));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                pvpEnabled.put(uuid, legacyData.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled(UUID uuid) {
        return pvpEnabled.getOrDefault(uuid, true);
    }

    public boolean toggle(UUID uuid) {
        refreshFromMysqlIfEnabled();
        boolean enabled = !isEnabled(uuid);
        pvpEnabled.put(uuid, enabled);
        setValue(uuid.toString(), enabled);
        save();
        return enabled;
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        refreshFromMysqlIfEnabled();
        pvpEnabled.put(uuid, enabled);
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

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Boolean> entry : pvpEnabled.entrySet()) {
            data.set("pvp-settings." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("pvp-settings." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    public void refreshFromMysqlIfEnabled() {
        if (!unifiedDataFile.refreshFromMysqlIfEnabled()) {
            return;
        }
        pvpEnabled.clear();
        load();
    }
}
