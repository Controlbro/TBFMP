package com.tbfmc.tbfmp.nickname;

import com.tbfmc.tbfmp.util.UnifiedDataFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, String> nicknames = new HashMap<>();

    public NicknameStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "nicknames.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public String getNickname(UUID playerId) {
        return nicknames.get(playerId);
    }

    public void setNickname(UUID playerId, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            nicknames.remove(playerId);
            setValue(playerId, null);
        } else {
            nicknames.put(playerId, nickname);
            setValue(playerId, nickname);
        }
        save();
    }

    public Map<UUID, String> getAllNicknames() {
        return Collections.unmodifiableMap(nicknames);
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
        for (Map.Entry<UUID, String> entry : nicknames.entrySet()) {
            data.set("nicknames." + entry.getKey(), entry.getValue());
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
        org.bukkit.configuration.ConfigurationSection section = data.getConfigurationSection("nicknames");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                String nickname = data.getString("nicknames." + key);
                if (nickname != null && !nickname.isBlank()) {
                    nicknames.put(playerId, nickname);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void setValue(UUID playerId, String nickname) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("nicknames." + playerId, nickname);
            return;
        }
        legacyData.set("nicknames." + playerId, nickname);
    }
}
