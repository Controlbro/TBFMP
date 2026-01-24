package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class GamemodeCommand implements CommandExecutor {
    private final MessageService messages;

    public GamemodeCommand(MessageService messages) {
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            messages.sendMessage(sender, messages.getMessage("messages.gamemode-usage"));
            return true;
        }

        GameMode mode = parseMode(args[0]);
        if (mode == null) {
            messages.sendMessage(sender, messages.getMessage("messages.gamemode-invalid"));
            return true;
        }

        String modePermission = permissionFor(mode);
        if (modePermission != null && !sender.hasPermission(modePermission)) {
            messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
            return true;
        }

        Player target = null;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                messages.sendMessage(sender, messages.getMessage("messages.player-not-found"));
                return true;
            }
        }

        if (target == null) {
            if (!(sender instanceof Player player)) {
                messages.sendMessage(sender, messages.getMessage("messages.players-only"));
                return true;
            }
            target = player;
        } else if (sender instanceof Player player && !target.equals(player)) {
            if (!player.hasPermission("tbfmp.gamemode.others")) {
                messages.sendMessage(sender, messages.getMessage("messages.no-permission"));
                return true;
            }
        }

        target.setGameMode(mode);
        String modeName = formatMode(mode);
        if (sender instanceof Player player && target.equals(player)) {
            messages.sendMessage(target, messages.getMessage("messages.gamemode-self")
                    .replace("{mode}", modeName));
        } else {
            messages.sendMessage(sender, messages.getMessage("messages.gamemode-other")
                    .replace("{player}", target.getName())
                    .replace("{mode}", modeName));
            messages.sendMessage(target, messages.getMessage("messages.gamemode-changed")
                    .replace("{mode}", modeName));
        }
        return true;
    }

    private GameMode parseMode(String input) {
        String value = input.toLowerCase(Locale.ROOT);
        return switch (value) {
            case "survival", "s" -> GameMode.SURVIVAL;
            case "creative", "c" -> GameMode.CREATIVE;
            case "spectator", "sp" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private String permissionFor(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "tbfmp.gamemode.survival";
            case CREATIVE -> "tbfmp.gamemode.creative";
            case SPECTATOR -> "tbfmp.gamemode.spectator";
            default -> null;
        };
    }

    private String formatMode(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "Survival";
            case CREATIVE -> "Creative";
            case SPECTATOR -> "Spectator";
            default -> mode.name();
        };
    }
}
