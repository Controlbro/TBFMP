package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.TpaSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaToggleCommand implements CommandExecutor {
    private final TpaSettingsStorage tpaSettingsStorage;
    private final MessageService messages;

    public TpaToggleCommand(TpaSettingsStorage tpaSettingsStorage, MessageService messages) {
        this.tpaSettingsStorage = tpaSettingsStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        boolean enabled = tpaSettingsStorage.toggle(player.getUniqueId());
        messages.sendMessage(player, messages.getMessage(
                enabled ? "messages.tpa-toggle-on" : "messages.tpa-toggle-off"));
        return true;
    }
}
