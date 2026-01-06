package com.tbfmc.tbfmp.chat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatNotificationSettingsStorage {
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Boolean> enabled = new HashMap<>();

    public ChatNotificationSettingsStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "chat-notification-settings.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                enabled.put(uuid, data.getBoolean(key, true));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled(UUID uuid) {
        return enabled.getOrDefault(uuid, true);
    }

    public boolean toggle(UUID uuid) {
        boolean value = !isEnabled(uuid);
        enabled.put(uuid, value);
        data.set(uuid.toString(), value);
        save();
        return value;
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
