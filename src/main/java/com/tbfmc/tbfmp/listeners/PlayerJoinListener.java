package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.CustomConfig;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerJoinListener implements Listener {
    private final MessageService messages;
    private final CustomConfig customConfig;

    public PlayerJoinListener(MessageService messages, CustomConfig customConfig) {
        this.messages = messages;
        this.customConfig = customConfig;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        String joinMessage = resolvePriorityMessage(event.getPlayer(), "join-messages");
        if (joinMessage == null) {
            event.setJoinMessage(null);
        } else {
            event.setJoinMessage(messages.colorize(joinMessage.replace("{player}", name)));
        }
        messages.sendMessage(event.getPlayer(), messages.getMessage("messages.motd"));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        String leaveMessage = resolvePriorityMessage(event.getPlayer(), "leave-messages");
        if (leaveMessage == null) {
            event.setQuitMessage(null);
        } else {
            event.setQuitMessage(messages.colorize(leaveMessage.replace("{player}", name)));
        }
    }

    private String resolvePriorityMessage(Player player, String basePath) {
        FileConfiguration config = customConfig.getConfig();
        if (!config.getBoolean(basePath + ".enabled", true)) {
            return null;
        }
        List<String> priorities = config.getStringList(basePath + ".priority");
        ConfigurationSection messagesSection = config.getConfigurationSection(basePath + ".messages");
        if (messagesSection == null) {
            return null;
        }
        for (String key : priorities) {
            String permission = messagesSection.getString(key + ".permission", "");
            if (permission.isEmpty() || player.hasPermission(permission)) {
                return messagesSection.getString(key + ".message");
            }
        }
        String defaultKey = messagesSection.contains("default") ? "default" : null;
        if (defaultKey == null) {
            return null;
        }
        String permission = messagesSection.getString(defaultKey + ".permission", "");
        if (permission.isEmpty() || player.hasPermission(permission)) {
            return messagesSection.getString(defaultKey + ".message");
        }
        return null;
    }
}
