package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.mallwarp.MallWarpManager;
import com.tbfmc.tbfmp.mallwarp.MallWarpRegion;
import com.tbfmc.tbfmp.mallwarp.MallWarpSelectionManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMallWarpRegionCommand implements CommandExecutor {
    private final MallWarpManager mallWarpManager;
    private final MallWarpSelectionManager selectionManager;
    private final MessageService messages;

    public SetMallWarpRegionCommand(MallWarpManager mallWarpManager, MallWarpSelectionManager selectionManager,
                                    MessageService messages) {
        this.mallWarpManager = mallWarpManager;
        this.selectionManager = selectionManager;
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
        Location first = selectionManager.getFirstSelection(player);
        Location second = selectionManager.getSecondSelection(player);
        if (first == null || second == null) {
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-missing"));
            return true;
        }
        if (first.getWorld() == null || second.getWorld() == null
                || !first.getWorld().getName().equals(second.getWorld().getName())) {
            messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-world"));
            return true;
        }
        double minX = Math.min(first.getX(), second.getX());
        double minY = Math.min(first.getY(), second.getY());
        double minZ = Math.min(first.getZ(), second.getZ());
        double maxX = Math.max(first.getX(), second.getX());
        double maxY = Math.max(first.getY(), second.getY());
        double maxZ = Math.max(first.getZ(), second.getZ());
        MallWarpRegion region = new MallWarpRegion(first.getWorld().getName(), minX, minY, minZ, maxX, maxY, maxZ);
        mallWarpManager.setRegion(region);
        messages.sendMessage(player, messages.getMessage("messages.mallwarp-region-set"));
        return true;
    }
}
