package com.tbfmc.tbfmp.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningEventService {
    private static final String OBJECTIVE_NAME = "tbfmp_event";
    private static final String TITLE = ChatColor.GOLD + "Mining Event";
    private final MiningEventStorage storage;
    private final EventSettingsStorage settingsStorage;
    private final ScoreboardManager scoreboardManager;
    private final Map<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final Map<UUID, String> lastEntries = new HashMap<>();

    public MiningEventService(MiningEventStorage storage, EventSettingsStorage settingsStorage) {
        this.storage = storage;
        this.settingsStorage = settingsStorage;
        this.scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void handleBlockMined(Player player, int amount) {
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
        String newEntry = ChatColor.YELLOW + "Mined: " + ChatColor.WHITE + storage.getCount(uuid);
        String oldEntry = lastEntries.put(uuid, newEntry);
        if (oldEntry != null && !oldEntry.equals(newEntry)) {
            scoreboard.resetScores(oldEntry);
        }
        objective.getScore(newEntry).setScore(1);
        player.setScoreboard(scoreboard);
    }

    private void clearScoreboard(Player player) {
        if (scoreboardManager == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Scoreboard scoreboard = scoreboards.remove(uuid);
        lastEntries.remove(uuid);
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
