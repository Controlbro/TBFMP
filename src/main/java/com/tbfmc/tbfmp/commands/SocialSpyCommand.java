package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.staff.SocialSpyManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand implements CommandExecutor {
    private final SocialSpyManager socialSpyManager;
    private final MessageService messages;

    public SocialSpyCommand(SocialSpyManager socialSpyManager, MessageService messages) {
        this.socialSpyManager = socialSpyManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!player.hasPermission("tbfmp.admin.socialspy")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }
        boolean enabled = socialSpyManager.toggle(player.getUniqueId());
        messages.sendMessage(player, messages.getMessage(
                enabled ? "messages.socialspy-enabled" : "messages.socialspy-disabled"));
        return true;
    }
}
