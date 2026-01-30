package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {
    private final BalanceStorage balanceStorage;
    private final PaySettingsStorage paySettingsStorage;
    private final MessageService messages;

    public PayCommand(BalanceStorage balanceStorage, PaySettingsStorage paySettingsStorage, MessageService messages) {
        this.balanceStorage = balanceStorage;
        this.paySettingsStorage = paySettingsStorage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        if (args.length < 2) {
            messages.sendMessage(player, messages.getMessage("messages.pay-usage"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.isOnline() && !target.hasPlayedBefore()) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            messages.sendMessage(player, messages.getMessage("messages.pay-self"));
            return true;
        }

        if (!paySettingsStorage.isPayEnabled(target.getUniqueId())) {
            messages.sendMessage(player, messages.getMessage("messages.pay-target-disabled"));
            return true;
        }

        Double amount = parseAmount(args[1]);
        if (amount == null || amount <= 0) {
            messages.sendMessage(player, messages.getMessage("messages.invalid-amount"));
            return true;
        }

        UUID senderId = player.getUniqueId();
        if (balanceStorage.getBalance(senderId) < amount) {
            messages.sendMessage(player, messages.getMessage("messages.insufficient-funds"));
            return true;
        }

        balanceStorage.subtractBalance(senderId, amount);
        balanceStorage.addBalance(target.getUniqueId(), amount);

        String targetName = target.getName() == null ? "player" : target.getName();
        messages.sendMessage(player, messages.getMessage("messages.pay-sent")
                .replace("{player}", targetName)
                .replace("{amount}", String.format("%.2f", amount)));
        if (target.isOnline() && target.getPlayer() != null) {
            messages.sendMessage(target.getPlayer(), messages.getMessage("messages.pay-received")
                    .replace("{player}", player.getName())
                    .replace("{amount}", String.format("%.2f", amount)));
        }
        return true;
    }

    private Double parseAmount(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
