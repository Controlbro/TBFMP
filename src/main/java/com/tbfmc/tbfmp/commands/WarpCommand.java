package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.WarpManager;
import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.TeleportSound;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarpCommand implements CommandExecutor {
    private final WarpManager warpManager;
    private final MessageService messages;

    public WarpCommand(WarpManager warpManager, MessageService messages) {
        this.warpManager = warpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (args.length < 1) {
            return sendWarpList(player);
        }
        Location location = warpManager.getWarp(args[0]);
        if (location == null) {
            messages.sendMessage(player, messages.getMessage("messages.warp-not-found")
                    .replace("{warp}", args[0]));
            return true;
        }
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                TeleportSound.play(player);
            }
        });
        messages.sendMessage(player, messages.getMessage("messages.warp-teleport")
                .replace("{warp}", args[0]));
        return true;
    }

    private boolean sendWarpList(Player player) {
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
