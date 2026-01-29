package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RealnameCommand implements CommandExecutor {
    private final MessageService messages;

    public RealnameCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            messages.sendMessage(sender, messages.getMessage("messages.realname-usage"));
            return true;
        }

        String queryRaw = String.join(" ", args).trim();
        if (queryRaw.isBlank()) {
            messages.sendMessage(sender, messages.getMessage("messages.realname-usage"));
            return true;
        }

        String query = ChatColor.stripColor(messages.colorize(queryRaw)).trim();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String displayName = player.getDisplayName();
            if (displayName == null || displayName.isBlank()) {
                displayName = player.getName();
            }
            String stripped = ChatColor.stripColor(displayName);
            if (stripped != null && stripped.equalsIgnoreCase(query)) {
                messages.sendMessage(sender, messages.getMessage("messages.realname-result")
                        .replace("{nickname}", queryRaw)
                        .replace("{player}", player.getName()));
                return true;
            }
        }

        messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
        return true;
    }
}
