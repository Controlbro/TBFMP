package com.tbfmc.tbfmp.settings;

import org.bukkit.Material;

import java.util.List;

public class SettingsMenuItem {
    private final int slot;
    private final Material material;
    private final String name;
    private final List<String> lore;

    public SettingsMenuItem(int slot, Material material, String name, List<String> lore) {
        this.slot = slot;
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }
}
