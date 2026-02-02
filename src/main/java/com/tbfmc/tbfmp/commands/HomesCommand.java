package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.HomeManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomesCommand implements CommandExecutor {
    private final HomeManager homeManager;
    private final MessageService messages;

    public HomesCommand(HomeManager homeManager, MessageService messages) {
        this.homeManager = homeManager;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        List<String> homeNames = new ArrayList<>(homeManager.getHomeNames(player.getUniqueId()));
        Collections.sort(homeNames);
        if (homeNames.isEmpty()) {
            messages.sendMessage(player, messages.getMessage("messages.home-none"));
            return true;
        }
        messages.sendMessage(player, messages.getMessage("messages.home-list")
                .replace("{homes}", String.join(", ", homeNames)));
        return true;
    }
}
