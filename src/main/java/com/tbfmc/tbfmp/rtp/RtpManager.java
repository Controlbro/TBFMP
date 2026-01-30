package com.tbfmc.tbfmp.rtp;

import com.tbfmc.tbfmp.util.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.tbfmc.tbfmp.util.UnifiedDataFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RtpManager {
    private final JavaPlugin plugin;
    private final MessageService messages;
    private final UnifiedDataFile unifiedDataFile;
    private final File file;
    private final FileConfiguration legacyData;
    private final Set<UUID> usedRtp = new HashSet<>();
    private final Map<UUID, Long> pendingConfirmations = new HashMap<>();

    public RtpManager(JavaPlugin plugin, MessageService messages, UnifiedDataFile unifiedDataFile) {
        this.plugin = plugin;
        this.messages = messages;
        this.unifiedDataFile = unifiedDataFile;
        this.file = new File(plugin.getDataFolder(), "rtp-used.yml");
        this.legacyData = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (unifiedDataFile.isEnabled()) {
            org.bukkit.configuration.ConfigurationSection section =
                    unifiedDataFile.getData().getConfigurationSection("rtp-used");
            if (section == null) {
                return;
            }
            for (String key : section.getKeys(false)) {
                try {
                    usedRtp.add(UUID.fromString(key));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return;
        }
        for (String key : legacyData.getKeys(false)) {
            try {
                usedRtp.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean hasUsedRtp(UUID uuid) {
        return usedRtp.contains(uuid);
    }

    public void requestRtp(Player player) {
        if (hasUsedRtp(player.getUniqueId())) {
            messages.sendMessage(player, messages.getMessage("messages.rtp-used"));
            return;
        }
        pendingConfirmations.put(player.getUniqueId(), System.currentTimeMillis());
        messages.sendMessage(player, messages.getMessage("messages.rtp-pending"));
    }

    public void confirmRtp(Player player) {
        UUID uuid = player.getUniqueId();
        if (!pendingConfirmations.containsKey(uuid)) {
            messages.sendMessage(player, messages.getMessage("messages.rtp-expired"));
            return;
        }

        long requestedAt = pendingConfirmations.get(uuid);
        long timeoutMillis = plugin.getConfig().getLong("rtp.confirm-timeout-seconds", 60) * 1000L;
        if (System.currentTimeMillis() - requestedAt > timeoutMillis) {
            pendingConfirmations.remove(uuid);
            messages.sendMessage(player, messages.getMessage("messages.rtp-expired"));
            return;
        }

        Location location = findSafeLocation();
        if (location == null) {
            messages.sendMessage(player, messages.getMessage("messages.rtp-failed"));
            return;
        }

        pendingConfirmations.remove(uuid);
        usedRtp.add(uuid);
        setValue(uuid.toString(), true);
        save();
        messages.sendMessage(player, messages.getMessage("messages.rtp-success"));
        player.teleportAsync(location);
    }

    public void resetRtp(UUID uuid) {
        usedRtp.remove(uuid);
        pendingConfirmations.remove(uuid);
        setValue(uuid.toString(), null);
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
        usedRtp.clear();
        pendingConfirmations.clear();
        load();
    }

    public void writeToUnifiedData() {
        if (!unifiedDataFile.isEnabled()) {
            return;
        }
        FileConfiguration data = unifiedDataFile.getData();
        for (UUID uuid : usedRtp) {
            data.set("rtp-used." + uuid, true);
        }
    }

    private void setValue(String key, Object value) {
        if (unifiedDataFile.isEnabled()) {
            unifiedDataFile.getData().set("rtp-used." + key, value);
            return;
        }
        legacyData.set(key, value);
    }

    private Location findSafeLocation() {
        String worldName = plugin.getConfig().getString("rtp.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        int minRadius = plugin.getConfig().getInt("rtp.min-radius", 500);
        int maxRadius = plugin.getConfig().getInt("rtp.max-radius", 3000);
        int attempts = plugin.getConfig().getInt("rtp.max-attempts", 20);
        List<String> excluded = plugin.getConfig().getStringList("rtp.excluded-biomes");

        for (int i = 0; i < attempts; i++) {
            int x = randomBetween(minRadius, maxRadius) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            int z = randomBetween(minRadius, maxRadius) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1);
            int y = world.getHighestBlockYAt(x, z);
            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (excluded.contains(world.getBiome(x, y, z).name())) {
                continue;
            }

            Block block = world.getBlockAt(x, y, z);
            if (block.getType().isAir() || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
                continue;
            }

            if (world.getBlockAt(x, y + 1, z).getType().isSolid()) {
                continue;
            }

            return location;
        }
        return null;
    }

    private int randomBetween(int min, int max) {
        if (max <= min) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
