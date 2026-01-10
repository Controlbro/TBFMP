package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.TBFMPPlugin;
import com.tbfmc.tbfmp.util.SpawnService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TbfmcCommand implements CommandExecutor {
    private final TBFMPPlugin plugin;
    private final MessageService messages;
    private final SpawnService spawnService;

    public TbfmcCommand(TBFMPPlugin plugin, MessageService messages, SpawnService spawnService) {
        this.plugin = plugin;
        this.messages = messages;
        this.spawnService = spawnService;
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

        if (args.length > 0 && args[0].equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("tbfmp.admin.setspawn")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            Location location = player.getLocation();
            spawnService.setSpawnLocation(location);
            messages.sendMessage(sender, messages.getMessage("messages.setspawn-success"));
            return true;
        }

        messages.sendMessage(sender, messages.getMessage("messages.tbfmc-usage"));
        return true;
    }
}
