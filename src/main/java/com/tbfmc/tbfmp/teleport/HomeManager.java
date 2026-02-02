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
import java.util.UUID;

public class HomeManager {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeManager(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void setHome(UUID uuid, String name, Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        String key = normalize(name);
        homes.computeIfAbsent(uuid, ignored -> new HashMap<>())
                .put(key, location.clone());
        FileConfiguration data = getDataFile();
        setLocation(data, "homes.players." + uuid + "." + key, location);
        save();
    }

    public Location getHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) {
            return null;
        }
        Location location = playerHomes.get(normalize(name));
        return location != null ? location.clone() : null;
    }

    public int getHomeCount(UUID uuid) {
        Map<String, Location> playerHomes = homes.get(uuid);
        return playerHomes != null ? playerHomes.size() : 0;
    }

    public List<String> getHomeNames(UUID uuid) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null || playerHomes.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(playerHomes.keySet());
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
        homes.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Map<String, Location>> entry : homes.entrySet()) {
            for (Map.Entry<String, Location> homeEntry : entry.getValue().entrySet()) {
                setLocation(data, "homes.players." + entry.getKey() + "." + homeEntry.getKey(),
                        homeEntry.getValue());
            }
        }
    }

    private void load() {
        FileConfiguration data = getDataFile();
        org.bukkit.configuration.ConfigurationSection section =
                data.getConfigurationSection("homes.players");
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
            org.bukkit.configuration.ConfigurationSection playerSection =
                    section.getConfigurationSection(key);
            if (playerSection == null) {
                continue;
            }
            Map<String, Location> playerHomes = new HashMap<>();
            for (String homeName : playerSection.getKeys(false)) {
                Location location = getLocation(data, "homes.players." + key + "." + homeName);
                if (location != null) {
                    playerHomes.put(homeName.toLowerCase(), location);
                }
            }
            if (!playerHomes.isEmpty()) {
                homes.put(uuid, playerHomes);
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
