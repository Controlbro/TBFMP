package com.tbfmc.tbfmp.tablist;

import com.tbfmc.tbfmp.util.MessageService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TabListService {
    private final JavaPlugin plugin;
    private MessageService messages;
    private Chat chat;
    private final Map<UUID, Instant> sessionStarts = new ConcurrentHashMap<>();

    public TabListService(JavaPlugin plugin, MessageService messages, Chat chat) {
        this.plugin = plugin;
        this.messages = messages;
        this.chat = chat;
    }

    public void updateServices(MessageService messages, Chat chat) {
        this.messages = messages;
        this.chat = chat;
    }

    public void updatePlayer(Player player, boolean afk) {
        String prefix = chat != null ? chat.getPlayerPrefix(player) : "";
        String afkSuffix = afk ? " &7[afk]" : "";
        String displayName = player.getDisplayName();
        if (displayName == null || displayName.isBlank()) {
            displayName = player.getName();
        }
        String listName = messages.colorize(prefix + displayName + afkSuffix);
        player.setPlayerListName(listName);
        updateHeaderFooter(player);
    }

    public void updateAll(Function<UUID, Boolean> afkLookup) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player, afkLookup.apply(player.getUniqueId()));
        }
    }

    public void recordSessionStart(Player player) {
        sessionStarts.put(player.getUniqueId(), Instant.now());
    }

    public void clearSession(Player player) {
        sessionStarts.remove(player.getUniqueId());
    }

    private void updateHeaderFooter(Player player) {
        String header = joinLines(player, "tab-list.header");
        String footer = joinLines(player, "tab-list.footer");
        player.setPlayerListHeaderFooter(header, footer);
    }

    private String joinLines(Player player, String path) {
        List<String> lines = plugin.getConfig().getStringList(path);
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        return lines.stream()
                .map(line -> replacePlaceholders(player, line))
                .map(messages::colorize)
                .collect(Collectors.joining("\n"));
    }

    private String replacePlaceholders(Player player, String line) {
        if (player == null || line == null) {
            return line;
        }
        return line.replace("%sessiontime%", formatDuration(getSessionDuration(player)));
    }

    private Duration getSessionDuration(Player player) {
        Instant start = sessionStarts.computeIfAbsent(player.getUniqueId(), key -> Instant.now());
        return Duration.between(start, Instant.now());
    }

    private String formatDuration(Duration duration) {
        long seconds = Math.max(0L, duration.getSeconds());
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            builder.append(minutes).append("m ");
        }
        builder.append(remainingSeconds).append("s");
        return builder.toString().trim();
    }
}
