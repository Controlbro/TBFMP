package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.SpawnService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {
    private final SpawnService spawnService;

    public SpawnListener(SpawnService spawnService) {
        this.spawnService = spawnService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getRespawnLocation() != null) {
            return;
        }
        Location spawn = spawnService.getSpawnLocation();
        if (spawn != null) {
            player.teleportAsync(spawn);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn() || event.isAnchorSpawn()) {
            return;
        }
        Location spawn = spawnService.getSpawnLocation();
        if (spawn != null) {
            event.setRespawnLocation(spawn);
        }
    }
}
