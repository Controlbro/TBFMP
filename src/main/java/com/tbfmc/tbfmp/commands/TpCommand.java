package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {
    private final MessageService messages;

    public TpCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!player.hasPermission("tbfmp.admin.tp")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
                return true;
            }
            player.teleportAsync(target.getLocation());
            messages.sendMessage(player, messages.getMessage("messages.tp-success-player")
                    .replace("{player}", target.getName()));
            return true;
        }
        if (args.length == 3) {
            Double x = parseDouble(args[0]);
            Double y = parseDouble(args[1]);
            Double z = parseDouble(args[2]);
            if (x == null || y == null || z == null) {
                messages.sendMessage(player, messages.getMessage("messages.tp-invalid-coordinates"));
                return true;
            }
            Location destination = new Location(player.getWorld(), x, y, z,
                    player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleportAsync(destination);
            messages.sendMessage(player, messages.getMessage("messages.tp-success-coordinates")
                    .replace("{x}", x.toString())
                    .replace("{y}", y.toString())
                    .replace("{z}", z.toString()));
            return true;
        }
        messages.sendMessage(player, messages.getMessage("messages.tp-usage"));
        return true;
    }

    private Double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
