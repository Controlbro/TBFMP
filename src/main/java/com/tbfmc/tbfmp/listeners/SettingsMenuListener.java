package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.chat.ChatNotificationSettingsStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.settings.SettingsMenuHolder;
import com.tbfmc.tbfmp.settings.SettingsMenuService;
import com.tbfmc.tbfmp.settings.SettingsOption;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SettingsMenuListener implements Listener {
    private final SettingsMenuService menuService;
    private final PaySettingsStorage paySettingsStorage;
    private final SitSettingsStorage sitSettingsStorage;
    private final ChatNotificationSettingsStorage chatNotificationSettingsStorage;
    private final MessageService messages;
    private final NamespacedKey settingKey;

    public SettingsMenuListener(SettingsMenuService menuService, PaySettingsStorage paySettingsStorage,
                                SitSettingsStorage sitSettingsStorage,
                                ChatNotificationSettingsStorage chatNotificationSettingsStorage,
                                MessageService messages, NamespacedKey settingKey) {
        this.menuService = menuService;
        this.paySettingsStorage = paySettingsStorage;
        this.sitSettingsStorage = sitSettingsStorage;
        this.chatNotificationSettingsStorage = chatNotificationSettingsStorage;
        this.messages = messages;
        this.settingKey = settingKey;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof SettingsMenuHolder)) {
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
        String optionKey = meta.getPersistentDataContainer().get(settingKey, PersistentDataType.STRING);
        if (optionKey == null || optionKey.isEmpty()) {
            return;
        }
        SettingsOption option = null;
        for (SettingsOption candidate : SettingsOption.values()) {
            if (candidate.getKey().equalsIgnoreCase(optionKey)) {
                option = candidate;
                break;
            }
        }
        if (option == null) {
            return;
        }
        switch (option) {
            case PAY -> {
                boolean enabled = paySettingsStorage.togglePay(player.getUniqueId());
                messages.sendMessage(player, messages.getMessage(
                        enabled ? "messages.pay-toggle-on" : "messages.pay-toggle-off"));
            }
            case SIT_CHAIR -> {
                boolean enabled = !sitSettingsStorage.isChairEnabled(player.getUniqueId());
                sitSettingsStorage.setChairEnabled(player.getUniqueId(), enabled);
                messages.sendMessage(player, messages.getMessage(
                        enabled ? "messages.sit-chair-enabled" : "messages.sit-chair-disabled"));
            }
            case SIT_PLAYER -> {
                boolean enabled = !sitSettingsStorage.isPlayerEnabled(player.getUniqueId());
                sitSettingsStorage.setPlayerEnabled(player.getUniqueId(), enabled);
                messages.sendMessage(player, messages.getMessage(
                        enabled ? "messages.sit-player-enabled" : "messages.sit-player-disabled"));
            }
            case AUTO_MESSAGES -> {
                boolean enabled = chatNotificationSettingsStorage.toggle(player.getUniqueId());
                messages.sendMessage(player, messages.getMessage(
                        enabled ? "messages.auto-messages-enabled" : "messages.auto-messages-disabled"));
            }
        }
        player.openInventory(menuService.createMenu(player));
    }
}
