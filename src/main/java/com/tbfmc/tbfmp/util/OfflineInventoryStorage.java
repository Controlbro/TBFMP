package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class OfflineInventoryStorage {
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Map<UUID, ItemStack[]> inventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> enderChests = new HashMap<>();

    public OfflineInventoryStorage(JavaPlugin plugin, UnifiedDataFile unifiedDataFile) {
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "offline-inventories.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("offline-inventories");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String inventoryData = section.getString(key + ".inventory", "");
                    String enderData = section.getString(key + ".enderchest", "");
                    if (!inventoryData.isEmpty()) {
                        inventories.put(uuid, decode(inventoryData));
                    }
                    if (!enderData.isEmpty()) {
                        enderChests.put(uuid, decode(enderData));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String inventoryData = legacyData.getString(key + ".inventory", "");
                String enderData = legacyData.getString(key + ".enderchest", "");
                if (!inventoryData.isEmpty()) {
                    inventories.put(uuid, decode(inventoryData));
                }
                if (!enderData.isEmpty()) {
                    enderChests.put(uuid, decode(enderData));
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean hasInventory(UUID uuid) {
        return inventories.containsKey(uuid);
    }

    public boolean hasEnderChest(UUID uuid) {
        return enderChests.containsKey(uuid);
    }

    public ItemStack[] getInventory(UUID uuid, int size) {
        ItemStack[] stored = inventories.getOrDefault(uuid, new ItemStack[size]);
        if (stored.length == size) {
            return stored.clone();
        }
        ItemStack[] resized = new ItemStack[size];
        System.arraycopy(stored, 0, resized, 0, Math.min(stored.length, size));
        return resized;
    }

    public ItemStack[] getEnderChest(UUID uuid, int size) {
        ItemStack[] stored = enderChests.getOrDefault(uuid, new ItemStack[size]);
        if (stored.length == size) {
            return stored.clone();
        }
        ItemStack[] resized = new ItemStack[size];
        System.arraycopy(stored, 0, resized, 0, Math.min(stored.length, size));
        return resized;
    }

    public void setInventory(UUID uuid, ItemStack[] items) {
        inventories.put(uuid, items.clone());
        setValue(uuid + ".inventory", encode(items));
        save();
    }

    public void setEnderChest(UUID uuid, ItemStack[] items) {
        enderChests.put(uuid, items.clone());
        setValue(uuid + ".enderchest", encode(items));
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

    public void saveWithMessage(Logger logger, String reason) {
        save();
        if (logger != null) {
            logger.info("Offline inventories saved (" + reason + ").");
        }
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (UUID uuid : inventories.keySet()) {
            data.set("offline-inventories." + uuid + ".inventory", encode(inventories.get(uuid)));
        }
        for (UUID uuid : enderChests.keySet()) {
            data.set("offline-inventories." + uuid + ".enderchest", encode(enderChests.get(uuid)));
        }
    }

    private void setValue(String key, String value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("offline-inventories." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    private String encode(ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException ignored) {
            return "";
        }
    }

    private ItemStack[] decode(String dataString) {
        byte[] data = Base64.getDecoder().decode(dataString);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (IOException | ClassNotFoundException ignored) {
            return new ItemStack[0];
        }
    }
}
