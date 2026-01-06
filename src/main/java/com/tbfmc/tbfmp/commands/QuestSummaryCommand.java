package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.quests.QuestSummaryService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestSummaryCommand implements CommandExecutor {
    private final QuestSummaryService summaryService;
    private final MessageService messages;

    public QuestSummaryCommand(QuestSummaryService summaryService, MessageService messages) {
        this.summaryService = summaryService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "&cOnly players can use this command.");
            return true;
        }
        player.openInventory(summaryService.createMenu(player));
        return true;
    }
}
