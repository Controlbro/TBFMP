package com.tbfmc.tbfmp.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageService {
    private final JavaPlugin plugin;
    private final String prefix;

    public MessageService(JavaPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.prefix = colorize(config.getString("prefix", "&f[TBFMP]"));
    }

    public String getMessage(String path) {
        String value = plugin.getConfig().getString(path, "");
        return colorize(value);
    }

    public String formatMessage(String message) {
        if (message == null || message.isEmpty()) {
            return prefix;
        }
        return prefix + " " + ChatColor.GREEN + colorize(message);
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(formatMessage(message));
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(formatMessage(message));
    }

    public String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
