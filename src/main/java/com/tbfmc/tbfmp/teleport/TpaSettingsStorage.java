package com.tbfmc.tbfmp.teleport;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaSettingsStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> tpaEnabled = new HashMap<>();

    public TpaSettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "tpa-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public boolean isEnabled(UUID uuid) {
        return tpaEnabled.getOrDefault(uuid, true);
    }

    public boolean toggle(UUID uuid) {
        boolean enabled = !isEnabled(uuid);
        tpaEnabled.put(uuid, enabled);
        setValue(uuid.toString(), enabled);
        save();
        return enabled;
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
        for (Map.Entry<UUID, Boolean> entry : tpaEnabled.entrySet()) {
            data.set("tpa-settings." + entry.getKey(), entry.getValue());
        }
    }

    public void reloadFromUnifiedData() {
        tpaEnabled.clear();
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("tpa-settings");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    tpaEnabled.put(uuid, section.getBoolean(key, true));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                tpaEnabled.put(uuid, legacyData.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("tpa-settings." + key, value);
            return;
        }
        legacyData.set(key, value);
    }
}
