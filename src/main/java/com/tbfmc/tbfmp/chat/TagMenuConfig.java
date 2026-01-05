package com.tbfmc.tbfmp.chat;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TagMenuConfig {
    private final FileConfiguration data;

    public TagMenuConfig(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "tag-menu.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public String getTitle() {
        return data.getString("menu.title", "&2Tags");
    }

    public int getSize() {
        int size = data.getInt("menu.size", 27);
        if (size < 9) {
            return 9;
        }
        return Math.min(size, 54);
    }

    public Material getItemMaterial() {
        String materialName = data.getString("menu.item-material", "NAME_TAG");
        Material material = Material.matchMaterial(materialName);
        return material != null ? material : Material.NAME_TAG;
    }

    public List<String> getLore() {
        return new ArrayList<>(data.getStringList("menu.lore"));
    }

    public List<String> getSelectedLore() {
        return new ArrayList<>(data.getStringList("menu.selected-lore"));
    }

    public List<String> getNoPermissionLore() {
        return new ArrayList<>(data.getStringList("menu.no-permission-lore"));
    }

    public String getPreviewMessage() {
        return data.getString("menu.preview-message", "&fHello!");
    }

    public Material getNavigationMaterial() {
        String materialName = data.getString("menu.navigation-material", "ARROW");
        Material material = Material.matchMaterial(materialName);
        return material != null ? material : Material.ARROW;
    }

    public int getPreviousPageSlot(int size) {
        int slot = data.getInt("menu.previous-page-slot", size - 9);
        return Math.max(0, Math.min(slot, size - 1));
    }

    public int getNextPageSlot(int size) {
        int slot = data.getInt("menu.next-page-slot", size - 1);
        return Math.max(0, Math.min(slot, size - 1));
    }

    public String getPreviousPageName() {
        return data.getString("menu.previous-page-name", "&aPrevious Page");
    }

    public String getNextPageName() {
        return data.getString("menu.next-page-name", "&aNext Page");
    }
}
