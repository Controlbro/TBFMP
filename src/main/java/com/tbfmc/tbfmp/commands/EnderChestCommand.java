package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand implements CommandExecutor {
    private final MessageService messages;

    public EnderChestCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!sender.hasPermission("oakglowutil.enderchest")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        player.openInventory(player.getEnderChest());
        messages.sendMessage(player, messages.getMessage("messages.enderchest-opened"));
        return true;
    }
}
