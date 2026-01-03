package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    private final MessageService messages;

    public FlyCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        if (!player.hasPermission("tbfmp.fly")) {
            messages.sendMessage(player, messages.getMessage("messages.no-permission"));
            return true;
        }

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            messages.sendMessage(player, messages.getMessage("messages.fly-creative"));
            return true;
        }

        boolean enable = !player.getAllowFlight();
        player.setAllowFlight(enable);
        player.setFlying(enable);

        messages.sendMessage(player, messages.getMessage(enable ? "messages.fly-enabled" : "messages.fly-disabled"));
        return true;
    }
}
