package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.economy.BalanceStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BalanceTopCommand implements CommandExecutor {
    private final BalanceStorage storage;
    private final MessageService messages;

    public BalanceTopCommand(BalanceStorage storage, MessageService messages) {
        this.storage = storage;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(messages.getMessage("messages.baltop-title"));

        List<Map.Entry<UUID, Double>> topBalances = storage.getAllBalances().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());

        if (topBalances.isEmpty()) {
            sender.sendMessage(messages.getMessage("messages.baltop-empty"));
            return true;
        }

        int rank = 1;
        for (Map.Entry<UUID, Double> entry : topBalances) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            String name = player.getName() == null ? entry.getKey().toString() : player.getName();
            sender.sendMessage(messages.formatMessage("&a" + rank + ". &f" + name + " &a- &f" + String.format("%.2f", entry.getValue())));
            rank++;
        }
        return true;
    }
}
