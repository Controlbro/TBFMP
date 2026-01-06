package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.quests.QuestService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestMenuCommand implements CommandExecutor {
    private final QuestService questService;
    private final MessageService messages;

    public QuestMenuCommand(QuestService questService, MessageService messages) {
        this.questService = questService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }
        player.openInventory(questService.createMenu(player));
        return true;
    }
}
