package com.tbfmc.tbfmp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnService {
    private final JavaPlugin plugin;

    public SpawnService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setSpawnLocation(Location location) {
        if (location.getWorld() == null) {
            return;
        }
        plugin.getConfig().set("spawn.enabled", true);
        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public Location getSpawnLocation() {
        if (!plugin.getConfig().getBoolean("spawn.enabled", false)) {
            return null;
        }
        String worldName = plugin.getConfig().getString("spawn.world");
        if (worldName == null || worldName.isBlank()) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }
}
