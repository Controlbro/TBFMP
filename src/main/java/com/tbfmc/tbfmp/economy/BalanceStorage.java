package com.tbfmc.tbfmp.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceStorage {
    private final JavaPlugin plugin;
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, Double> balances = new HashMap<>();

    public BalanceStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.plugin = plugin;
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("balances");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    balances.put(uuid, section.getDouble(key,
                            plugin.getConfig().getDouble("starting-balance", 0.0)));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                balances.put(uuid, legacyData.getDouble(key, plugin.getConfig().getDouble("starting-balance", 0.0)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public double getBalance(UUID uuid) {
        return getBalanceInternal(uuid);
    }

    public void setBalance(UUID uuid, double amount) {
        setBalanceInternal(uuid, amount);
    }

    public void addBalance(UUID uuid, double amount) {
        setBalanceInternal(uuid, getBalanceInternal(uuid) + amount);
    }

    public void subtractBalance(UUID uuid, double amount) {
        setBalanceInternal(uuid, Math.max(0.0, getBalanceInternal(uuid) - amount));
    }

    public Map<UUID, Double> getAllBalances() {
        refreshFromMysqlIfEnabled();
        return Collections.unmodifiableMap(balances);
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
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            data.set("balances." + entry.getKey(), entry.getValue());
        }
    }

    private void setValue(String key, double value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("balances." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    public void reloadFromUnifiedData() {
        balances.clear();
        load();
    }

    private double getBalanceInternal(UUID uuid) {
        return balances.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0));
    }

    private void setBalanceInternal(UUID uuid, double amount) {
        balances.put(uuid, amount);
        setValue(uuid.toString(), amount);
        save();
    }
}
