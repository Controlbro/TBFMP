package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MsgCommand implements CommandExecutor {
    private final MessageService messages;

    public MsgCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            messages.sendMessage(sender, messages.getMessage("messages.msg-usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
            return true;
        }

        if (sender instanceof Player player && target.equals(player)) {
            messages.sendMessage(player, messages.getMessage("messages.msg-self"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        messages.sendMessage(sender, messages.getMessage("messages.msg-sent")
                .replace("{player}", target.getName())
                .replace("{message}", message));
        messages.sendMessage(target, messages.getMessage("messages.msg-received")
                .replace("{player}", sender.getName())
                .replace("{message}", message));
        return true;
    }
}
