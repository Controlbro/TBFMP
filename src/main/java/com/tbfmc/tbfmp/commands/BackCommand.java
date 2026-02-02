package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.BackLocationManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final BackLocationManager backLocationManager;
    private final MessageService messages;

    public BackCommand(BackLocationManager backLocationManager, MessageService messages) {
        this.backLocationManager = backLocationManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        Location backLocation = backLocationManager.getBackLocation(player.getUniqueId());
        if (backLocation == null) {
            messages.sendMessage(player, messages.getMessage("messages.back-missing"));
            return true;
        }
        player.teleportAsync(backLocation);
        messages.sendMessage(player, messages.getMessage("messages.back-success"));
        return true;
    }
}
