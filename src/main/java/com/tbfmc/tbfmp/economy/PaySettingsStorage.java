package com.tbfmc.tbfmp.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaySettingsStorage {
    private final JavaPlugin plugin;
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Boolean> payEnabled = new HashMap<>();

    public PaySettingsStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.plugin = plugin;
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "pay-settings.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("pay-settings");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    payEnabled.put(uuid, section.getBoolean(key, true));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                payEnabled.put(uuid, legacyData.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isPayEnabled(UUID uuid) {
        refreshFromMysqlIfEnabled();
        return isPayEnabledInternal(uuid);
    }

    public boolean togglePay(UUID uuid) {
        refreshFromMysqlIfEnabled();
        boolean enabled = !isPayEnabledInternal(uuid);
        payEnabled.put(uuid, enabled);
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
        for (Map.Entry<UUID, Boolean> entry : payEnabled.entrySet()) {
            data.set("pay-settings." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, boolean value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("pay-settings." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    public void refreshFromMysqlIfEnabled() {
        if (!unifiedDataFile.refreshFromMysqlIfEnabled()) {
            return;
        }
        payEnabled.clear();
        load();
    }

    private boolean isPayEnabledInternal(UUID uuid) {
        return payEnabled.getOrDefault(uuid, true);
    }
}
