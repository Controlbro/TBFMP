package com.tbfmc.tbfmp.quests;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class QuestMenuHolder implements InventoryHolder {
    private final String category;

    public QuestMenuHolder(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
