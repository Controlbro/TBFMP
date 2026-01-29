package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCommand implements CommandExecutor {
    private final MessageService messages;

    public WorkbenchCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }

        if (!player.hasPermission("oakglowutil.workbench")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }

        player.openWorkbench(null, true);
        messages.sendMessage(player, messages.getMessage("messages.workbench-opened"));
        return true;
    }
}
