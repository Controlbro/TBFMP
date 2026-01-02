package com.tbfmc.tbfmp.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaySettingsStorage {
    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Boolean> payEnabled = new HashMap<>();

    public PaySettingsStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "pay-settings.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                payEnabled.put(uuid, data.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isPayEnabled(UUID uuid) {
        return payEnabled.getOrDefault(uuid, true);
    }

    public boolean togglePay(UUID uuid) {
        boolean enabled = !isPayEnabled(uuid);
        payEnabled.put(uuid, enabled);
        data.set(uuid.toString(), enabled);
        save();
        return enabled;
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
