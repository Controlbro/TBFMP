package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.WarpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {
    private static final String PERMISSION = "oakglowutil.admin.setwarp";

    private final WarpManager warpManager;
    private final MessageService messages;

    public SetWarpCommand(WarpManager warpManager, MessageService messages) {
        this.warpManager = warpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!player.hasPermission(PERMISSION)) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.setwarp-usage"));
            return true;
        }
        String name = args[0];
        warpManager.setWarp(name, player.getLocation());
        messages.sendMessage(player, messages.getMessage("messages.warp-set")
                .replace("{warp}", name));
        return true;
    }
}
