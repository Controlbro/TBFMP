package com.tbfmc.tbfmp.commands;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HugCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MessageService messages;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HugCommand(JavaPlugin plugin, MessageService messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.sendMessage(sender, "This command can only be used in-game.");
            return true;
        }

        if (!plugin.getConfig().getBoolean("hug.enabled", true)) {
            messages.sendMessage(player, messages.getMessage("messages.hug-disabled"));
            return true;
        }

        if (args.length < 1) {
            messages.sendMessage(player, messages.getMessage("messages.hug-usage"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            messages.sendMessage(player, messages.getMessage("messages.player-not-found"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            messages.sendMessage(player, messages.getMessage("messages.hug-self"));
            return true;
        }

        long cooldownSeconds = plugin.getConfig().getLong("hug.cooldown", 15);
        long now = System.currentTimeMillis();
        Long lastUsed = cooldowns.get(player.getUniqueId());
        if (lastUsed != null) {
            long elapsed = Duration.ofMillis(now - lastUsed).toSeconds();
            if (elapsed < cooldownSeconds) {
                long remaining = cooldownSeconds - elapsed;
                messages.sendMessage(player, messages.getMessage("messages.hug-cooldown")
                        .replace("{seconds}", String.valueOf(remaining)));
                return true;
            }
        }

        int heartCount = plugin.getConfig().getInt("hug.heart_count", 15);
        if (heartCount < 1) {
            heartCount = 1;
        }
        String hearts = "❤".repeat(heartCount);

        cooldowns.put(player.getUniqueId(), now);

        messages.sendMessage(player, messages.getMessage("messages.hug-sent")
                .replace("{player}", target.getName())
                .replace("{hearts}", messages.colorize(hearts)));
        messages.sendMessage(target, messages.getMessage("messages.hug-received")
                .replace("{player}", player.getName())
                .replace("{hearts}", messages.colorize(hearts)));
        return true;
    }
}
