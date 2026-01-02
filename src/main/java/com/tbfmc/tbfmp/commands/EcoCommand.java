package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {
    private final BalanceStorage storage;
    private final MessageService messages;

    public EcoCommand(BalanceStorage storage, MessageService messages) {
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tbfmp.admin.eco")) {
            sender.sendMessage(messages.getMessage("messages.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(messages.formatMessage("&aUsage: /eco <give|take|set> <player> <amount>"));
            return true;
        }

        String action = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(messages.getMessage("messages.player-not-found"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messages.getMessage("messages.invalid-amount"));
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(messages.getMessage("messages.invalid-amount"));
            return true;
        }

        switch (action) {
            case "give" -> storage.addBalance(target.getUniqueId(), amount);
            case "take" -> storage.subtractBalance(target.getUniqueId(), amount);
            case "set" -> storage.setBalance(target.getUniqueId(), amount);
            default -> {
                sender.sendMessage(messages.formatMessage("&aUsage: /eco <give|take|set> <player> <amount>"));
                return true;
            }
        }

        String updated = messages.getMessage("messages.balance-updated")
                .replace("{player}", target.getName() == null ? args[1] : target.getName())
                .replace("{balance}", String.format("%.2f", storage.getBalance(target.getUniqueId())));
        sender.sendMessage(updated);
        return true;
    }
}
