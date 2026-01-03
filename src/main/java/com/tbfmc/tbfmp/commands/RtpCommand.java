package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.rtp.RtpManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCommand implements CommandExecutor {
    private final RtpManager rtpManager;
    private final MessageService messages;

    public RtpCommand(RtpManager rtpManager, MessageService messages) {
        this.rtpManager = rtpManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        rtpManager.requestRtp(player);
        return true;
    }
}
