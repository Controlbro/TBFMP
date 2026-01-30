package com.tbfmc.tbfmp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DragonDropListener implements Listener {
    private final JavaPlugin plugin;

    public DragonDropListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ENDER_DRAGON) {
            return;
        }
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("dragon-drops.enabled", false)) {
            return;
        }
        Location dropLocation = getDropLocation(config);
        if (dropLocation == null) {
            return;
        }
        List<ItemStack> drops = buildDrops(config);
        if (drops.isEmpty()) {
            return;
        }
        World world = dropLocation.getWorld();
        if (world == null) {
            return;
        }
        for (ItemStack drop : drops) {
            world.dropItem(dropLocation, drop);
        }
    }

    private Location getDropLocation(FileConfiguration config) {
        String worldName = config.getString("dragon-drops.location.world");
        if (worldName == null || worldName.isBlank()) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        double x = config.getDouble("dragon-drops.location.x");
        double y = config.getDouble("dragon-drops.location.y");
        double z = config.getDouble("dragon-drops.location.z");
        float yaw = (float) config.getDouble("dragon-drops.location.yaw");
        float pitch = (float) config.getDouble("dragon-drops.location.pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    private List<ItemStack> buildDrops(FileConfiguration config) {
        List<ItemStack> drops = new ArrayList<>();
        if (config.getBoolean("dragon-drops.drop-elytra", true)) {
            drops.add(new ItemStack(Material.ELYTRA));
        }
        if (config.getBoolean("dragon-drops.drop-egg", true)) {
            drops.add(new ItemStack(Material.DRAGON_EGG));
        }
        if (config.getBoolean("dragon-drops.drop-head", false)) {
            drops.add(new ItemStack(Material.DRAGON_HEAD));
        }
        return drops;
    }
}
