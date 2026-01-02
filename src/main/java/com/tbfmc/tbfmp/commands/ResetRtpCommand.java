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
            sender.sendMessage(messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(messages.formatMessage("&aUsage: /resetrtp <player>"));
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(messages.getMessage("messages.player-not-found"));
            return true;
        }
        rtpManager.resetRtp(target.getUniqueId());
        sender.sendMessage(messages.formatMessage("&aRTP reset for &f" + (target.getName() == null ? args[0] : target.getName())));
        if (target.isOnline()) {
            target.getPlayer().sendMessage(messages.getMessage("messages.rtp-reset"));
        }
        return true;
    }
}
