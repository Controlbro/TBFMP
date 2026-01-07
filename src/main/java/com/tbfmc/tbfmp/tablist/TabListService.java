package com.tbfmc.tbfmp.tablist;

import com.tbfmc.tbfmp.util.MessageService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TabListService {
    private final JavaPlugin plugin;
    private MessageService messages;
    private Chat chat;

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
        String listName = messages.colorize(prefix + player.getName() + afkSuffix);
        player.setPlayerListName(listName);
        updateHeaderFooter(player);
    }

    public void updateAll(Function<UUID, Boolean> afkLookup) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayer(player, afkLookup.apply(player.getUniqueId()));
        }
    }

    private void updateHeaderFooter(Player player) {
        String header = joinLines("tab-list.header");
        String footer = joinLines("tab-list.footer");
        player.setPlayerListHeaderFooter(header, footer);
    }

    private String joinLines(String path) {
        List<String> lines = plugin.getConfig().getStringList(path);
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        return lines.stream()
                .map(messages::colorize)
                .collect(Collectors.joining("\n"));
    }
}
