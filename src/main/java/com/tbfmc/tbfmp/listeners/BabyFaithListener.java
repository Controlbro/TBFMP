package com.tbfmc.tbfmp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BabyFaithListener implements Listener {
    private static final String FAITH_NAME = "Faith";
    private final NamespacedKey faithKey;

    public BabyFaithListener(JavaPlugin plugin) {
        this.faithKey = new NamespacedKey(plugin, "faith-baby");
    }

    @EventHandler(ignoreCancelled = true)
    public void onNameTagUse(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (item == null || item.getType() != Material.NAME_TAG) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String rawName = ChatColor.stripColor(meta.getDisplayName());
        if (rawName == null || !rawName.equalsIgnoreCase(FAITH_NAME)) {
            return;
        }

        Entity entity = event.getRightClicked();
        if (!(entity instanceof Ageable ageable)) {
            return;
        }

        if (ageable.isAdult()) {
            return;
        }

        markAsFaithBaby(ageable);
    }

    private void markAsFaithBaby(Ageable ageable) {
        PersistentDataContainer container = ageable.getPersistentDataContainer();
        container.set(faithKey, PersistentDataType.BYTE, (byte) 1);
        ageable.setAgeLock(true);
        ageable.setBaby();
    }

    private boolean isFaithBaby(Ageable ageable) {
        PersistentDataContainer container = ageable.getPersistentDataContainer();
        return container.has(faithKey, PersistentDataType.BYTE);
    }
}
