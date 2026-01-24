package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMallWarpCommand implements CommandExecutor {
    private final MallWarpManager mallWarpManager;
    private final MessageService messages;

    public SetMallWarpCommand(MallWarpManager mallWarpManager, MessageService messages) {
        this.mallWarpManager = mallWarpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!sender.hasPermission("oakglowutil.admin.mallwarp")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        mallWarpManager.setWarpLocation(player.getLocation());
        messages.sendMessage(sender, messages.getMessage("messages.mallwarp-set"));
        return true;
    }
}
