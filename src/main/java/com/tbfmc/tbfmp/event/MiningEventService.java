package com.tbfmc.tbfmp.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MiningEventService {
    private static final String OBJECTIVE_NAME = "oakglowutil_event";
    private static final String TITLE = ChatColor.GOLD + "Mining Event";
    private static final int MAX_ENTRIES = 15;
    private final MiningEventStorage storage;
    private final EventSettingsStorage settingsStorage;
    private final org.bukkit.plugin.java.JavaPlugin plugin;
    private final ScoreboardManager scoreboardManager;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private boolean enabled;
    private final Set<Material> trackedBlocks = EnumSet.noneOf(Material.class);

    public MiningEventService(org.bukkit.plugin.java.JavaPlugin plugin, MiningEventStorage storage,
                              EventSettingsStorage settingsStorage) {
        this.plugin = plugin;
        this.storage = storage;
        this.settingsStorage = settingsStorage;
        this.scoreboardManager = Bukkit.getScoreboardManager();
        reloadSettings();
    }

    public void handleBlockMined(Player player, int amount) {
        if (!enabled) {
            return;
        }
        storage.addCount(player.getUniqueId(), amount);
        if (settingsStorage.isEnabled(player.getUniqueId())) {
            updateScoreboard(player);
        }
    }

    public void showScoreboard(Player player) {
        settingsStorage.setEnabled(player.getUniqueId(), true);
        updateScoreboard(player);
    }

    public void hideScoreboard(Player player) {
        settingsStorage.setEnabled(player.getUniqueId(), false);
        clearScoreboard(player);
    }

    public void applyScoreboard(Player player) {
        if (!enabled) {
            clearScoreboard(player);
            return;
        }
        if (settingsStorage.isEnabled(player.getUniqueId())) {
            updateScoreboard(player);
        } else {
            clearScoreboard(player);
        }
    }

    public void applyToOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyScoreboard(player);
        }
    }

    public boolean isEventEnabled() {
        return enabled;
    }

    public boolean isBlockTracked(Material material) {
        return trackedBlocks.contains(material);
    }

    public void reloadSettings() {
        FileConfiguration config = plugin.getConfig();
        enabled = config.getBoolean("event.enabled", true);
        trackedBlocks.clear();
        for (String blockName : config.getStringList("event.acceptedblocks")) {
            Material material = Material.matchMaterial(blockName);
            if (material != null) {
                trackedBlocks.add(material);
            }
        }
    }

    public boolean toggleLeaderboard(Player player) {
        boolean enabledSetting = settingsStorage.toggle(player.getUniqueId());
        if (!enabled) {
            clearScoreboard(player);
            return enabledSetting;
        }
        if (enabledSetting) {
            updateScoreboard(player);
        } else {
            clearScoreboard(player);
        }
        return enabledSetting;
    }

    public void resetEvent() {
        storage.reset();
        if (!enabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                clearScoreboard(player);
            }
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (settingsStorage.isEnabled(player.getUniqueId())) {
                updateScoreboard(player);
            } else {
                clearScoreboard(player);
            }
        }
    }

    private void updateScoreboard(Player player) {
        if (scoreboardManager == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Scoreboard scoreboard = scoreboards.computeIfAbsent(uuid, key -> createScoreboard());
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, TITLE);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        int entriesAdded = 0;
        for (Map.Entry<UUID, Integer> entry : storage.getAllCounts().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((left, right) -> Integer.compare(right.getValue(), left.getValue()))
                .toList()) {
            if (entriesAdded >= MAX_ENTRIES) {
                break;
            }
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            if (name == null || name.isBlank()) {
                name = entry.getKey().toString().substring(0, 8);
            }
            String displayName = ChatColor.YELLOW + name;
            objective.getScore(displayName).setScore(entry.getValue());
            entriesAdded++;
        }
        player.setScoreboard(scoreboard);
    }

    private void clearScoreboard(Player player) {
        if (scoreboardManager == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Scoreboard scoreboard = scoreboards.remove(uuid);
        if (scoreboard != null && player.getScoreboard().equals(scoreboard)) {
            player.setScoreboard(scoreboardManager.getMainScoreboard());
        }
    }

    private Scoreboard createScoreboard() {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, TITLE);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return scoreboard;
    }
}
