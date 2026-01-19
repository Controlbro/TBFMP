package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.mallwarp.MallWarpRegion;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MallWarpCommand implements CommandExecutor {
    private final MallWarpManager mallWarpManager;
    private final MessageService messages;

    public MallWarpCommand(MallWarpManager mallWarpManager, MessageService messages) {
        this.mallWarpManager = mallWarpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        Location warpLocation = mallWarpManager.getWarpLocation();
        if (warpLocation == null) {
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-not-set"));
            return true;
        }
        MallWarpRegion region = mallWarpManager.getRegion();
        if (region == null) {
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-not-set"));
            return true;
        }
        mallWarpManager.enterMall(player);
        player.teleportAsync(warpLocation);
        messages.sendMessage(player, messages.getMessage("messages.mallwarp-success"));
        messages.sendMessage(player, messages.getMessage("messages.mallwarp-warning"));
        return true;
    }
}
