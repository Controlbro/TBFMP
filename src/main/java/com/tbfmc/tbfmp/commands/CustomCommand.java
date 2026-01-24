package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.TBFMPPlugin;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CustomCommand implements CommandExecutor {
    private final TBFMPPlugin plugin;
    private final MessageService messages;

    public CustomCommand(TBFMPPlugin plugin, MessageService messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("oakglowutil.admin.customreload")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            plugin.reloadCustomConfig();
            messages.sendMessage(sender, messages.getMessage("messages.reload-complete"));
            return true;
        }

        messages.sendMessage(sender, "&aUsage: &f/custom reload");
        return true;
    }
}
