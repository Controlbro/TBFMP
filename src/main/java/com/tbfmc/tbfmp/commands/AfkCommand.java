package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.afk.AfkManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand implements CommandExecutor {
    private final AfkManager afkManager;
    private final MessageService messages;

    public AfkCommand(AfkManager afkManager, MessageService messages) {
        this.afkManager = afkManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        afkManager.toggleAfk(player);
        return true;
    }
}
