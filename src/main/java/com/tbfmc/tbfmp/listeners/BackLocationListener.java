package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.teleport.BackLocationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackLocationListener implements Listener {
    private final BackLocationManager backLocationManager;

    public BackLocationListener(BackLocationManager backLocationManager) {
        this.backLocationManager = backLocationManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        backLocationManager.setBackLocation(player.getUniqueId(), event.getFrom());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        backLocationManager.setBackLocation(player.getUniqueId(), player.getLocation());
    }
}
