package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {
    private final MessageService messages;

    public StaffChatCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tbfmp.staffchat")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length == 0) {
            messages.sendMessage(sender, messages.getMessage("messages.staffchat-usage"));
            return true;
        }
        String message = String.join(" ", args);
        String format = messages.getMessage("messages.staffchat-format")
                .replace("{player}", sender.getName())
                .replace("{message}", message);
        String formatted = messages.colorize(format);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("tbfmp.staffchat")) {
                player.sendMessage(formatted);
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(formatted);
        }
        return true;
    }
}
