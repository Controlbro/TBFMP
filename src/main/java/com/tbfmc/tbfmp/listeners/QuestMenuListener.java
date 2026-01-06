package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.quests.QuestMenuHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class QuestMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof QuestMenuHolder)) {
            return;
        }
        event.setCancelled(true);
    }
}
