package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.chat.TagConfig;
import com.tbfmc.tbfmp.chat.TagDefinition;
import com.tbfmc.tbfmp.chat.TagMenuService;
import com.tbfmc.tbfmp.chat.TagSelectionStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TagMenuListener implements Listener {
    private final TagConfig tagConfig;
    private final TagSelectionStorage selectionStorage;
    private final TagMenuService menuService;
    private final MessageService messages;
    private final NamespacedKey tagKey;
    private final NamespacedKey navigationKey;
    private final String menuTitle;
    private final String chatFormat;

    public TagMenuListener(TagConfig tagConfig, TagSelectionStorage selectionStorage, TagMenuService menuService,
                           MessageService messages, NamespacedKey tagKey, NamespacedKey navigationKey,
                           String menuTitle, String chatFormat) {
        this.tagConfig = tagConfig;
        this.selectionStorage = selectionStorage;
        this.menuService = menuService;
        this.messages = messages;
        this.tagKey = tagKey;
        this.navigationKey = navigationKey;
        this.menuTitle = menuTitle;
        this.chatFormat = chatFormat;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!event.getView().getTitle().equals(menuTitle)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) {
            return;
        }
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }
        Integer targetPage = meta.getPersistentDataContainer().get(navigationKey, PersistentDataType.INTEGER);
        if (targetPage != null) {
            player.openInventory(menuService.createMenu(player, chatFormat, targetPage));
            return;
        }
        String tagId = meta.getPersistentDataContainer().get(tagKey, PersistentDataType.STRING);
        if (tagId == null || tagId.isEmpty()) {
            return;
        }
        TagDefinition tag = tagConfig.getTag(tagId);
        if (tag == null) {
            return;
        }
        if (!tag.getPermission().isEmpty() && !player.hasPermission(tag.getPermission())) {
            messages.sendMessage(player, messages.getMessage("messages.tag-no-permission"));
            return;
        }
        selectionStorage.setSelection(player.getUniqueId(), tagId);
        messages.sendMessage(player, messages.getMessage("messages.tag-selected")
                .replace("{tag}", tag.getDisplay()));
        player.closeInventory();
    }
}
