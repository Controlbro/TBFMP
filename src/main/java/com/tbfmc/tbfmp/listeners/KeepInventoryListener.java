package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.settings.KeepInventorySettingsStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepInventoryListener implements Listener {
    private final KeepInventorySettingsStorage keepInventorySettingsStorage;

    public KeepInventoryListener(KeepInventorySettingsStorage keepInventorySettingsStorage) {
        this.keepInventorySettingsStorage = keepInventorySettingsStorage;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!keepInventorySettingsStorage.isEnabled(player.getUniqueId())) {
            return;
        }
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
