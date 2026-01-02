package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayToggleCommand implements CommandExecutor {
    private final PaySettingsStorage paySettingsStorage;
    private final MessageService messages;

    public PayToggleCommand(PaySettingsStorage paySettingsStorage, MessageService messages) {
        this.paySettingsStorage = paySettingsStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        boolean enabled = paySettingsStorage.togglePay(player.getUniqueId());
        messages.sendMessage(player, messages.getMessage(enabled ? "messages.pay-toggle-on" : "messages.pay-toggle-off"));
        return true;
    }
}
