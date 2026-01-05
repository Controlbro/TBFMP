package com.tbfmc.tbfmp.chat;

import com.tbfmc.tbfmp.util.MessageService;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TagMenuService {
    private final TagConfig tagConfig;
    private final TagMenuConfig menuConfig;
    private final TagSelectionStorage selectionStorage;
    private final MessageService messages;
    private final Chat chat;
    private final NamespacedKey tagKey;

    public TagMenuService(TagConfig tagConfig, TagMenuConfig menuConfig, TagSelectionStorage selectionStorage,
                          MessageService messages, Chat chat, NamespacedKey tagKey) {
        this.tagConfig = tagConfig;
        this.menuConfig = menuConfig;
        this.selectionStorage = selectionStorage;
        this.messages = messages;
        this.chat = chat;
        this.tagKey = tagKey;
    }

    public Inventory createMenu(Player player, String chatFormat) {
        int size = menuConfig.getSize();
        Inventory inventory = Bukkit.createInventory(player, size, messages.colorize(menuConfig.getTitle()));
        int slot = 0;
        for (TagDefinition tag : tagConfig.getTags()) {
            if (slot >= size) {
                break;
            }
            inventory.setItem(slot, buildItem(player, tag, chatFormat));
            slot++;
        }
        return inventory;
    }

    private ItemStack buildItem(Player player, TagDefinition tag, String chatFormat) {
        Material material = menuConfig.getItemMaterial();
        ItemStack item = new ItemStack(material != null ? material : Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(tag.getDisplay()));
            List<String> lore = new ArrayList<>();
            String selected = selectionStorage.getSelection(player.getUniqueId());
            boolean hasPermission = tag.getPermission().isEmpty() || player.hasPermission(tag.getPermission());

            String preview = formatPreview(player, chatFormat, tag.getDisplay());
            for (String line : menuConfig.getLore()) {
                lore.add(messages.colorize(line.replace("{preview}", preview)));
            }
            if (!hasPermission) {
                for (String line : menuConfig.getNoPermissionLore()) {
                    lore.add(messages.colorize(line));
                }
            } else if (tag.getId().equalsIgnoreCase(selected)) {
                for (String line : menuConfig.getSelectedLore()) {
                    lore.add(messages.colorize(line));
                }
            }

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(tagKey, PersistentDataType.STRING, tag.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    private String formatPreview(Player player, String chatFormat, String tagDisplay) {
        String prefix = chat != null ? chat.getPlayerPrefix(player) : "";
        String messageColor = chat != null ? chat.getPlayerSuffix(player) : "";
        String previewMessage = menuConfig.getPreviewMessage();
        return messages.colorize(chatFormat
                .replace("{prefix}", prefix)
                .replace("{name}", player.getName())
                .replace("%tag%", tagDisplay)
                .replace("{message-color}", messageColor)
                .replace("{message}", previewMessage));
    }
}
