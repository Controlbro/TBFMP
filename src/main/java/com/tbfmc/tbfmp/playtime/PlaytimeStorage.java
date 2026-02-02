package com.tbfmc.tbfmp.playtime;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlaytimeStorage {
    private final JavaPlugin plugin;
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Long> playtimeSeconds = new HashMap<>();
    private final Map<UUID, Set<String>> claimedRewards = new HashMap<>();

    public PlaytimeStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.plugin = plugin;
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "playtime.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        FileConfiguration data = unifiedDataFile.isEnabled() ? unifiedDataFile.getData() : legacyData;
        ConfigurationSection secondsSection = data.getConfigurationSection("playtime.seconds");
        if (secondsSection != null) {
            for (String key : secondsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    playtimeSeconds.put(uuid, secondsSection.getLong(key, 0L));
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Invalid playtime UUID: " + key);
                }
            }
        }
        ConfigurationSection rewardsSection = data.getConfigurationSection("playtime.claimed");
        if (rewardsSection != null) {
            for (String key : rewardsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    Set<String> rewards = new HashSet<>(rewardsSection.getStringList(key));
                    if (!rewards.isEmpty()) {
                        claimedRewards.put(uuid, rewards);
                    }
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Invalid playtime reward UUID: " + key);
                }
            }
        }
    }

    public long getPlaytimeSeconds(UUID uuid) {
        return playtimeSeconds.getOrDefault(uuid, 0L);
    }

    public void addPlaytimeSeconds(UUID uuid, long seconds) {
        if (seconds <= 0L) {
            return;
        }
        playtimeSeconds.put(uuid, getPlaytimeSeconds(uuid) + seconds);
        setPlaytimeValue(uuid);
    }

    public void setPlaytimeSeconds(UUID uuid, long seconds) {
        playtimeSeconds.put(uuid, Math.max(0L, seconds));
        setPlaytimeValue(uuid);
    }

    public Map<UUID, Long> getAllPlaytimeSeconds() {
        return Collections.unmodifiableMap(playtimeSeconds);
    }

    public boolean hasClaimed(UUID uuid, String rewardId) {
        return claimedRewards.getOrDefault(uuid, Collections.emptySet()).contains(rewardId);
    }

    public void claimReward(UUID uuid, String rewardId) {
        claimedRewards.computeIfAbsent(uuid, key -> new HashSet<>()).add(rewardId);
        setClaimedRewards(uuid);
    }

    public Set<String> getClaimedRewards(UUID uuid) {
        return Collections.unmodifiableSet(claimedRewards.getOrDefault(uuid, Collections.emptySet()));
    }

    public void save() {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.save();
            return;
        }
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            legacyData.save(file);
        } catch (IOException ignored) {
        }
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (Map.Entry<UUID, Long> entry : playtimeSeconds.entrySet()) {
            data.set("playtime.seconds." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UUID, Set<String>> entry : claimedRewards.entrySet()) {
            data.set("playtime.claimed." + entry.getKey(), new java.util.ArrayList<>(entry.getValue()));
        }
    }

    private void setPlaytimeValue(UUID uuid) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("playtime.seconds." + uuid, playtimeSeconds.get(uuid));
            return;
        }
        legacyData.set("playtime.seconds." + uuid, playtimeSeconds.get(uuid));
    }

    private void setClaimedRewards(UUID uuid) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("playtime.claimed." + uuid, new java.util.ArrayList<>(getClaimedRewards(uuid)));
            return;
        }
        legacyData.set("playtime.claimed." + uuid, new java.util.ArrayList<>(getClaimedRewards(uuid)));
    }
}
