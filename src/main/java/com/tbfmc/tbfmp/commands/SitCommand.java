package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SitCommand implements CommandExecutor, TabCompleter {
    private final SitSettingsStorage settingsStorage;
    private final MessageService messages;

    public SitCommand(SitSettingsStorage settingsStorage, MessageService messages) {
        this.settingsStorage = settingsStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        if (args.length < 2) {
            messages.sendMessage(player, messages.getMessage("messages.sit-usage"));
            return true;
        }

        String category = args[0].toLowerCase(Locale.ROOT);
        String action = args[1].toLowerCase(Locale.ROOT);
        boolean enabled;
        if ("enable".equals(action)) {
            enabled = true;
        } else if ("disable".equals(action)) {
            enabled = false;
        } else {
            messages.sendMessage(player, messages.getMessage("messages.sit-usage"));
            return true;
        }

        if ("chair".equals(category)) {
            settingsStorage.setChairEnabled(player.getUniqueId(), enabled);
            messages.sendMessage(player, messages.getMessage(enabled ? "messages.sit-chair-enabled" : "messages.sit-chair-disabled"));
            return true;
        }

        if ("player".equals(category)) {
            settingsStorage.setPlayerEnabled(player.getUniqueId(), enabled);
            messages.sendMessage(player, messages.getMessage(enabled ? "messages.sit-player-enabled" : "messages.sit-player-disabled"));
            return true;
        }

        messages.sendMessage(player, messages.getMessage("messages.sit-usage"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("player", "chair");
        }
        if (args.length == 2) {
            return Arrays.asList("enable", "disable");
        }
        return Collections.emptyList();
    }
}
