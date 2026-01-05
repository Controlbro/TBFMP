package com.tbfmc.tbfmp.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TagConfig {
    private final File file;
    private final FileConfiguration data;
    private final Map<String, TagDefinition> tags = new LinkedHashMap<>();

    public TagConfig(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "tags.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public void load() {
        tags.clear();
        ConfigurationSection section = data.getConfigurationSection("tags");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            String display = section.getString(key + ".display", "");
            String permission = section.getString(key + ".permission", "");
            tags.put(key, new TagDefinition(key, display, permission));
        }
    }

    public TagDefinition getTag(String id) {
        return tags.get(id);
    }

    public Collection<TagDefinition> getTags() {
        return tags.values();
    }
}
