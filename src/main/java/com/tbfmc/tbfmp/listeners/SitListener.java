package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.sit.SitManager;
import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class SitListener implements Listener {
    private final SitSettingsStorage settingsStorage;
    private final SitManager sitManager;

    public SitListener(SitSettingsStorage settingsStorage, SitManager sitManager) {
        this.settingsStorage = settingsStorage;
        this.sitManager = sitManager;
    }

    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (!settingsStorage.isChairEnabled(player.getUniqueId())) {
            return;
        }
        if (!sitManager.canSit(player)) {
            return;
        }
        if (player.isInsideVehicle()) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        BlockData data = block.getBlockData();
        if (!(data instanceof Slab) && !(data instanceof Stairs)) {
            return;
        }
        event.setCancelled(true);
        sitManager.sitOnBlock(player, block, data);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof Player target)) {
            return;
        }
        Player player = event.getPlayer();
        if (!settingsStorage.isPlayerEnabled(player.getUniqueId())) {
            return;
        }
        if (!sitManager.canSit(player)) {
            return;
        }
        if (player.isInsideVehicle()) {
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        target.addPassenger(player);
        sitManager.hideSitterFromTarget(player, target);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isInsideVehicle()) {
            return;
        }
        Entity vehicle = player.getVehicle();
        player.leaveVehicle();
        sitManager.showSitterToTarget(player.getUniqueId());
        if (vehicle instanceof ArmorStand stand) {
            sitManager.removeSeat(player.getUniqueId(), stand);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        sitManager.showSitterToTarget(uuid);
        sitManager.removeSeat(uuid);
    }
}
