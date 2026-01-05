package com.tbfmc.tbfmp.sit;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SitManager {
    private final MessageService messages;
    private final Map<UUID, ArmorStand> seats = new HashMap<>();

    public SitManager(MessageService messages) {
        this.messages = messages;
    }

    public boolean sitOnGround(Player player) {
        if (player.isInsideVehicle()) {
            return false;
        }
        if (!player.isOnGround()) {
            return false;
        }
        Location seatLocation = getGroundSeatLocation(player.getLocation());
        ArmorStand stand = spawnSeat(seatLocation);
        seats.put(player.getUniqueId(), stand);
        stand.addPassenger(player);
        return true;
    }

    public boolean sitOnBlock(Player player, Block block, BlockData data) {
        if (player.isInsideVehicle()) {
            return false;
        }
        Location seatLocation = getSeatLocation(block.getLocation(), data);
        if (seatLocation == null) {
            return false;
        }
        ArmorStand stand = spawnSeat(seatLocation);
        seats.put(player.getUniqueId(), stand);
        stand.addPassenger(player);
        return true;
    }

    public boolean canSit(Player player) {
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

    public void removeSeat(UUID uuid) {
        ArmorStand stand = seats.remove(uuid);
        if (stand != null) {
            stand.remove();
        }
    }

    public void removeSeat(UUID uuid, ArmorStand stand) {
        seats.remove(uuid);
        stand.remove();
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

    private Location getGroundSeatLocation(Location location) {
        Location base = location.getBlock().getLocation();
        return base.add(0.5, 0.1, 0.5);
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
