package com.tbfmc.tbfmp.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageService {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern PAREN_HEX_PATTERN = Pattern.compile("\\(#([A-Fa-f0-9]{6})\\)");
    private final JavaPlugin plugin;
    private final FileConfiguration messagesConfig;
    private final String prefix;

    public MessageService(JavaPlugin plugin, FileConfiguration messagesConfig) {
        this.plugin = plugin;
        this.messagesConfig = messagesConfig;
        FileConfiguration config = plugin.getConfig();
        this.prefix = colorize(config.getString("prefix", "&f[&6OGN&f]"));
    }

    public String getMessage(String path) {
        String value = messagesConfig.getString(path, "");
        if ((value == null || value.isEmpty()) && path.startsWith("messages.")) {
            String legacyPath = path.substring("messages.".length());
            value = messagesConfig.getString(legacyPath, "");
        }
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
        if (input == null) {
            return "";
        }
        String withHex = applyHexPattern(input, HEX_PATTERN);
        withHex = applyHexPattern(withHex, PAREN_HEX_PATTERN);
        return ChatColor.translateAlternateColorCodes('&', withHex);
    }

    private String applyHexPattern(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String color = matcher.group(1);
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + color).toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
