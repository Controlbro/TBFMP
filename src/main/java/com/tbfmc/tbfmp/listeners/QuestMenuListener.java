package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.quests.QuestMenuHolder;
import com.tbfmc.tbfmp.quests.QuestService;
import com.tbfmc.tbfmp.quests.QuestSummaryMenuHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;

public class QuestMenuListener implements Listener {
    private final Map<String, QuestService> questServices;

    public QuestMenuListener(Map<String, QuestService> questServices) {
        this.questServices = questServices;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof QuestSummaryMenuHolder) {
            event.setCancelled(true);
            return;
        }
        if (!(event.getInventory().getHolder() instanceof QuestMenuHolder holder)) {
            return;
        }
        event.setCancelled(true);
        QuestService service = questServices.get(holder.getCategory());
        if (service == null) {
            return;
        }
        if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
            service.handleMenuClick(player, event.getCurrentItem());
        }
    }
}
