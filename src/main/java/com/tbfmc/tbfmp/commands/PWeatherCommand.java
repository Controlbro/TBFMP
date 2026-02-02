package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PWeatherCommand implements CommandExecutor {
    private final MessageService messages;

    public PWeatherCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, messages.getMessage("messages.players-only"));
            return true;
        }
        if (!sender.hasPermission("oakglowutil.pweather")) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }
        if (args.length != 1) {
            messages.sendMessage(sender, messages.getMessage("messages.pweather-usage"));
            return true;
        }
        String mode = args[0].toLowerCase();
        switch (mode) {
            case "sun" -> player.setPlayerWeather(WeatherType.CLEAR);
            case "rain", "thunder" -> player.setPlayerWeather(WeatherType.DOWNFALL);
            default -> {
                messages.sendMessage(sender, messages.getMessage("messages.pweather-usage"));
                return true;
            }
        }
        String message = messages.getMessage("messages.pweather-updated").replace("{weather}", mode);
        messages.sendMessage(player, message);
        return true;
    }
}
