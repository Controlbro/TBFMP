package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PTimeCommand implements CommandExecutor {
    private final MessageService messages;

    public PTimeCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!sender.hasPermission("oakglowutil.ptime")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length != 1) {
            messages.sendMessage(sender, messages.getMessage("messages.ptime-usage"));
            return true;
        }
        String mode = args[0].toLowerCase();
        long time;
        switch (mode) {
            case "day" -> time = 1000L;
            case "night" -> time = 13000L;
            case "sunrise" -> time = 23000L;
            case "sunset" -> time = 12000L;
            default -> {
                messages.sendMessage(sender, messages.getMessage("messages.ptime-usage"));
                return true;
            }
        }
        player.setPlayerTime(time, false);
        String message = messages.getMessage("messages.ptime-updated").replace("{time}", mode);
        messages.sendMessage(player, message);
        return true;
    }
}
