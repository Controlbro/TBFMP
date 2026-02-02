package com.tbfmc.tbfmp.playtime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeTracker {
    private final PlaytimeStorage playtimeStorage;
    private final Map<UUID, Long> sessionStart = new HashMap<>();

    public PlaytimeTracker(PlaytimeStorage playtimeStorage) {
        this.playtimeStorage = playtimeStorage;
    }

    public void startSession(Player player) {
        sessionStart.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void endSession(Player player) {
        UUID uuid = player.getUniqueId();
        Long start = sessionStart.remove(uuid);
        if (start == null) {
            return;
        }
        long seconds = Math.max(0L, (System.currentTimeMillis() - start) / 1000L);
        playtimeStorage.addPlaytimeSeconds(uuid, seconds);
        playtimeStorage.save();
    }

    public long getTotalPlaytimeSeconds(UUID uuid) {
        long base = playtimeStorage.getPlaytimeSeconds(uuid);
        Long start = sessionStart.get(uuid);
        if (start == null) {
            return base;
        }
        long current = Math.max(0L, (System.currentTimeMillis() - start) / 1000L);
        return base + current;
    }

    public void flushOnlineSessions() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Long start = sessionStart.get(uuid);
            if (start == null) {
                startSession(player);
                continue;
            }
            long seconds = Math.max(0L, (System.currentTimeMillis() - start) / 1000L);
            if (seconds > 0L) {
                playtimeStorage.addPlaytimeSeconds(uuid, seconds);
                sessionStart.put(uuid, System.currentTimeMillis());
            }
        }
        playtimeStorage.save();
    }
}
