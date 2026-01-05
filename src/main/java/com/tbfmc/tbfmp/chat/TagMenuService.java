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
import java.util.stream.Collectors;

public class TagMenuService {
    private final TagConfig tagConfig;
    private final TagMenuConfig menuConfig;
    private final TagSelectionStorage selectionStorage;
    private final MessageService messages;
    private final Chat chat;
    private final NamespacedKey tagKey;
    private final NamespacedKey navigationKey;

    public TagMenuService(TagConfig tagConfig, TagMenuConfig menuConfig, TagSelectionStorage selectionStorage,
                          MessageService messages, Chat chat, NamespacedKey tagKey, NamespacedKey navigationKey) {
        this.tagConfig = tagConfig;
        this.menuConfig = menuConfig;
        this.selectionStorage = selectionStorage;
        this.messages = messages;
        this.chat = chat;
        this.tagKey = tagKey;
        this.navigationKey = navigationKey;
    }

    public Inventory createMenu(Player player, String chatFormat) {
        return createMenu(player, chatFormat, 0);
    }

    public Inventory createMenu(Player player, String chatFormat, int page) {
        int size = menuConfig.getSize();
        Inventory inventory = Bukkit.createInventory(player, size, messages.colorize(menuConfig.getTitle()));
        List<TagDefinition> availableTags = tagConfig.getTags().stream()
                .filter(tag -> tag.getPermission().isEmpty() || player.hasPermission(tag.getPermission()))
                .collect(Collectors.toList());

        int previousSlot = menuConfig.getPreviousPageSlot(size);
        int nextSlot = menuConfig.getNextPageSlot(size);
        List<Integer> itemSlots = buildItemSlots(size, availableTags, previousSlot, nextSlot);
        int tagsPerPage = itemSlots.size();
        int totalPages = Math.max(1, (int) Math.ceil(availableTags.size() / (double) tagsPerPage));
        int currentPage = Math.max(0, Math.min(page, totalPages - 1));

        int startIndex = currentPage * tagsPerPage;
        int endIndex = Math.min(startIndex + tagsPerPage, availableTags.size());
        for (int index = startIndex; index < endIndex; index++) {
            int slot = itemSlots.get(index - startIndex);
            inventory.setItem(slot, buildItem(player, availableTags.get(index), chatFormat));
        }

        if (totalPages > 1) {
            if (currentPage > 0) {
                inventory.setItem(previousSlot, buildNavigationItem(menuConfig.getPreviousPageName(), currentPage - 1));
            }
            if (currentPage < totalPages - 1) {
                inventory.setItem(nextSlot, buildNavigationItem(menuConfig.getNextPageName(), currentPage + 1));
            }
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
            String preview = formatPreview(player, chatFormat, tag.getDisplay());
            for (String line : menuConfig.getLore()) {
                lore.add(messages.colorize(line.replace("{preview}", preview)));
            }
            if (tag.getId().equalsIgnoreCase(selected)) {
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
        String messageColor = resolveMessageColor(player);
        String previewMessage = menuConfig.getPreviewMessage();
        return messages.colorize(chatFormat
                .replace("{prefix}", prefix)
                .replace("{name}", player.getName())
                .replace("%tag%", tagDisplay)
                .replace("{message-color}", messageColor)
                .replace("{message}", previewMessage));
    }

    private String resolveMessageColor(Player player) {
        if (chat == null) {
            return "";
        }
        String metaColor = chat.getPlayerInfoString(player, "message-color", "");
        if (metaColor == null || metaColor.isEmpty()) {
            return chat.getPlayerSuffix(player);
        }
        return metaColor;
    }

    private List<Integer> buildItemSlots(int size, List<TagDefinition> availableTags, int previousSlot, int nextSlot) {
        List<Integer> slots = new ArrayList<>();
        boolean includeNavigation = availableTags.size() > size - 2;
        for (int slot = 0; slot < size; slot++) {
            if (includeNavigation && (slot == previousSlot || slot == nextSlot)) {
                continue;
            }
            slots.add(slot);
        }
        if (slots.isEmpty()) {
            slots.add(0);
        }
        return slots;
    }

    private ItemStack buildNavigationItem(String name, int targetPage) {
        ItemStack item = new ItemStack(menuConfig.getNavigationMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(name));
            meta.getPersistentDataContainer().set(navigationKey, PersistentDataType.INTEGER, targetPage);
            item.setItemMeta(meta);
        }
        return item;
    }
}
