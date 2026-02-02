package com.tbfmc.tbfmp.playtime;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PlaytimeRewardsListener implements Listener {
    private final PlaytimeRewardsService rewardsService;

    public PlaytimeRewardsListener(PlaytimeRewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (inventory == null) {
            return;
        }
        if (!(inventory.getHolder() instanceof PlaytimeRewardsMenuHolder holder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        PlaytimeReward reward = holder.getRewardSlots().get(event.getSlot());
        if (reward == null) {
            return;
        }
        rewardsService.handleClick(player, reward);
    }
}
