package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public final class ConfigUpdater {
    private ConfigUpdater() {
    }

    public static void updateConfig(JavaPlugin plugin, String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
            return;
        }
        FileConfiguration current = YamlConfiguration.loadConfiguration(file);
        try (InputStream inputStream = plugin.getResource(resourcePath)) {
            if (inputStream == null) {
                return;
            }
            FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            boolean changed = mergeDefaults(current, defaults, "");
            if (changed) {
                try {
                    current.save(file);
                } catch (IOException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static boolean mergeDefaults(FileConfiguration current, FileConfiguration defaults, String path) {
        boolean changed = false;
        Set<String> keys = path.isEmpty()
                ? defaults.getKeys(false)
                : defaults.getConfigurationSection(path).getKeys(false);
        for (String key : keys) {
            String fullPath = path.isEmpty() ? key : path + "." + key;
            if (defaults.isConfigurationSection(fullPath)) {
                if (!current.isConfigurationSection(fullPath)) {
                    current.createSection(fullPath);
                    changed = true;
                }
                changed |= mergeDefaults(current, defaults, fullPath);
            } else if (!current.contains(fullPath)) {
                current.set(fullPath, defaults.get(fullPath));
                changed = true;
            }
        }
        return changed;
    }
}
