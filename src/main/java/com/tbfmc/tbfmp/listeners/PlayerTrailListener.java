package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.CustomConfig;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTrailListener implements Listener {
    private final JavaPlugin plugin;
    private final CustomConfig customConfig;
    private final Map<UUID, Long> lastTrail = new HashMap<>();

    public PlayerTrailListener(JavaPlugin plugin, CustomConfig customConfig) {
        this.plugin = plugin;
        this.customConfig = customConfig;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        Player player = event.getPlayer();
        if (!hasMoved(event)) {
            return;
        }
        ConfigurationSection trails = customConfig.getConfig().getConfigurationSection("trails");
        if (trails == null || !trails.getBoolean("enabled", true)) {
            return;
        }
        List<String> disabledWorlds = trails.getStringList("disabled-worlds");
        if (disabledWorlds != null && disabledWorlds.contains(player.getWorld().getName())) {
            return;
        }
        long intervalTicks = Math.max(1L, trails.getLong("interval-ticks", 5L));
        long now = System.currentTimeMillis();
        long last = lastTrail.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < intervalTicks * 50L) {
            return;
        }
        ConfigurationSection particleSection = trails.getConfigurationSection("particles");
        if (particleSection == null) {
            return;
        }
        String selectedKey = selectTrailKey(player, particleSection, trails.getString("default"));
        if (selectedKey == null || !particleSection.contains(selectedKey)) {
            return;
        }
        ConfigurationSection selected = particleSection.getConfigurationSection(selectedKey);
        if (selected == null) {
            return;
        }
        Particle particle = resolveParticle(selected.getString("type"));
        if (particle == null) {
            return;
        }
        int count = Math.max(1, selected.getInt("count", 2));
        double offsetX = selected.getDouble("offset.x", 0.2);
        double offsetY = selected.getDouble("offset.y", 0.1);
        double offsetZ = selected.getDouble("offset.z", 0.2);
        double extra = selected.getDouble("extra", 0.01);
        Location location = player.getLocation().add(0.0, 0.1, 0.0);
        if (particle == Particle.REDSTONE) {
            Particle.DustOptions dustOptions = buildDustOptions(selected);
            if (dustOptions == null) {
                return;
            }
            player.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, dustOptions);
        } else {
            player.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
        }
        lastTrail.put(player.getUniqueId(), now);
    }

    private boolean hasMoved(PlayerMoveEvent event) {
        if (event.getFrom() == null || event.getTo() == null) {
            return false;
        }
        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }

    private String selectTrailKey(Player player, ConfigurationSection particlesSection, String fallback) {
        for (String key : particlesSection.getKeys(false)) {
            String permission = particlesSection.getString(key + ".permission", "");
            if (!permission.isEmpty() && player.hasPermission(permission)) {
                return key;
            }
        }
        return fallback;
    }

    private Particle resolveParticle(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return null;
        }
        try {
            return Particle.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Particle.DustOptions buildDustOptions(ConfigurationSection section) {
        String colorValue = section.getString("color", "#FFFFFF");
        float size = (float) section.getDouble("size", 1.0);
        Color color = parseColor(colorValue);
        if (color == null) {
            return null;
        }
        return new Particle.DustOptions(color, Math.max(0.1f, size));
    }

    private Color parseColor(String input) {
        if (input == null) {
            return null;
        }
        String normalized = input.startsWith("#") ? input.substring(1) : input;
        if (normalized.length() != 6) {
            return null;
        }
        try {
            int rgb = Integer.parseInt(normalized, 16);
            return Color.fromRGB(rgb);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
