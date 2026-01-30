package com.tbfmc.tbfmp.mallwarp;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MallWarpManager {
    private final MallWarpService mallWarpService;
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Set<UUID> mallPlayers = new HashSet<>();

    public MallWarpManager(JavaPlugin plugin, MallWarpService mallWarpService, UnifiedDataFile unifiedDataFile) {
        this.mallWarpService = mallWarpService;
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "mallwarp-state.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void setWarpLocation(Location location) {
        mallWarpService.setWarpLocation(location);
    }

    public Location getWarpLocation() {
        return mallWarpService.getWarpLocation();
    }

    public void setRegion(MallWarpRegion region) {
        mallWarpService.setRegion(region);
    }

    public MallWarpRegion getRegion() {
        return mallWarpService.getRegion();
    }

    public void enterMall(Player player) {
        UUID playerId = player.getUniqueId();
        Location backLocation = player.getLocation().clone();
        backLocations.put(playerId, backLocation);
        mallPlayers.add(playerId);
        setPlayerState(playerId, backLocation, true);
        save();
    }

    public boolean isMallPlayer(Player player) {
        return mallPlayers.contains(player.getUniqueId());
    }

    public boolean hasBackLocation(Player player) {
        return backLocations.containsKey(player.getUniqueId());
    }

    public Location exitMall(Player player) {
        UUID playerId = player.getUniqueId();
        mallPlayers.remove(playerId);
        Location backLocation = backLocations.remove(playerId);
        clearPlayerData(playerId);
        save();
        return backLocation;
    }

    public void clearPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        mallPlayers.remove(playerId);
        backLocations.remove(playerId);
        clearPlayerData(playerId);
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
        mallPlayers.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Location> entry : backLocations.entrySet()) {
            setLocation(data, "mallwarp.players." + entry.getKey() + ".back", entry.getValue());
        }
        for (UUID playerId : mallPlayers) {
            data.set("mallwarp.players." + playerId + ".warped", true);
        }
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            loadFromConfig(unifiedDataFile.getData());
            return;
        }
        loadFromConfig(legacyData);
    }

    private void loadFromConfig(FileConfiguration data) {
        org.bukkit.configuration.ConfigurationSection section =
                data.getConfigurationSection("mallwarp.players");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            UUID playerId;
            try {
                playerId = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            boolean warped = data.getBoolean("mallwarp.players." + key + ".warped", false);
            if (warped) {
                mallPlayers.add(playerId);
            }
            Location backLocation = getLocation(data, "mallwarp.players." + key + ".back");
            if (warped && backLocation != null) {
                backLocations.put(playerId, backLocation);
            }
        }
    }

    private void setPlayerState(UUID playerId, Location backLocation, boolean warped) {
        String basePath = "mallwarp.players." + playerId;
        FileConfiguration data = unifiedDataFile.isEnabled() ? unifiedDataFile.getData() : legacyData;
        data.set(basePath + ".warped", warped);
        setLocation(data, basePath + ".back", backLocation);
    }

    private void clearPlayerData(UUID playerId) {
        String basePath = "mallwarp.players." + playerId;
        FileConfiguration data = unifiedDataFile.isEnabled() ? unifiedDataFile.getData() : legacyData;
        data.set(basePath, null);
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
