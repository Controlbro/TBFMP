package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.tablist.TabListService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TabListSessionListener implements Listener {
    private final TabListService tabListService;

    public TabListSessionListener(TabListService tabListService) {
        this.tabListService = tabListService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        tabListService.recordSessionStart(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        tabListService.clearSession(event.getPlayer());
    }
}
