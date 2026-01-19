package com.tbfmc.tbfmp.mallwarp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MallWarpManager {
    private final MallWarpService mallWarpService;
    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Set<UUID> mallPlayers = new HashSet<>();

    public MallWarpManager(MallWarpService mallWarpService) {
        this.mallWarpService = mallWarpService;
    }

    public void setWarpLocation(Location location) {
        mallWarpService.setWarpLocation(location);
    }

    public Location getWarpLocation() {
        return mallWarpService.getWarpLocation();
    }

    public void setRegion(MallWarpRegion region) {
        mallWarpService.setRegion(region);
    }

    public MallWarpRegion getRegion() {
        return mallWarpService.getRegion();
    }

    public void enterMall(Player player) {
        UUID playerId = player.getUniqueId();
        backLocations.put(playerId, player.getLocation().clone());
        mallPlayers.add(playerId);
    }

    public boolean isMallPlayer(Player player) {
        return mallPlayers.contains(player.getUniqueId());
    }

    public boolean hasBackLocation(Player player) {
        return backLocations.containsKey(player.getUniqueId());
    }

    public Location exitMall(Player player) {
        UUID playerId = player.getUniqueId();
        mallPlayers.remove(playerId);
        return backLocations.remove(playerId);
    }

    public void clearPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        mallPlayers.remove(playerId);
        backLocations.remove(playerId);
    }
}
