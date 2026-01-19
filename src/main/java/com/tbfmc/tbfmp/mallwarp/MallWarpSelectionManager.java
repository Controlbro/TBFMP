package com.tbfmc.tbfmp.mallwarp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MallWarpSelectionManager {
    private final Map<UUID, Location> firstSelections = new HashMap<>();
    private final Map<UUID, Location> secondSelections = new HashMap<>();

    public void setFirstSelection(Player player, Location location) {
        if (player == null || location == null) {
            return;
        }
        firstSelections.put(player.getUniqueId(), location.clone());
    }

    public void setSecondSelection(Player player, Location location) {
        if (player == null || location == null) {
            return;
        }
        secondSelections.put(player.getUniqueId(), location.clone());
    }

    public Location getFirstSelection(Player player) {
        return player == null ? null : firstSelections.get(player.getUniqueId());
    }

    public Location getSecondSelection(Player player) {
        return player == null ? null : secondSelections.get(player.getUniqueId());
    }
}
