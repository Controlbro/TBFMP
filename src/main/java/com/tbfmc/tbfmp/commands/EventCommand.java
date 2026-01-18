package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.event.MiningEventService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class EventCommand implements CommandExecutor {
    private final MiningEventService eventService;
    private final MessageService messages;

    public EventCommand(MiningEventService eventService, MessageService messages) {
        this.eventService = eventService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (args.length == 0) {
            messages.sendMessage(player, messages.getMessage("messages.event-usage"));
            return true;
        }
        if (!eventService.isEventEnabled()) {
            messages.sendMessage(player, messages.getMessage("messages.event-disabled"));
            return true;
        }
        String action = args[0].toLowerCase(Locale.ROOT);
        switch (action) {
            case "show" -> {
                eventService.showScoreboard(player);
                messages.sendMessage(player, messages.getMessage("messages.event-show"));
            }
            case "hide" -> {
                eventService.hideScoreboard(player);
                messages.sendMessage(player, messages.getMessage("messages.event-hide"));
            }
            default -> messages.sendMessage(player, messages.getMessage("messages.event-usage"));
        }
        return true;
    }
}
