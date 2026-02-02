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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackLocationManager {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Location> backLocations = new HashMap<>();

    public BackLocationManager(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "back-locations.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void setBackLocation(UUID uuid, Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        backLocations.put(uuid, location.clone());
        FileConfiguration data = getDataFile();
        setLocation(data, "back-locations." + uuid, location);
        save();
    }

    public Location getBackLocation(UUID uuid) {
        Location location = backLocations.get(uuid);
        return location != null ? location.clone() : null;
    }

    public void clearBackLocation(UUID uuid) {
        backLocations.remove(uuid);
        FileConfiguration data = getDataFile();
        data.set("back-locations." + uuid, null);
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

    public void reloadFromUnifiedData() {
        backLocations.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Location> entry : backLocations.entrySet()) {
            setLocation(data, "back-locations." + entry.getKey(), entry.getValue());
        }
    }

    private void load() {
        FileConfiguration data = getDataFile();
        org.bukkit.configuration.ConfigurationSection section =
                data.getConfigurationSection("back-locations");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            Location location = getLocation(data, "back-locations." + key);
            if (location != null) {
                backLocations.put(uuid, location);
            }
        }
    }

    private FileConfiguration getDataFile() {
        return unifiedDataFile.isEnabled() ? unifiedDataFile.getData() : legacyData;
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
