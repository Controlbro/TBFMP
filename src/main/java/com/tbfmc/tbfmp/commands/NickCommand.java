package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {
    private final MessageService messages;

    public NickCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }

        if (!player.hasPermission("oakglowutil.nick")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            messages.sendMessage(player, messages.getMessage("messages.nick-usage"));
            return true;
        }

        String rawNickname = String.join(" ", args).trim();
        if (rawNickname.equalsIgnoreCase("off") || rawNickname.equalsIgnoreCase("clear")
                || rawNickname.equalsIgnoreCase("reset")) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            messages.sendMessage(player, messages.getMessage("messages.nick-cleared"));
            return true;
        }

        String nickname = messages.colorize(rawNickname);
        player.setDisplayName(nickname);
        player.setPlayerListName(nickname);
        messages.sendMessage(player, messages.getMessage("messages.nick-set")
                .replace("{nickname}", nickname));
        return true;
    }
}
