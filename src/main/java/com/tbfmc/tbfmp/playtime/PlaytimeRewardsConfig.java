package com.tbfmc.tbfmp.playtime;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaytimeRewardsConfig {
    private final File file;
    private FileConfiguration data;

    public PlaytimeRewardsConfig(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "playtime-rewards.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public String getTitle() {
        return data.getString("menu.title", "&2Playtime Rewards");
    }

    public int getSize() {
        int size = data.getInt("menu.size", 27);
        if (size < 9) {
            return 9;
        }
        return Math.min(size, 54);
    }

    public Material getFillerMaterial() {
        String materialName = data.getString("menu.filler.material", "GRAY_STAINED_GLASS_PANE");
        Material material = Material.matchMaterial(materialName);
        return material != null ? material : Material.GRAY_STAINED_GLASS_PANE;
    }

    public String getFillerName() {
        return data.getString("menu.filler.name", " ");
    }

    public String getStatusLocked() {
        return data.getString("menu.status.locked", "&cLocked");
    }

    public String getStatusClaimable() {
        return data.getString("menu.status.claimable", "&aClaimable");
    }

    public String getStatusClaimed() {
        return data.getString("menu.status.claimed", "&7Claimed");
    }

    public Map<String, PlaytimeReward> getRewards() {
        ConfigurationSection rewardsSection = data.getConfigurationSection("rewards");
        if (rewardsSection == null) {
            return Collections.emptyMap();
        }
        Map<String, PlaytimeReward> rewards = new LinkedHashMap<>();
        for (String key : rewardsSection.getKeys(false)) {
            ConfigurationSection section = rewardsSection.getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            int slot = section.getInt("slot", -1);
            if (slot < 0) {
                continue;
            }
            String materialName = section.getString("material", "STONE");
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                material = Material.STONE;
            }
            long requiredSeconds = section.getLong("required-seconds", 0L);
            String name = section.getString("name", "&f" + key);
            List<String> lore = new ArrayList<>(section.getStringList("lore"));
            List<String> commands = new ArrayList<>(section.getStringList("commands"));
            rewards.put(key, new PlaytimeReward(key, slot, requiredSeconds, material, name, lore, commands));
        }
        return rewards;
    }
}
