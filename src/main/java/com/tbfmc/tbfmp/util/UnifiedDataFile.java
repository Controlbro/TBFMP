package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UnifiedDataFile {
    private static final int DATA_VERSION = 1;
    private final File file;
    private final com.tbfmc.tbfmp.storage.MySqlStorageService mysqlStorageService;
    private FileConfiguration data;
    private boolean enabled;

    public UnifiedDataFile(JavaPlugin plugin, com.tbfmc.tbfmp.storage.MySqlStorageService mysqlStorageService) {
        this.file = new File(plugin.getDataFolder(), "data/data.yml");
        this.mysqlStorageService = mysqlStorageService;
        File legacyFile = new File(plugin.getDataFolder(), "oakglowutil-data.yml");
        if (!file.exists() && legacyFile.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                Files.move(legacyFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
            }
        }
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

    public boolean refreshFromMysqlIfEnabled() {
        if (mysqlStorageService == null || !mysqlStorageService.isEnabled()) {
            return false;
        }
        if (!mysqlStorageService.refreshConnection()) {
            return false;
        }
        FileConfiguration fresh = new YamlConfiguration();
        mysqlStorageService.loadTo(fresh);
        if (!mysqlStorageService.isConnectionValid()) {
            return false;
        }
        this.data = fresh;
        this.enabled = true;
        return true;
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
