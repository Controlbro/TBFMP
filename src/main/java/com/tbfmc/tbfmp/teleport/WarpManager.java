package com.tbfmc.tbfmp.teleport;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpManager {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<String, Location> warps = new HashMap<>();

    public WarpManager(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "warps.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void setWarp(String name, Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        String key = normalize(name);
        warps.put(key, location.clone());
        FileConfiguration data = getDataFile();
        setLocation(data, "warps." + key, location);
        save();
    }

    public Location getWarp(String name) {
        Location location = warps.get(normalize(name));
        return location != null ? location.clone() : null;
    }

    public List<String> getWarpNames() {
        if (warps.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(warps.keySet());
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

    public void reloadFromUnifiedData() {
        warps.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            setLocation(data, "warps." + entry.getKey(), entry.getValue());
        }
    }

    private void load() {
        FileConfiguration data = getDataFile();
        org.bukkit.configuration.ConfigurationSection section = data.getConfigurationSection("warps");
        if (section == null) {
            return;
        }
        for (String name : section.getKeys(false)) {
            Location location = getLocation(data, "warps." + name);
            if (location != null) {
                warps.put(name.toLowerCase(), location);
            }
        }
    }

    private FileConfiguration getDataFile() {
        return unifiedDataFile.isEnabled() ? unifiedDataFile.getData() : legacyData;
    }

    private String normalize(String name) {
        return name == null ? "" : name.toLowerCase();
    }

    private void setLocation(FileConfiguration data, String path, Location location) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        data.set(path + ".world", world.getName());
        data.set(path + ".x", location.getX());
        data.set(path + ".y", location.getY());
        data.set(path + ".z", location.getZ());
        data.set(path + ".yaw", location.getYaw());
        data.set(path + ".pitch", location.getPitch());
    }

    private Location getLocation(FileConfiguration data, String path) {
        String worldName = data.getString(path + ".world");
        if (worldName == null || worldName.isBlank()) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        double x = data.getDouble(path + ".x");
        double y = data.getDouble(path + ".y");
        double z = data.getDouble(path + ".z");
        float yaw = (float) data.getDouble(path + ".yaw");
        float pitch = (float) data.getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
