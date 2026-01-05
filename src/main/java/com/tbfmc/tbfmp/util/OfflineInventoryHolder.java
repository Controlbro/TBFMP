package com.tbfmc.tbfmp.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class OfflineInventoryHolder implements InventoryHolder {
    private final UUID targetId;
    private Inventory inventory;
    private final boolean enderChest;

    public OfflineInventoryHolder(UUID targetId, boolean enderChest) {
        this.targetId = targetId;
        this.enderChest = enderChest;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public boolean isEnderChest() {
        return enderChest;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
