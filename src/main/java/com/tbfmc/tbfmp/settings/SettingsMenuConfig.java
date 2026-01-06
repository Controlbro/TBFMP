package com.tbfmc.tbfmp.settings;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsMenuConfig {
    private final FileConfiguration data;

    public SettingsMenuConfig(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "settings-menu.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public String getTitle() {
        return data.getString("menu.title", "&2Settings");
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

    public String getStatusEnabled() {
        return data.getString("menu.status-enabled", "&aEnabled");
    }

    public String getStatusDisabled() {
        return data.getString("menu.status-disabled", "&cDisabled");
    }

    public SettingsMenuItem getItem(String key) {
        ConfigurationSection section = data.getConfigurationSection("items." + key);
        if (section == null) {
            return null;
        }
        int slot = section.getInt("slot", -1);
        if (slot < 0) {
            return null;
        }
        String materialName = section.getString("material", "STONE");
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            material = Material.STONE;
        }
        String name = section.getString("name", "&f" + key);
        List<String> lore = new ArrayList<>(section.getStringList("lore"));
        return new SettingsMenuItem(slot, material, name, lore);
    }
}
