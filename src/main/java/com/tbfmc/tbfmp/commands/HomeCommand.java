package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.teleport.HomeManager;
import com.tbfmc.tbfmp.util.MessageService;
import com.tbfmc.tbfmp.util.TeleportSound;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeCommand implements CommandExecutor {
    private final HomeManager homeManager;
    private final MessageService messages;

    public HomeCommand(HomeManager homeManager, MessageService messages) {
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
        if (args.length == 0) {
            if (homeNames.isEmpty()) {
                messages.sendMessage(player, messages.getMessage("messages.home-none"));
                return true;
            }
            if (homeNames.size() > 1) {
                messages.sendMessage(player, messages.getMessage("messages.home-list")
                        .replace("{homes}", String.join(", ", homeNames)));
                return true;
            }
            return teleportHome(player, homeNames.get(0));
        }
        return teleportHome(player, args[0]);
    }

    private boolean teleportHome(Player player, String name) {
        Location location = homeManager.getHome(player.getUniqueId(), name);
        if (location == null) {
            messages.sendMessage(player, messages.getMessage("messages.home-not-found")
                    .replace("{home}", name));
            return true;
        }
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                TeleportSound.play(player);
            }
        });
        messages.sendMessage(player, messages.getMessage("messages.home-teleport")
                .replace("{home}", name));
        return true;
    }
}
