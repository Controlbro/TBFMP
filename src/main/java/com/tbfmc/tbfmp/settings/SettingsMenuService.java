package com.tbfmc.tbfmp.settings;

import com.tbfmc.tbfmp.chat.ChatNotificationSettingsStorage;
import com.tbfmc.tbfmp.economy.PaySettingsStorage;
import com.tbfmc.tbfmp.event.EventSettingsStorage;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
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

public class SettingsMenuService {
    private final SettingsMenuConfig config;
    private final PaySettingsStorage paySettingsStorage;
    private final SitSettingsStorage sitSettingsStorage;
    private final ChatNotificationSettingsStorage chatNotificationSettingsStorage;
    private final KeepInventorySettingsStorage keepInventorySettingsStorage;
    private final PvpSettingsStorage pvpSettingsStorage;
    private final EventSettingsStorage eventSettingsStorage;
    private final MessageService messages;
    private final NamespacedKey settingKey;

    public SettingsMenuService(SettingsMenuConfig config, PaySettingsStorage paySettingsStorage,
                               SitSettingsStorage sitSettingsStorage,
                               ChatNotificationSettingsStorage chatNotificationSettingsStorage,
                               KeepInventorySettingsStorage keepInventorySettingsStorage,
                               PvpSettingsStorage pvpSettingsStorage,
                               EventSettingsStorage eventSettingsStorage,
                               MessageService messages, NamespacedKey settingKey) {
        this.config = config;
        this.paySettingsStorage = paySettingsStorage;
        this.sitSettingsStorage = sitSettingsStorage;
        this.chatNotificationSettingsStorage = chatNotificationSettingsStorage;
        this.keepInventorySettingsStorage = keepInventorySettingsStorage;
        this.pvpSettingsStorage = pvpSettingsStorage;
        this.eventSettingsStorage = eventSettingsStorage;
        this.messages = messages;
        this.settingKey = settingKey;
    }

    public Inventory createMenu(Player player) {
        int size = config.getSize();
        Inventory inventory = Bukkit.createInventory(new SettingsMenuHolder(), size,
                messages.colorize(config.getTitle()));
        fillFiller(inventory);
        for (SettingsOption option : SettingsOption.values()) {
            SettingsMenuItem itemDefinition = config.getItem(option.getKey());
            if (itemDefinition == null) {
                continue;
            }
            int slot = itemDefinition.getSlot();
            if (slot < 0 || slot >= size) {
                continue;
            }
            boolean enabled = isOptionEnabled(option, player);
            inventory.setItem(slot, buildItem(itemDefinition, option, enabled));
        }
        return inventory;
    }

    private void fillFiller(Inventory inventory) {
        Material fillerMaterial = config.getFillerMaterial();
        if (fillerMaterial == null || fillerMaterial == Material.AIR) {
            return;
        }
        ItemStack filler = new ItemStack(fillerMaterial);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(messages.colorize(config.getFillerName()));
            filler.setItemMeta(meta);
        }
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }

    private ItemStack buildItem(SettingsMenuItem itemDefinition, SettingsOption option, boolean enabled) {
        ItemStack item = new ItemStack(itemDefinition.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String status = enabled ? config.getStatusEnabled() : config.getStatusDisabled();
            meta.setDisplayName(messages.colorize(replaceStatus(itemDefinition.getName(), status)));
            List<String> lore = new ArrayList<>();
            for (String line : itemDefinition.getLore()) {
                lore.add(messages.colorize(replaceStatus(line, status)));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(settingKey, PersistentDataType.STRING, option.getKey());
            item.setItemMeta(meta);
        }
        return item;
    }

    private String replaceStatus(String value, String status) {
        return value.replace("{status}", status);
    }

    private boolean isOptionEnabled(SettingsOption option, Player player) {
        return switch (option) {
            case PAY -> paySettingsStorage.isPayEnabled(player.getUniqueId());
            case SIT_CHAIR -> sitSettingsStorage.isChairEnabled(player.getUniqueId());
            case SIT_PLAYER -> sitSettingsStorage.isPlayerEnabled(player.getUniqueId());
            case AUTO_MESSAGES -> chatNotificationSettingsStorage.isEnabled(player.getUniqueId());
            case KEEP_INVENTORY -> keepInventorySettingsStorage.isEnabled(player.getUniqueId());
            case PVP -> pvpSettingsStorage.isEnabled(player.getUniqueId());
            case EVENT_LEADERBOARD -> eventSettingsStorage.isEnabled(player.getUniqueId());
        };
    }
}
