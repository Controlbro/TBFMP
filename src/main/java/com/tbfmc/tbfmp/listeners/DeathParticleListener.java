package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.CustomConfig;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathParticleListener implements Listener {
    private final JavaPlugin plugin;
    private final CustomConfig customConfig;

    public DeathParticleListener(JavaPlugin plugin, CustomConfig customConfig) {
        this.plugin = plugin;
        this.customConfig = customConfig;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        FileConfiguration config = customConfig.getConfig();
        if (!config.getBoolean("death-particles.enabled", true)) {
            return;
        }

        ConfigurationSection particlesSection = config.getConfigurationSection("death-particles.particles");
        if (particlesSection == null) {
            return;
        }

        String selectedKey = null;
        for (String key : particlesSection.getKeys(false)) {
            String permission = particlesSection.getString(key + ".permission", "");
            if (!permission.isEmpty() && player.hasPermission(permission)) {
                selectedKey = key;
                break;
            }
        }
        if (selectedKey == null) {
            selectedKey = config.getString("death-particles.default");
        }
        if (selectedKey == null || !particlesSection.contains(selectedKey)) {
            return;
        }

        String typeName = particlesSection.getString(selectedKey + ".type", "");
        int count = particlesSection.getInt(selectedKey + ".count", 10);
        double spread = particlesSection.getDouble(selectedKey + ".spread", 0.2);
        int durationTicks = particlesSection.getInt(selectedKey + ".duration-ticks", 10);
        Particle particle;
        try {
            particle = Particle.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return;
        }

        Location location = player.getLocation().add(0.0, 0.5, 0.0);
        int safeDuration = Math.max(1, durationTicks);
        new BukkitRunnable() {
            private int ticks;

            @Override
            public void run() {
                if (ticks >= safeDuration || location.getWorld() == null) {
                    cancel();
                    return;
                }
                location.getWorld().spawnParticle(
                        particle,
                        location,
                        count,
                        spread,
                        spread,
                        spread,
                        0.01
                );
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
