package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.chat.TagConfig;
import com.tbfmc.tbfmp.chat.TagDefinition;
import com.tbfmc.tbfmp.chat.TagSelectionStorage;
import com.tbfmc.tbfmp.util.MessageService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFormatListener implements Listener {
    private final TagConfig tagConfig;
    private final TagSelectionStorage selectionStorage;
    private final MessageService messages;
    private final Chat chat;
    private final String formatTemplate;

    public ChatFormatListener(TagConfig tagConfig, TagSelectionStorage selectionStorage, MessageService messages,
                              Chat chat, String formatTemplate) {
        this.tagConfig = tagConfig;
        this.selectionStorage = selectionStorage;
        this.messages = messages;
        this.chat = chat;
        this.formatTemplate = formatTemplate;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String tagDisplay = getTagDisplay(player);
        String prefix = chat != null ? chat.getPlayerPrefix(player) : "";
        String messageColor = chat != null ? chat.getPlayerSuffix(player) : "";
        String format = formatTemplate
                .replace("{prefix}", prefix)
                .replace("{name}", player.getName())
                .replace("%tag%", tagDisplay)
                .replace("{message-color}", messageColor)
                .replace("{message}", "%2$s");
        event.setFormat(messages.colorize(format));
    }

    private String getTagDisplay(Player player) {
        String selected = selectionStorage.getSelection(player.getUniqueId());
        if (selected.isEmpty()) {
            return "";
        }
        TagDefinition tag = tagConfig.getTag(selected);
        if (tag == null) {
            return "";
        }
        if (!tag.getPermission().isEmpty() && !player.hasPermission(tag.getPermission())) {
            return "";
        }
        return tag.getDisplay();
    }
}
