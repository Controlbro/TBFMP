package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.HomeManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class SetHomeCommand implements CommandExecutor {
    private static final String HOMES_PERMISSION_PREFIX = "oakglow.homes.";
    private static final int DEFAULT_HOME_LIMIT = 3;

    private final HomeManager homeManager;
    private final MessageService messages;

    public SetHomeCommand(HomeManager homeManager, MessageService messages) {
        this.homeManager = homeManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.sethome-usage"));
            return true;
        }
        String name = args[0];
        int limit = resolveHomeLimit(player);
        boolean exists = homeManager.getHome(player.getUniqueId(), name) != null;
        if (!exists && homeManager.getHomeCount(player.getUniqueId()) >= limit) {
            messages.sendMessage(player, messages.getMessage("messages.home-limit")
                    .replace("{limit}", String.valueOf(limit)));
            return true;
        }
        homeManager.setHome(player.getUniqueId(), name, player.getLocation());
        messages.sendMessage(player, messages.getMessage("messages.home-set")
                .replace("{home}", name));
        return true;
    }

    private int resolveHomeLimit(Player player) {
        int limit = DEFAULT_HOME_LIMIT;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (!info.getValue()) {
                continue;
            }
            String permission = info.getPermission();
            if (!permission.startsWith(HOMES_PERMISSION_PREFIX)) {
                continue;
            }
            String value = permission.substring(HOMES_PERMISSION_PREFIX.length());
            try {
                int parsed = Integer.parseInt(value);
                if (parsed > limit) {
                    limit = parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return limit;
    }
}
