package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.CustomConfig;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class CritParticleListener implements Listener {
    private final CustomConfig customConfig;

    public CritParticleListener(CustomConfig customConfig) {
        this.customConfig = customConfig;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player) {
            return;
        }

        Particle.DustOptions dustOptions = resolveDustOptions(player);
        if (dustOptions == null) {
            return;
        }

        Entity target = event.getEntity();
        target.getWorld().spawnParticle(
                Particle.DUST,
                target.getLocation().add(0.0, target.getHeight() * 0.75, 0.0),
                12,
                0.2,
                0.2,
                0.2,
                0.01,
                dustOptions
        );
    }

    private Particle.DustOptions resolveDustOptions(Player player) {
        FileConfiguration config = customConfig.getConfig();
        if (!config.getBoolean("crit-colors.enabled", true)) {
            return null;
        }

        ConfigurationSection colorsSection = config.getConfigurationSection("crit-colors.colors");
        String selectedKey = null;
        if (colorsSection != null) {
            for (String key : colorsSection.getKeys(false)) {
                String permission = colorsSection.getString(key + ".permission", "");
                if (!permission.isEmpty() && player.hasPermission(permission)) {
                    selectedKey = key;
                    break;
                }
            }
        }
        if (selectedKey == null) {
            selectedKey = config.getString("crit-colors.default");
        }
        if (selectedKey == null || colorsSection == null) {
            return null;
        }

        if ("rainbow".equalsIgnoreCase(selectedKey)) {
            Color rainbow = resolveRainbowColor(config, player);
            return rainbow == null ? null : new Particle.DustOptions(rainbow, 1.0f);
        }

        String colorValue = colorsSection.getString(selectedKey + ".color");
        Color color = parseColor(colorValue);
        if (color == null) {
            return null;
        }
        return new Particle.DustOptions(color, 1.0f);
    }

    private Color resolveRainbowColor(FileConfiguration config, Player player) {
        int speedTicks = Math.max(1, config.getInt("crit-colors.rainbow.speed-ticks", 2));
        List<String> colors = config.getStringList("crit-colors.rainbow.colors");
        if (colors.isEmpty()) {
            return null;
        }
        long fullTime = player.getWorld().getFullTime();
        int index = (int) ((fullTime / speedTicks) % colors.size());
        return parseColor(colors.get(index));
    }

    private Color parseColor(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6) {
            return null;
        }
        try {
            int rgb = Integer.parseInt(hex, 16);
            return Color.fromRGB(rgb);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
