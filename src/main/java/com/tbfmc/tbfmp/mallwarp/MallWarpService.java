package com.tbfmc.tbfmp.mallwarp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MallWarpService {
    private final JavaPlugin plugin;

    public MallWarpService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setWarpLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        FileConfiguration config = plugin.getConfig();
        config.set("mallwarp.warp.enabled", true);
        config.set("mallwarp.warp.world", location.getWorld().getName());
        config.set("mallwarp.warp.x", location.getX());
        config.set("mallwarp.warp.y", location.getY());
        config.set("mallwarp.warp.z", location.getZ());
        config.set("mallwarp.warp.yaw", location.getYaw());
        config.set("mallwarp.warp.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public Location getWarpLocation() {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("mallwarp.warp.enabled", false)) {
            return null;
        }
        String worldName = config.getString("mallwarp.warp.world");
        if (worldName == null) {
            return null;
        }
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            return null;
        }
        double x = config.getDouble("mallwarp.warp.x");
        double y = config.getDouble("mallwarp.warp.y");
        double z = config.getDouble("mallwarp.warp.z");
        float yaw = (float) config.getDouble("mallwarp.warp.yaw");
        float pitch = (float) config.getDouble("mallwarp.warp.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void setRegion(MallWarpRegion region) {
        if (region == null) {
            return;
        }
        FileConfiguration config = plugin.getConfig();
        config.set("mallwarp.region.enabled", true);
        config.set("mallwarp.region.world", region.getWorldName());
        config.set("mallwarp.region.min-x", region.getMinX());
        config.set("mallwarp.region.min-y", region.getMinY());
        config.set("mallwarp.region.min-z", region.getMinZ());
        config.set("mallwarp.region.max-x", region.getMaxX());
        config.set("mallwarp.region.max-y", region.getMaxY());
        config.set("mallwarp.region.max-z", region.getMaxZ());
        plugin.saveConfig();
    }

    public MallWarpRegion getRegion() {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("mallwarp.region.enabled", false)) {
            return null;
        }
        String worldName = config.getString("mallwarp.region.world");
        if (worldName == null) {
            return null;
        }
        double minX = config.getDouble("mallwarp.region.min-x");
        double minY = config.getDouble("mallwarp.region.min-y");
        double minZ = config.getDouble("mallwarp.region.min-z");
        double maxX = config.getDouble("mallwarp.region.max-x");
        double maxY = config.getDouble("mallwarp.region.max-y");
        double maxZ = config.getDouble("mallwarp.region.max-z");
        return new MallWarpRegion(worldName, minX, minY, minZ, maxX, maxY, maxZ);
    }
}
