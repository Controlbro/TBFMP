package com.tbfmc.tbfmp.listeners;

import com.tbfmc.tbfmp.sit.SitSettingsStorage;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitListener implements Listener {
    private final SitSettingsStorage settingsStorage;
    private final MessageService messages;
    private final Map<UUID, ArmorStand> seats = new HashMap<>();

    public SitListener(SitSettingsStorage settingsStorage, MessageService messages) {
        this.settingsStorage = settingsStorage;
        this.messages = messages;
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
        if (!canSit(player)) {
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

        Location seatLocation = getSeatLocation(block.getLocation(), data);
        if (seatLocation == null) {
            return;
        }
        event.setCancelled(true);
        ArmorStand stand = spawnSeat(seatLocation);
        seats.put(player.getUniqueId(), stand);
        stand.addPassenger(player);
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
        if (!canSit(player)) {
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
    }

    private boolean canSit(Player player) {
        if (player.isSneaking()) {
            return false;
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (mainHand != null && mainHand.getType() != Material.AIR) {
            return false;
        }
        return offHand == null || offHand.getType() == Material.AIR;
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
        if (vehicle instanceof ArmorStand stand) {
            seats.remove(player.getUniqueId());
            stand.remove();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        ArmorStand stand = seats.remove(uuid);
        if (stand != null) {
            stand.remove();
        }
    }

    private Location getSeatLocation(Location blockLocation, BlockData data) {
        double yOffset = 0.5;
        if (data instanceof Slab slab) {
            if (slab.getType() == Slab.Type.TOP) {
                yOffset = 1.0;
            } else {
                yOffset = 0.5;
            }
        } else if (data instanceof Stairs stairs) {
            if (stairs.getHalf() == Stairs.Half.TOP) {
                yOffset = 1.0;
            } else {
                yOffset = 0.5;
            }
        }
        return blockLocation.clone().add(0.5, yOffset, 0.5);
    }

    private ArmorStand spawnSeat(Location location) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setInvisible(true);
        stand.setMarker(true);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setSilent(true);
        stand.setVelocity(new Vector(0, 0, 0));
        stand.setCustomNameVisible(false);
        stand.setCustomName(messages.colorize("&7Seat"));
        return stand;
    }
}
