package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.util.OfflineInventoryHolder;
import com.tbfmc.tbfmp.util.OfflineInventoryStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OfflineInventoryListener implements Listener {
    private static final int INVENTORY_SIZE = 41;
    private final OfflineInventoryStorage storage;

    public OfflineInventoryListener(OfflineInventoryStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof OfflineInventoryHolder holder)) {
            return;
        }
        ItemStack[] contents = inventory.getContents();
        if (holder.isEnderChest()) {
            ItemStack[] saved = new ItemStack[27];
            System.arraycopy(contents, 0, saved, 0, Math.min(contents.length, saved.length));
            storage.setEnderChest(holder.getTargetId(), saved);
        } else {
            ItemStack[] saved = new ItemStack[INVENTORY_SIZE];
            System.arraycopy(contents, 0, saved, 0, Math.min(contents.length, saved.length));
            storage.setInventory(holder.getTargetId(), saved);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        storage.setInventory(player.getUniqueId(), player.getInventory().getContents());
        storage.setEnderChest(player.getUniqueId(), player.getEnderChest().getContents());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (storage.hasInventory(player.getUniqueId())) {
            ItemStack[] stored = storage.getInventory(player.getUniqueId(), player.getInventory().getContents().length);
            player.getInventory().setContents(stored);
        }
        if (storage.hasEnderChest(player.getUniqueId())) {
            ItemStack[] stored = storage.getEnderChest(player.getUniqueId(), player.getEnderChest().getContents().length);
            player.getEnderChest().setContents(stored);
        }
        storage.save();
    }
}
