package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final MessageService messages;

    public PlayerJoinListener(MessageService messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        messages.sendMessage(event.getPlayer(), messages.getMessage("messages.motd"));
    }
}
