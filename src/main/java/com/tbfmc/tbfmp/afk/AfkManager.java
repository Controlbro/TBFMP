package com.tbfmc.tbfmp.afk;

import com.tbfmc.tbfmp.tablist.TabListService;
import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AfkManager {
    private final Map<UUID, Long> lastMovement = new HashMap<>();
    private final Set<UUID> afkPlayers = new HashSet<>();
    private long timeoutMillis;
    private MessageService messages;
    private TabListService tabListService;

    public AfkManager(long timeoutMillis, MessageService messages, TabListService tabListService) {
        this.timeoutMillis = timeoutMillis;
        this.messages = messages;
        this.tabListService = tabListService;
    }

    public void updateSettings(long timeoutMillis, MessageService messages, TabListService tabListService) {
        this.timeoutMillis = timeoutMillis;
        this.messages = messages;
        this.tabListService = tabListService;
    }

    public void initialize(Player player) {
        lastMovement.put(player.getUniqueId(), System.currentTimeMillis());
        tabListService.updatePlayer(player, false);
    }

    public void remove(Player player) {
        lastMovement.remove(player.getUniqueId());
        afkPlayers.remove(player.getUniqueId());
    }

    public boolean isAfk(UUID uuid) {
        return afkPlayers.contains(uuid);
    }

    public void recordMovement(Player player) {
        lastMovement.put(player.getUniqueId(), System.currentTimeMillis());
        if (isAfk(player.getUniqueId())) {
            setAfk(player, false, true);
        }
    }

    public void toggleAfk(Player player) {
        if (isAfk(player.getUniqueId())) {
            setAfk(player, false, true);
        } else {
            setAfk(player, true, true);
        }
    }

    public void checkAfk() {
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            lastMovement.putIfAbsent(uuid, now);
            if (isAfk(uuid)) {
                continue;
            }
            long lastMove = lastMovement.get(uuid);
            if (now - lastMove >= timeoutMillis) {
                setAfk(player, true, true);
            }
        }
    }

    private void setAfk(Player player, boolean afk, boolean notify) {
        UUID uuid = player.getUniqueId();
        if (afk) {
            afkPlayers.add(uuid);
            if (notify) {
                messages.sendMessage(player, messages.getMessage("messages.afk-enabled"));
            }
        } else {
            afkPlayers.remove(uuid);
            lastMovement.put(uuid, System.currentTimeMillis());
            if (notify) {
                messages.sendMessage(player, messages.getMessage("messages.afk-disabled"));
            }
        }
        tabListService.updatePlayer(player, afk);
    }
}
