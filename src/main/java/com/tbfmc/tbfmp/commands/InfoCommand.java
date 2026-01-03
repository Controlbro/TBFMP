package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InfoCommand implements CommandExecutor {
    private final MessageService messages;
    private final String messageKey;

    public InfoCommand(MessageService messages, String messageKey) {
        this.messages = messages;
        this.messageKey = messageKey;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        messages.sendMessage(sender, messages.getMessage(messageKey));
        return true;
    }
}
