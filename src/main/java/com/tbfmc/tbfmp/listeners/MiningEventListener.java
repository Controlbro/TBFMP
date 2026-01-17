package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.event.MiningEventService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.EnumSet;
import java.util.Set;

public class MiningEventListener implements Listener {
    private static final Set<Material> TRACKED_BLOCKS = EnumSet.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE
    );
    private final MiningEventService eventService;

    public MiningEventListener(MiningEventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!TRACKED_BLOCKS.contains(event.getBlock().getType())) {
            return;
        }
        Player player = event.getPlayer();
        eventService.handleBlockMined(player, 1);
    }
}
