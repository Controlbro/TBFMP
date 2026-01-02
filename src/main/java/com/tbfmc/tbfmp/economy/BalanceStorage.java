package com.tbfmc.tbfmp.economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BalanceStorage {
    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, Double> balances = new HashMap<>();

    public BalanceStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                balances.put(uuid, data.getDouble(key, plugin.getConfig().getDouble("starting-balance", 0.0)));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, plugin.getConfig().getDouble("starting-balance", 0.0));
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, amount);
        data.set(uuid.toString(), amount);
        save();
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void subtractBalance(UUID uuid, double amount) {
        setBalance(uuid, Math.max(0.0, getBalance(uuid) - amount));
    }

    public Map<UUID, Double> getAllBalances() {
        return Collections.unmodifiableMap(balances);
    }

    public void save() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            data.save(file);
        } catch (IOException ignored) {
        }
    }
}
