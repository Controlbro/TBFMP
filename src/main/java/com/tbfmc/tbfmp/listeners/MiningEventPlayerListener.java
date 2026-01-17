package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.event.MiningEventService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MiningEventPlayerListener implements Listener {
    private final MiningEventService eventService;

    public MiningEventPlayerListener(MiningEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        eventService.applyScoreboard(event.getPlayer());
    }
}
