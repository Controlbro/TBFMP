package com.tbfmc.tbfmp.listeners;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class CritParticleListener implements Listener {
    private static final String PERMISSION_GOLD = "critcolor.gold";
    private static final String PERMISSION_RED = "critcolor.red";
    private static final String PERMISSION_BLUE = "critcolor.blue";

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!isTrueCritical(player)) {
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

    private boolean isTrueCritical(Player player) {
        if (player.isOnGround()) {
            return false;
        }
        if (player.getFallDistance() <= 0.0f) {
            return false;
        }
        if (player.isInWater()) {
            return false;
        }
        if (player.isClimbing()) {
            return false;
        }
        if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            return false;
        }
        return !player.isInsideVehicle();
    }

    private Particle.DustOptions resolveDustOptions(Player player) {
        if (player.hasPermission(PERMISSION_GOLD)) {
            return new Particle.DustOptions(Color.fromRGB(255, 215, 0), 1.0f);
        }
        if (player.hasPermission(PERMISSION_RED)) {
            return new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f);
        }
        if (player.hasPermission(PERMISSION_BLUE)) {
            return new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.0f);
        }
        return null;
    }
}
