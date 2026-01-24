package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand implements CommandExecutor {
    private final MessageService messages;

    public TpHereCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!player.hasPermission("tbfmp.admin.tphere")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.tphere-usage"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return true;
        }
        Location destination = player.getLocation();
        target.teleportAsync(destination);
        messages.sendMessage(player, messages.getMessage("messages.tphere-success")
                .replace("{player}", target.getName()));
        messages.sendMessage(target, messages.getMessage("messages.tphere-target")
                .replace("{player}", player.getName()));
        return true;
    }
}
