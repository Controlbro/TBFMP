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

public class KeepInventorySettingsStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> keepInventory = new HashMap<>();

    public KeepInventorySettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "keep-inventory-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("keep-inventory");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    keepInventory.put(uuid, section.getBoolean(key, false));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                keepInventory.put(uuid, legacyData.getBoolean(key, false));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled(UUID uuid) {
        return keepInventory.getOrDefault(uuid, false);
    }

    public boolean toggle(UUID uuid) {
        boolean enabled = !isEnabled(uuid);
        keepInventory.put(uuid, enabled);
        setValue(uuid.toString(), enabled);
        save();
        return enabled;
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        keepInventory.put(uuid, enabled);
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
        for (Map.Entry<UUID, Boolean> entry : keepInventory.entrySet()) {
            data.set("keep-inventory." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("keep-inventory." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    public void reloadFromUnifiedData() {
        keepInventory.clear();
        load();
    }
}
