package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    private final MessageService messages;

    public PlayerJoinListener(MessageService messages) {
        this.messages = messages;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        String joinKey = event.getPlayer().hasPlayedBefore() ? "messages.join" : "messages.first-join";
        String joinMessage = messages.getMessage(joinKey)
                .replace("{player}", name);
        event.setJoinMessage(messages.formatMessage(joinMessage));
        messages.sendMessage(event.getPlayer(), messages.getMessage("messages.motd"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        String leaveMessage = messages.getMessage("messages.leave")
                .replace("{player}", name);
        event.setQuitMessage(messages.formatMessage(leaveMessage));
    }
}
