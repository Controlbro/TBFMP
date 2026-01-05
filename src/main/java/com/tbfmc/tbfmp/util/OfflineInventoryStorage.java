package com.tbfmc.tbfmp.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class OfflineInventoryStorage {
    private final File file;
    private final FileConfiguration data;
    private final Map<UUID, ItemStack[]> inventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> enderChests = new HashMap<>();

    public OfflineInventoryStorage(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "offline-inventories.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        for (String key : data.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String inventoryData = data.getString(key + ".inventory", "");
                String enderData = data.getString(key + ".enderchest", "");
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
        data.set(uuid + ".inventory", encode(items));
        save();
    }

    public void setEnderChest(UUID uuid, ItemStack[] items) {
        enderChests.put(uuid, items.clone());
        data.set(uuid + ".enderchest", encode(items));
        save();
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
