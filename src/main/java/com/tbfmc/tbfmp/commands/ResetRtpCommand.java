package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetRtpCommand implements CommandExecutor {
    private final RtpManager rtpManager;
    private final MessageService messages;

    public ResetRtpCommand(RtpManager rtpManager, MessageService messages) {
        this.rtpManager = rtpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tbfmp.admin.rtp")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            messages.sendMessage(sender, "Usage: /resetrtp <player>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
            return true;
        }
        rtpManager.resetRtp(target.getUniqueId());
        messages.sendMessage(sender, "RTP reset for " + (target.getName() == null ? args[0] : target.getName()));
        if (target.isOnline()) {
            messages.sendMessage(target.getPlayer(), messages.getMessage("messages.rtp-reset"));
        }
        return true;
    }
}
