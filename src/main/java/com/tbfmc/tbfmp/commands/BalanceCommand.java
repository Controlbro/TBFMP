package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {
    private final BalanceStorage storage;
    private final MessageService messages;

    public BalanceCommand(BalanceStorage storage, MessageService messages) {
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, "Use /balance <player> from console.");
                return true;
            }
            double balance = storage.getBalance(player.getUniqueId());
            String message = messages.getMessage("messages.balance-self")
                    .replace("{balance}", String.format("%.2f", balance));
            player.sendMessage(message);
            return true;
        }

        if (!sender.hasPermission("tbfmp.balance.others")) {
            sender.sendMessage(messages.getMessage("messages.no-permission"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(messages.getMessage("messages.player-not-found"));
            return true;
        }

        double balance = storage.getBalance(target.getUniqueId());
        String message = messages.getMessage("messages.balance-other")
                .replace("{player}", target.getName() == null ? args[0] : target.getName())
                .replace("{balance}", String.format("%.2f", balance));
        sender.sendMessage(message);
        return true;
    }
}
