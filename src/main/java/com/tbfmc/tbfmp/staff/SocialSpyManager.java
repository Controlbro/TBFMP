package com.tbfmc.tbfmp.staff;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialSpyManager {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Set<UUID> enabledPlayers = new HashSet<>();

    public SocialSpyManager(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "socialspy.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public boolean isEnabled(UUID playerId) {
        return enabledPlayers.contains(playerId);
    }

    public boolean toggle(UUID playerId) {
        boolean enabled = !enabledPlayers.contains(playerId);
        setEnabled(playerId, enabled);
        return enabled;
    }

    public void setEnabled(UUID playerId, boolean enabled) {
        if (enabled) {
            enabledPlayers.add(playerId);
        } else {
            enabledPlayers.remove(playerId);
        }
        setValue(playerId.toString(), enabled ? true : null);
        save();
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

    public void reloadFromUnifiedData() {
        enabledPlayers.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (UUID playerId : enabledPlayers) {
            data.set("socialspy." + playerId, true);
        }
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            loadFromConfig(unifiedDataFile.getData());
            return;
        }
        loadFromConfig(legacyData);
    }

    private void loadFromConfig(FileConfiguration data) {
        org.bukkit.configuration.ConfigurationSection section = data.getConfigurationSection("socialspy");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                if (data.getBoolean("socialspy." + key, false)) {
                    enabledPlayers.add(playerId);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void setValue(String key, Object value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("socialspy." + key, value);
            return;
        }
        legacyData.set("socialspy." + key, value);
    }
}
