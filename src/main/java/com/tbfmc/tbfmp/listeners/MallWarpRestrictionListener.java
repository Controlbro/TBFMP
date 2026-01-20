package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.mallwarp.MallWarpRegion;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MallWarpRestrictionListener implements Listener {
    private final MallWarpManager mallWarpManager;
    private final MessageService messages;

    public MallWarpRestrictionListener(MallWarpManager mallWarpManager, MessageService messages) {
        this.mallWarpManager = mallWarpManager;
        this.messages = messages;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!mallWarpManager.isMallPlayer(player)) {
            return;
        }
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        MallWarpRegion region = mallWarpManager.getRegion();
        if (region == null) {
            return;
        }
        if (region.contains(to)) {
            return;
        }
        Location warpLocation = mallWarpManager.getWarpLocation();
        if (warpLocation == null) {
            return;
        }
        messages.sendMessage(player, messages.getMessage("messages.mallwarp-leave-blocked"));
        player.teleportAsync(warpLocation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        mallWarpManager.clearPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!mallWarpManager.isMallPlayer(player)) {
            return;
        }
        Location warpLocation = mallWarpManager.getWarpLocation();
        if (warpLocation == null) {
            return;
        }
        event.setCancelled(true);
        messages.sendMessage(player, messages.getMessage("messages.mallwarp-leave-blocked"));
        player.teleportAsync(warpLocation);
    }
}
