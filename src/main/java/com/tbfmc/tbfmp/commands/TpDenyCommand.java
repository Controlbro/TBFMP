package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.TpaManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpDenyCommand implements CommandExecutor {
    private final TpaManager tpaManager;
    private final MessageService messages;

    public TpDenyCommand(TpaManager tpaManager, MessageService messages) {
        this.tpaManager = tpaManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        tpaManager.denyRequest(player);
        return true;
    }
}
