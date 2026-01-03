package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.TBFMPPlugin;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TbfmcCommand implements CommandExecutor {
    private final TBFMPPlugin plugin;
    private final MessageService messages;

    public TbfmcCommand(TBFMPPlugin plugin, MessageService messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("tbfmp.admin.reload")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            plugin.reloadPluginConfig();
            messages.sendMessage(sender, messages.getMessage("messages.reload-complete"));
            return true;
        }

        messages.sendMessage(sender, messages.getMessage("messages.tbfmc-usage"));
        return true;
    }
}
