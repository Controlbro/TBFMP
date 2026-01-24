package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class UnifiedDataFile {
    private static final int DATA_VERSION = 1;
    private final File file;
    private FileConfiguration data;
    private boolean enabled;

    public UnifiedDataFile(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "oakglowutil-data.yml");
        reload();
    }

    public void reload() {
        if (file.exists()) {
            this.data = YamlConfiguration.loadConfiguration(file);
            this.enabled = data.getInt("data-version", 0) >= DATA_VERSION;
        } else {
            this.data = new YamlConfiguration();
            this.enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        this.enabled = true;
        data.set("data-version", DATA_VERSION);
    }

    public FileConfiguration getData() {
        return data;
    }

    public void save() {
        if (!enabled) {
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            data.save(file);
        } catch (IOException ignored) {
        }
    }
}
