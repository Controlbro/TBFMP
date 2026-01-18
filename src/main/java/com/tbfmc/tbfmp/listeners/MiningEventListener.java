package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.event.MiningEventService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningEventListener implements Listener {
    private final MiningEventService eventService;

    public MiningEventListener(MiningEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!eventService.isEventEnabled()) {
            return;
        }
        if (!eventService.isBlockTracked(event.getBlock().getType())) {
            return;
        }
        Player player = event.getPlayer();
        eventService.handleBlockMined(player, 1);
    }
}
