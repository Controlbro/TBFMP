package com.tbfmc.tbfmp.playtime;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collections;
import java.util.Map;

public class PlaytimeRewardsMenuHolder implements InventoryHolder {
    private final Map<Integer, PlaytimeReward> rewardSlots;

    public PlaytimeRewardsMenuHolder(Map<Integer, PlaytimeReward> rewardSlots) {
        this.rewardSlots = rewardSlots;
    }

    public Map<Integer, PlaytimeReward> getRewardSlots() {
        return Collections.unmodifiableMap(rewardSlots);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
