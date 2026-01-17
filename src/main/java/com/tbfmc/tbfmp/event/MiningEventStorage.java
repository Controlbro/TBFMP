package com.tbfmc.tbfmp.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningEventStorage {
    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Integer> counts = new HashMap<>();

    public MiningEventStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mining-event.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                counts.put(uuid, data.getInt(key, 0));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public int getCount(UUID uuid) {
        return counts.getOrDefault(uuid, 0);
    }

    public void addCount(UUID uuid, int amount) {
        int updated = getCount(uuid) + amount;
        counts.put(uuid, updated);
        data.set(uuid.toString(), updated);
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
