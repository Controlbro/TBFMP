package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.staff.SocialSpyManager;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SocialSpyListener implements Listener {
    private static final String[] SPY_COMMANDS = {
            "msg",
            "tell",
            "w",
            "whisper",
            "pm",
            "m",
            "message"
    };
    private final SocialSpyManager socialSpyManager;
    private final MessageService messages;

    public SocialSpyListener(SocialSpyManager socialSpyManager, MessageService messages) {
        this.socialSpyManager = socialSpyManager;
        this.messages = messages;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String raw = event.getMessage();
        if (raw == null || raw.isBlank() || !raw.startsWith("/")) {
            return;
        }
        String[] parts = raw.substring(1).split(" ");
        if (parts.length < 3) {
            return;
        }
        String command = parts[0].toLowerCase();
        if (!isSpyCommand(command)) {
            return;
        }
        Player sender = event.getPlayer();
        String targetName = parts[1];
        String message = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length));
        String format = messages.getMessage("messages.socialspy-format")
                .replace("{sender}", sender.getName())
                .replace("{target}", targetName)
                .replace("{message}", message);
        String formatted = messages.colorize(format);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(sender)) {
                continue;
            }
            if (!player.hasPermission("oakglowutil.admin.socialspy")) {
                continue;
            }
            if (!socialSpyManager.isEnabled(player.getUniqueId())) {
                continue;
            }
            player.sendMessage(formatted);
        }
    }

    private boolean isSpyCommand(String command) {
        for (String candidate : SPY_COMMANDS) {
            if (candidate.equalsIgnoreCase(command)) {
                return true;
            }
        }
        return false;
    }
}
