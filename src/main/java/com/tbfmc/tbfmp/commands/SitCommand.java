package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.sit.SitManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SitCommand implements CommandExecutor {
    private final SitManager sitManager;
    private final MessageService messages;

    public SitCommand(SitManager sitManager, MessageService messages) {
        this.sitManager = sitManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        if (!sitManager.canSit(player)) {
            return true;
        }
        sitManager.sitOnGround(player);
        return true;
    }
}
