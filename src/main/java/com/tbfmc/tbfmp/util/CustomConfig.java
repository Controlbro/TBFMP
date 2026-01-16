package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CustomConfig {
    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public CustomConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "CustomConfig.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            plugin.saveResource("CustomConfig.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
