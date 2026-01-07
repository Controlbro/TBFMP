package com.tbfmc.tbfmp.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SitDamageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Entity vehicle = victim.getVehicle();
        if (vehicle == null) {
            return;
        }
        if (vehicle.getUniqueId().equals(damager.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
