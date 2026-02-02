package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.WarpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpsCommand implements CommandExecutor {
    private final WarpManager warpManager;
    private final MessageService messages;

    public WarpsCommand(WarpManager warpManager, MessageService messages) {
        this.warpManager = warpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        List<String> warps = new ArrayList<>(warpManager.getWarpNames());
        Collections.sort(warps);
        if (warps.isEmpty()) {
            messages.sendMessage(player, messages.getMessage("messages.warp-none"));
            return true;
        }
        messages.sendMessage(player, messages.getMessage("messages.warp-list")
                .replace("{warps}", String.join(", ", warps)));
        return true;
    }
}
