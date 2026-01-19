package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackWarpCommand implements CommandExecutor {
    private final MallWarpManager mallWarpManager;
    private final MessageService messages;

    public BackWarpCommand(MallWarpManager mallWarpManager, MessageService messages) {
        this.mallWarpManager = mallWarpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!mallWarpManager.hasBackLocation(player)) {
            messages.sendMessage(player, messages.getMessage("messages.backwarp-missing"));
            return true;
        }
        Location returnLocation = mallWarpManager.exitMall(player);
        if (returnLocation != null) {
            player.teleportAsync(returnLocation);
        }
        messages.sendMessage(player, messages.getMessage("messages.backwarp-success"));
        return true;
    }
}
