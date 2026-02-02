package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.TpaManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaHereCommand implements CommandExecutor {
    private final TpaManager tpaManager;
    private final MessageService messages;

    public TpaHereCommand(TpaManager tpaManager, MessageService messages) {
        this.tpaManager = tpaManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.tpahere-usage"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return true;
        }
        tpaManager.requestTeleport(player, target, TpaManager.TpaRequestType.HERE);
        return true;
    }
}
